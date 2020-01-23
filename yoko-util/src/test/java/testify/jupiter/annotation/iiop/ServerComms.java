/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package testify.jupiter.annotation.iiop;

import org.junit.platform.commons.support.ReflectionSupport;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import testify.bus.Bus;
import testify.bus.FieldRef;
import testify.bus.MethodRef;
import testify.bus.TypeRef;
import testify.bus.VoidRef;
import testify.parts.PartRunner;
import testify.util.Maps;
import testify.util.Stack;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testify.bus.LogLevel.ERROR;
import static testify.util.Reflect.newMatchingInstance;

final class ServerComms implements Serializable {
    private enum LifeCycle implements VoidRef {STARTED, STOP}
    private enum Invocation implements MethodRef {INVOKE}
    private enum Instantiation implements FieldRef {INSTANTIATE}
    private enum Result implements TypeRef<Throwable> { RESULT;}
    private static String METHOD_COUNT_PREFIX = "Method#";

    private final String serverName;
    private final Properties props;
    private final String[] args;
    private transient Bus bus;
    /** This is initialized to true in the constructor but will be false when de-serialized. */
    private transient final boolean inClient;
    private transient int methodCount;

    ServerComms(String serverName, Properties props, String[] args) {
        this.serverName = serverName;
        this.props = props;
        this.args = args;
        this.inClient = true;
    }

    private void assertClientSide() { assertTrue(inClient, () -> Stack.getCallingFrame(1) + " must only be used on the client");}
    private void assertServerSide() { assertFalse(inClient, () -> Stack.getCallingFrame(1) + " must only be used on the server");}

    public void launch(PartRunner runner) {
        assertClientSide();
        this.bus = runner.bus(serverName);
        runner.fork(serverName, this::run, bus -> bus.put(LifeCycle.STOP));
        // wait for the server to be ready
        bus.get(LifeCycle.STARTED);

    }

    private void stop(Bus bus, ORB orb) {
        assertServerSide();
        try {
            bus.log(ERROR, "Calling orb.shutdown(true)");
            orb.shutdown(true);
            bus.log("ORB shutdown complete, calling orb.destroy()");
            orb.destroy();
            bus.log("orb.destroy() returned");
        } catch (BAD_INV_ORDER e) {
            // The ORB is sometimes already shut down.
            // This should not cause an error in the test.
            // TODO: find out how this happens
            if (e.minor == 4) return;
            throw e;
        }
    }

    private void run(Bus bus) throws Exception {
        assertServerSide();
        ORB orb = ORB.init(args, props);
        // register the stop method
        bus.onMsg(LifeCycle.STOP, nul -> stop(bus, orb));
        // register the invocation handler
        final Map<Class<?>, Object> params = Maps.of(Bus.class, bus, ORB.class, orb);
        bus.onMsg(Invocation.INVOKE, m -> invoke(m, params, bus));
        bus.onMsg(Instantiation.INSTANTIATE, f -> instantiate(f, params, orb, bus));
        // tell the client we are ready
        bus.put(LifeCycle.STARTED);

        // Give up control to the ORB
        orb.run();
        bus.log("orb.run() completed.");
    }

    private void invoke(Method m, Map<Class<?>, Object> paramMap, Bus bus) {
        assertServerSide();
        final String requestId = getNextRequestId();
        try {
            ReflectionSupport.invokeMethod(m, null, Stream.of(m.getParameterTypes())
                    .map(paramMap::get)
                    .collect(toList())
                    .toArray(new Object[0]));
            // on successful completion, send back a null
            bus.put(Result.RESULT, null);
            bus.put(requestId, m + " completed normally");
        } catch (Throwable t) {
            // if there was an error, send that back instead
            bus.put(Result.RESULT, t);
            bus.put(requestId, m + " completed abnormally with exception " + t);
        }
    }

    private <IMPL extends Remote & org.omg.CORBA.Object, TIE extends Servant & Tie> void instantiate(Field f, Map<Class<?>, Object> paramMap, ORB orb, Bus bus) {
        assertServerSide();
        final String requestId = getNextRequestId();
        try {
            IMPL o = newMatchingInstance(f.getType(), "*Impl", paramMap);
            // set the static field to hold the new object
            f.set(null, o);
            // create the tie
            if (!!! (o instanceof PortableRemoteObject)) {
                PortableRemoteObject.exportObject(o);
            }
            TIE tie = (TIE)Util.getTie(o);
            if (tie == null) {
                // try creating the tie directly
                tie = newMatchingInstance(f.getType(), "_*Impl_Tie");
            }
            tie.setTarget(o);
            // do the POA things
            POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPoa.the_POAManager().activate();
            rootPoa.activate_object(tie);
            // put the IOR on the bus
            String ior = orb.object_to_string(tie.thisObject());
            bus.put(f.getName(), ior);
            // on successful completion, send back a null
            bus.put(Result.RESULT, null);
            bus.put(requestId, f + " instantiated normally");
        } catch (Throwable t) {
            // if there was an error, send that back instead
            bus.put(Result.RESULT, t);
            bus.put(requestId, f + " failed instantiation with exception " + t);
        }
    }


    private String getNextRequestId() {
        methodCount++;
        return METHOD_COUNT_PREFIX + methodCount;
    }

    void invoke(Method m) throws ServerCommsException {
        assertClientSide();
        final String requestId = getNextRequestId();
        // request invocation of the method
        bus.put(Invocation.INVOKE, m);
        // wait for the method to complete
        String info = bus.get(requestId);
        final Throwable result = bus.get(Result.RESULT);
        if (result == null) return;
        throw new MethodInvocationFailed(m, result);
    }

    void instantiate(Field f) throws ServerCommsException {
        assertClientSide();
        final String requestId = getNextRequestId();
        bus.put(Instantiation.INSTANTIATE, f);
        String info = bus.get(requestId);
        final Throwable result = bus.get(Result.RESULT);
        if (result == null) return;
        throw new FieldInstantiationFailed(f, result);
    }

    static class ServerCommsException extends RuntimeException {
        ServerCommsException(Throwable cause) { super(cause); }
        ServerCommsException(String message, Throwable cause) { super(message, cause); }
    }

    static class MethodInvocationFailed extends ServerCommsException {
        MethodInvocationFailed(Method m, Throwable cause) { super("Received exception from server while trying to invoke method:\n    " + m, cause); }
    }

    static class FieldInstantiationFailed extends ServerCommsException {
        FieldInstantiationFailed(Field f, Throwable cause) { super("Received exception from server while trying to instantiate field:\n    " + f, cause); }
    }
}

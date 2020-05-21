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

import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.support.ReflectionSupport;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import testify.bus.Bus;
import testify.bus.EnumRef;
import testify.bus.FieldRef;
import testify.bus.MethodRef;
import testify.bus.TypeRef;
import testify.parts.PartRunner;
import testify.util.Maps;
import testify.util.Stack;
import testify.util.Throw;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testify.util.Reflect.newMatchingInstance;

final class ServerComms implements Serializable {
    private enum ServerOp {START_SERVER, STOP_SERVER, KILL_SERVER}
    private enum ServerRequest implements EnumRef<ServerOp> {SEND}
    private enum MethodRequest implements MethodRef {SEND}
    private enum FieldRequest implements FieldRef {INIT}
    private enum Result implements TypeRef<Throwable> {RESULT}

    private static String REQUEST_COUNT_PREFIX = "Request#";
    private final String serverName;
    private final Properties props;
    private final String[] args;
    private transient Bus bus;
    /** The constructor initializes this to true but will be false when de-serialized. */
    private transient final boolean inClient;
    private transient int methodCount;

    private transient ServerInstance server;
    private transient CountDownLatch serverShutdown;

    class ServerInstance {
        final ORB orb;
        final Map<Class<?>, Object> paramMap;
        private ServerInstance() {
            this.orb = ORB.init(args, props);
            this.paramMap = Maps.of(ORB.class, orb, Bus.class, bus);
        }

        void stop() {
            try {
                bus.log("Calling orb.shutdown(true)");
                orb.shutdown(true);
                bus.log("ORB shutdown complete, calling orb.destroy()");
                orb.destroy();
                bus.log("orb.destroy() returned");
            } catch (BAD_INV_ORDER e) {
                // The ORB is sometimes already shut down.
                // This should not cause an error in the test.
                // TODO: find out how this happens
                if (e.minor != 4) throw e;
            }
        }
    }

    ServerComms(String serverName, Properties props, String[] args) {
        this.inClient = true;
        this.serverName = serverName;
        this.props = props;
        this.args = args;
    }

    public ServerControl getServerControl() {
        return new ServerControl(){
            public void start() { control(ServerOp.START_SERVER); }
            public void stop() { control(ServerOp.STOP_SERVER); }
        };
    }

    private void assertClientSide() { assertTrue(inClient, () -> Stack.getCallingFrame(1) + " must only be used on the client");}
    private void assertServerSide() { assertFalse(inClient, () -> Stack.getCallingFrame(1) + " must only be used on the server"); }

    private static final boolean IS_STARTED = true;
    private static final boolean IS_STOPPED = false;
    private void assertServer(boolean serverShouldBeStarted) {
        assertServerSide();
        if (serverShouldBeStarted)
            assertNotNull(server, "Server not started");
        else
            assertNull(server, "Server already started");
    }

    public void launch(PartRunner runner) {
        assertClientSide();
        this.bus = runner.bus(serverName);
        runner.fork(serverName, this::run, bus -> control(ServerOp.KILL_SERVER));
        // wait for the server to be ready:
        // server side will respond as request 0
        waitForCompletion(ServerLaunchFailed::new);
    }

    private void run(Bus bus) throws Exception {
        assertServerSide();
        this.bus = bus;
        // treat the server launch as request 0
        completeRequest("launch of server ", this::launch0, serverName);
        bus.log("Awaiting server shutdown");
        this.serverShutdown.await();
        bus.log("Received server shutdown");
    }

    private void launch0(String name) {
        this.bus.log("Server started");
        this.serverShutdown = new CountDownLatch(1);
        // register the control, method invocation, and field instantiation handlers
        this.bus.onMsg(ServerRequest.SEND, op -> completeRequest("control operation ", this::control0, op));
        this.bus.onMsg(FieldRequest.INIT, field -> completeRequest("instantiation of field " , this::instantiate0, field));
        this.bus.onMsg(MethodRequest.SEND, method -> completeRequest("invocation of ", this::invoke0, method));
    }

    private void control(ServerOp op) throws ControlOperationFailed {
        assertClientSide();
        bus.put(ServerRequest.SEND, op);
        waitForCompletion(t -> new ControlOperationFailed(op, t));
    }

    private void control0(ServerOp op) {
        switch (op) {
        case START_SERVER:
            assertServer(IS_STOPPED);
            assertNull(server, "Server already started");
            server = new ServerInstance();
            break;
        case STOP_SERVER:
            assertServer(IS_STARTED);
            server.stop();
            server = null;
            break;
        case KILL_SERVER:
            bus.log("killing server");
            if (server != null) server.stop();
            serverShutdown.countDown();
            break;
        default:
            throw (Error)Assertions.fail("Unknown op type: " + op);
        }
    }

    void invoke(Method m) throws MethodInvocationFailed {
        assertClientSide();
        bus.put(MethodRequest.SEND, m);
        waitForCompletion(t -> new MethodInvocationFailed(m, t));
    }

    private Object invoke0(Method m) {
        assertServer(IS_STARTED);
        final Object[] params = Stream.of(m.getParameterTypes())
                .map(server.paramMap::get)
                .collect(toList())
                .toArray(new Object[0]);
        return ReflectionSupport.invokeMethod(m, null, params);
    }

    void instantiate(Field f) throws FieldInstantiationFailed {
        assertClientSide();
        bus.put(FieldRequest.INIT, f);
        waitForCompletion(t -> new FieldInstantiationFailed(f, t));
    }

    private <IMPL extends Remote & org.omg.CORBA.Object, TIE extends Servant & Tie>
    void instantiate0(Field f) {
        assertServer(IS_STARTED);
        try {
            IMPL o = newMatchingInstance(f.getType(), "*Impl", server.paramMap);
            // set the static field to hold the new object
            f.set(null, o);
            // create the tie
            if (!!!(o instanceof PortableRemoteObject)) {
                PortableRemoteObject.exportObject(o);
            }
            TIE tie = (TIE) Util.getTie(o);
            if (tie == null) {
                // try creating the tie directly
                tie = newMatchingInstance(f.getType(), "_*Impl_Tie");
            }
            tie.setTarget(o);
            // do the POA things
            POA rootPoa = POAHelper.narrow(server.orb.resolve_initial_references("RootPOA"));
            rootPoa.the_POAManager().activate();
            rootPoa.activate_object(tie);
            // put the IOR on the bus
            String ior = server.orb.object_to_string(tie.thisObject());
            bus.put(f.getName(), ior);
        } catch (Throwable throwable) {
            throw Throw.andThrowAgain(throwable);
        }
    }

    private <T extends Throwable> void waitForCompletion(Function<Throwable, T> wrapper) throws T {
        assertClientSide();
        final String requestId = getNextRequestId();
        bus.log("Waiting for completion of " + requestId);
        String info = bus.get(requestId);
        bus.log(requestId + ": " + info);
        final Throwable result = bus.get(Result.RESULT);
        if (result != null) throw wrapper.apply(result);
    }

    private <T> void completeRequest(String prefix, Consumer<T> consumer, T parameter) {
        assertServerSide();
        final String requestId = getNextRequestId();
        try {
            bus.log("Invoking " + requestId);
            consumer.accept(parameter);
            // on successful completion, send back a null
            bus.put(Result.RESULT, null);
            bus.put(requestId, prefix + parameter + " completed normally");
        } catch (Throwable e) {
            // if there was an error, send that back instead
            bus.put(Result.RESULT, e);
            bus.put(requestId, prefix + parameter + " completed abnormally with exception " + e);
        }
    }

    private String getNextRequestId() {
        return REQUEST_COUNT_PREFIX + methodCount++;
    }

    static class ServerCommsException extends RuntimeException {
        ServerCommsException(String message, Throwable cause) { super(message, cause); }
    }

    static class ServerLaunchFailed extends ServerCommsException {
        ServerLaunchFailed(Throwable cause) { super("Server launch failed", cause); }
    }

    static class ControlOperationFailed extends ServerCommsException {
        ControlOperationFailed(ServerOp op, Throwable cause) { super("Received exception from server while trying to issue " + op + " request", cause); }
    }

    static class MethodInvocationFailed extends ServerCommsException {
        MethodInvocationFailed(Method m, Throwable cause) { super("Received exception from server while trying to invoke method:\n    " + m, cause); }
    }

    static class FieldInstantiationFailed extends ServerCommsException {
        FieldInstantiationFailed(Field f, Throwable cause) { super("Received exception from server while trying to instantiate field:\n    " + f, cause); }
    }
}

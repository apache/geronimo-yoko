/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.jupiter.annotation.iiop;

import org.junit.platform.commons.support.ReflectionSupport;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.ORB;
import testify.bus.Bus;
import testify.bus.MethodRef;
import testify.bus.TypeRef;
import testify.bus.VoidRef;
import testify.parts.PartRunner;
import testify.util.Maps;
import testify.util.Stack;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testify.bus.LogLevel.ERROR;

final class ServerComms implements Serializable {
    private enum LifeCycle implements VoidRef {STARTED, STOP}
    private enum Invocation implements MethodRef {INVOKE}
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
            bus.put(requestId, m + " completed abnormally with exception " + t.toString());
        } finally {
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

    static class ServerCommsException extends RuntimeException {
        ServerCommsException(Throwable cause) { super(cause); }
        ServerCommsException(String message, Throwable cause) { super(message, cause); }
    }

    static class MethodInvocationFailed extends ServerCommsException {
        MethodInvocationFailed(Method m, Throwable cause) { super("Received exception from server while trying to invoke method:\n    " + m, cause); }
    }
}

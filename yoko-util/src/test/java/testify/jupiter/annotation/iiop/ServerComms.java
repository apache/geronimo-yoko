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

import org.apache.yoko.orb.OBPortableServer.POAManager_impl;
import org.apache.yoko.orb.OCI.IIOP.AcceptorInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.support.ReflectionSupport;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicyValue;
import testify.bus.Bus;
import testify.bus.EnumSpec;
import testify.bus.FieldSpec;
import testify.bus.LogLevel;
import testify.bus.MethodSpec;
import testify.bus.StringSpec;
import testify.bus.TypeSpec;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;
import testify.jupiter.annotation.iiop.ConfigureServer.CorbanameUrl;
import testify.jupiter.annotation.logging.TestLogger;
import testify.parts.PartRunner;
import testify.util.Maps;
import testify.util.Optionals;
import testify.util.Stack;
import testify.util.Throw;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static testify.jupiter.annotation.iiop.ServerComms.ServerInfo.NAME_SERVICE_URL;
import static testify.util.FormatUtil.escapeHostForUseInUrl;
import static testify.util.Reflect.newInstance;
import static testify.util.Reflect.newMatchingInstance;

final class ServerComms implements Serializable {
    enum ServerOp {START_SERVER, STOP_SERVER, KILL_SERVER}
    enum ServerInfo implements StringSpec {NAME_SERVICE_URL}
    private enum ServerRequest implements EnumSpec<ServerOp> {SEND}
    private enum MethodRequest implements MethodSpec {SEND}
    private enum FieldRequest implements FieldSpec {INIT}
    private enum BeginLogging implements TypeSpec<Supplier<Optional<TestLogger>>> {BEGIN_LOGGING}
    private enum EndLogging implements TypeSpec<Consumer<TestLogger>> {END_LOGGING}
    private enum Result implements TypeSpec<Throwable> {RESULT}

    private static String REQUEST_COUNT_PREFIX = "Request#";
    private final String serverName;
    private final Properties props;
    private final String[] args;
    private transient Bus bus;
    private String nsUrl;
    /** The constructor initializes this to true but it will be false when de-serialized. */
    private transient final boolean inClient;
    private transient int methodCount;

    private transient volatile ServerInstance server;
    private transient CountDownLatch serverShutdown;
    /** The logger to be used, per test. Server-side only! */
    private transient Optional<TestLogger> testLogger;

    private transient ServerComms otherSide; // can only be populated if the server is in the same process as the client

    class ServerInstance {
        final ORB orb;
        final Map<Class<?>, Object> paramMap;
        final POA childPoa;
        final int port;
        final String host;
        private ServerInstance() {
            this.orb = ORB.init(args, props);
            this.paramMap = Maps.of(ORB.class, orb, Bus.class, bus);
            try {
                POA rootPoa = (POA) orb.resolve_initial_references("RootPOA");
                POAManager_impl pm = (POAManager_impl) rootPoa.the_POAManager();
                pm.activate();
                final AcceptorInfo info = (AcceptorInfo) pm.get_acceptors()[0].get_info();
                // We might have been started up without a specific port.
                // In any case, dig out the host and port number and save them away.
                this.port = info.port() & 0xFFFF;
                this.host = info.hosts()[0];
                bus.log(() -> String.format("Server listening on host %s and port %d%n", host, port));
                // create the POA policies for the server
                Policy[] policies = {
                        rootPoa.create_thread_policy(ThreadPolicyValue.ORB_CTRL_MODEL),
                        rootPoa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT),
                        rootPoa.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID),
                        rootPoa.create_id_uniqueness_policy(IdUniquenessPolicyValue.MULTIPLE_ID),
                        rootPoa.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN),
                        rootPoa.create_request_processing_policy(RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY),
                        rootPoa.create_implicit_activation_policy(ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION),
                };
                childPoa = rootPoa.create_POA(serverName, pm, policies);

            } catch (InvalidName | AdapterInactive | AdapterAlreadyExists | InvalidPolicy e) {
                throw Throw.andThrowAgain(e);
            }
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

    String getNameServiceUrl() {
        return Objects.requireNonNull(nsUrl, () -> { throw new IllegalStateException("Name service not available"); });
    }

    private void assertClientSide() { assertTrue(inClient, () -> Stack.getCallingFrame(1) + " must only be used on the client"); }
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
        this.bus.onMsg(BeginLogging.BEGIN_LOGGING, supplier -> completeRequest("start logging", this::beginLogging0, supplier));
        this.bus.onMsg(EndLogging.END_LOGGING, consumer -> completeRequest("start logging", this::endLogging0, consumer));
    }

    void control(ServerOp op) throws ControlOperationFailed {
        assertClientSide();
        bus.put(ServerRequest.SEND, op);
        waitForCompletion(t -> new ControlOperationFailed(op, t));
        this.nsUrl = bus.get(NAME_SERVICE_URL);
    }

    private void control0(ServerOp op) {
        switch (op) {
        case START_SERVER:
            assertServer(IS_STOPPED);
            assertNull(server, "Server already started");
            this.server = new ServerInstance();
            this.props.setProperty("yoko.iiop.port", "" + server.port);
            this.props.setProperty("yoko.iiop.host", server.host);
            this.nsUrl = String.format("corbaname:iiop:%s:%d", escapeHostForUseInUrl(server.host), server.port);
            bus.put(NAME_SERVICE_URL, nsUrl);
            break;
        case STOP_SERVER:
            assertServer(IS_STARTED);
            server.stop();
            server = null;
            this.nsUrl = null;
            bus.put(NAME_SERVICE_URL, "SERVER STOPPED");
            break;
        case KILL_SERVER:
            bus.log("killing server");
            if (server != null) server.stop();
            this.nsUrl = null;
            bus.put(NAME_SERVICE_URL, "SERVER STOPPED");
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

    void beginLogging(Supplier<Optional<TestLogger>> supplier) {
        assertClientSide();
        bus.put(BeginLogging.BEGIN_LOGGING, supplier);
        waitForCompletion(t -> new LoggingFailed("Failure beginning logging", t));
    }

    void beginLogging0(Supplier<Optional<TestLogger>> supplier) {
        assertServer(true);
        this.testLogger = supplier.get();
    }

    void endLogging(Consumer<TestLogger> consumer) {
        assertClientSide();
        bus.put(EndLogging.END_LOGGING, consumer);
        waitForCompletion(t -> new LoggingFailed("Failure ending logging", t));
    }

    void endLogging0(Consumer<TestLogger> consumer) {
        assertServer(true);
        this.testLogger.ifPresent(consumer);
        this.testLogger = null; // DELIBERATE! Invoking end without begin is an error
    }

    String instantiate(Field f) throws FieldInstantiationFailed {
        assertClientSide();
        bus.put(FieldRequest.INIT, f);
        waitForCompletion(t -> new FieldInstantiationFailed(f, t));
        final String ior = bus.get(f.getName());
        bus.log( ior);
        return ior;
    }

    private <IMPL extends Remote, TIE extends Servant & Tie>
    void instantiate0(Field f) {
        assertServer(IS_STARTED);
        try {
            bus.log(LogLevel.ERROR, "field = " + f);
            final Class<IMPL> implClass = (Class<IMPL>) Optionals.requireOneOf(
                    findAnnotation(f, ClientStub.class).map(ClientStub::value),
                    findAnnotation(f, CorbanameUrl.class).map(CorbanameUrl::value));

            IMPL o = newInstance(implClass, server.paramMap);
            // create the tie
            if (!!!(o instanceof PortableRemoteObject)) {
                PortableRemoteObject.exportObject(o);
            }
            TIE tie = (TIE) Util.getTie(o);
            if (tie == null) {
                // try creating the tie directly
                tie = newMatchingInstance(implClass, "_*_Tie");
            }
            tie.setTarget(o);
            // do the POA things
            server.childPoa.activate_object(tie);
            // put the result on the bus
            String result;
            if (f.getType() == String.class) {
                NamingContext root = NamingContextHelper.narrow(server.orb.resolve_initial_references("NameService"));
                NameComponent[] cosName = {new NameComponent(f.getName(), "")};
                root.bind(cosName, tie.thisObject());
                result = this.nsUrl + "#" + f.getName();
            } else {
                result = server.orb.object_to_string(tie.thisObject());
            }
            bus.put(f.getName(), result);
        } catch (Throwable throwable) {
            throw Throw.andThrowAgain(throwable);
        }
    }

    private <T extends ServerResult> void waitForCompletion(Function<Throwable, T> wrapper) throws T {
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
            bus.put(Result.RESULT, new ServerSideException(e));
            bus.put(requestId, prefix + parameter + " completed abnormally with exception " + e);
        }
    }

    private String getNextRequestId() {
        return REQUEST_COUNT_PREFIX + methodCount++;
    }

    static class ServerSideException extends RuntimeException {
        ServerSideException(Throwable cause) {
            super(String.format("Server side thread %08x threw %s\n%s", Thread.currentThread().getId(), cause, fullText(cause)));
        }

        private static String fullText(Throwable cause) {
            final StringWriter sw = new StringWriter();
            cause.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
    }

    static class ServerResult extends RuntimeException {
        ServerResult(String message, Throwable cause) { super(message, cause); }
    }

    static class ServerLaunchFailed extends ServerResult {
        ServerLaunchFailed(Throwable cause) { super("Server launch failed", cause); }
    }

    static class ControlOperationFailed extends ServerResult {
        ControlOperationFailed(ServerOp op, Throwable cause) { super("Received exception from server while trying to issue " + op + " request", cause); }
    }

    static class MethodInvocationFailed extends ServerResult {
        MethodInvocationFailed(Method m, Throwable cause) { super("Received exception from server while trying to invoke method:\n    " + m, cause); }
    }

    static class FieldInstantiationFailed extends ServerResult {
        FieldInstantiationFailed(Field f, Throwable cause) { super("Received exception from server while trying to instantiate field:\n    " + f, cause); }
    }

    static class LoggingFailed extends ServerResult {
        LoggingFailed(String reason, Throwable t) { super(reason, t); }
    }
}

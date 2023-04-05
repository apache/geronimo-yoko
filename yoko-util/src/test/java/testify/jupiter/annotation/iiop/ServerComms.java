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
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.Servant;
import org.opentest4j.TestAbortedException;
import testify.bus.Bus;
import testify.bus.EnumSpec;
import testify.bus.FieldSpec;
import testify.bus.LogLevel;
import testify.bus.MethodSpec;
import testify.bus.StringSpec;
import testify.bus.TypeSpec;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;
import testify.jupiter.annotation.iiop.ConfigureServer.CorbanameUrl;
import testify.jupiter.annotation.iiop.ConfigureServer.ServerName;
import testify.jupiter.annotation.logging.TestLogger;
import testify.parts.PartRunner;
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
import java.net.BindException;
import java.rmi.Remote;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static testify.jupiter.annotation.iiop.ServerComms.ServerInfo.NAME_SERVICE_URL;
import static testify.util.FormatUtil.escapeHostForUseInUrl;
import static testify.util.Reflect.newInstance;
import static testify.util.Reflect.newMatchingInstance;

final class ServerComms implements Serializable {
    /*
     * An instance of this class is constructed for the client.
     * This is serialized and de-serialized into an instance for the server.
     * After the initial object copy, all further communications are via the Bus.
     */
    enum ServerOp {START_SERVER, STOP_SERVER, KILL_SERVER}
    enum ServerInfo implements StringSpec {NAME_SERVICE_URL}
    private enum ServerRequest implements EnumSpec<ServerOp> {SEND}
    private enum MethodRequest implements MethodSpec {SEND}
    private enum FieldRequest implements FieldSpec {INIT, EXPORT}
    private enum BeginLogging implements TypeSpec<Supplier<Optional<TestLogger>>> {BEGIN_LOGGING}
    private enum EndLogging implements TypeSpec<Consumer<TestLogger>> {END_LOGGING}
    private enum Result implements TypeSpec<ServerSideException> {RESULT}

    private static final String REQUEST_COUNT_PREFIX = "Request#";
    private static final Map<UUID, ORB> ORB_MAP = new ConcurrentHashMap<>();

    private final UUID uuid = UUID.randomUUID();
    private final ServerName serverName;
    private final Properties props;
    private final Map<Object, Object> originalProps;
    private final String[] args;
    private transient Bus bus;
    private String nsUrl;
    /** true when constructed, false when deserialized */
    private transient final boolean inClient;
    private transient int methodCount;

    /** This should only ever be used on the server, and always hold a null on the client. */
    private final AtomicReference<ServerInstance> serverRef = new AtomicReference<>();

    private transient CountDownLatch serverShutdown;
    /** The logger to be used, per test. Server-side only! */
    private transient Optional<TestLogger> testLogger;

    private transient ServerComms otherSide; // can only be populated if the server is in the same process as the client

    ServerComms(ServerName serverName, Properties props, String[] args) {
        this.inClient = true;
        this.serverName = serverName;
        this.props = props;
        this.originalProps = Collections.unmodifiableMap(new HashMap<Object, Object>(props));
        this.args = args;
    }

    String getNameServiceUrl() {
        return Objects.requireNonNull(nsUrl, () -> { throw new IllegalStateException("Name service not available"); });
    }

    private void assertClientSide() { if (!inClient) fail(Stack.getCallingFrame(1) + " must only be used on the client"); }
    private void assertServerSide() { if (inClient) fail(Stack.getCallingFrame(1) + " must only be used on the server"); }

    private static final boolean IS_STARTED = true;
    private static final boolean IS_STOPPED = false;
    private void assertServer(boolean serverShouldBeStarted) {
        assertServerSide();
        if (serverShouldBeStarted)
            assertNotNull(serverRef.get(), "Server not started");
        else
            assertNull(serverRef.get(), "Server already started");
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

    private void launch0(ServerName name) {
        this.bus.log("Server started");
        this.serverShutdown = new CountDownLatch(1);
        // register the control, method invocation, and field instantiation handlers
        this.bus.onMsg(ServerRequest.SEND, op -> completeRequest("control operation ", this::control0, op));
        this.bus.onMsg(FieldRequest.INIT, field -> completeRequest("instantiation of field " , this::instantiate0, field));
        this.bus.onMsg(FieldRequest.EXPORT, field -> completeRequest("export of field " , this::exportObject0, field));
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
        final ServerInstance oldServer = serverRef.get();
        switch (op) {
        case START_SERVER:
            assertServer(IS_STOPPED);
            try {
                final ServerInstance newServer = new ServerInstance(bus, serverName, args, props);
                System.out.println("### server started on " + newServer.host + ":" + newServer.port);
                if (!this.serverRef.compareAndSet(null, newServer)) fail("Server already started");
                ORB_MAP.put(uuid, newServer.orb);
                // set the endpoint property which seems to take precedence
                String endpointSpec = String.format("iiop --bind %1$s --host %1$s --port %2$d", newServer.host, newServer.port);
                this.props.setProperty("yoko.orb.oa.endpoint", endpointSpec);
                // set the host and port properties for completeness
                this.nsUrl = String.format("corbaname:iiop:%s:%d", escapeHostForUseInUrl(newServer.host), newServer.port);
                bus.put(NAME_SERVICE_URL, nsUrl);
            } catch (INITIALIZE e) {
                if (! (e.getCause() instanceof COMM_FAILURE)) throw e;
                if (! (e.getCause().getCause() instanceof BindException)) throw e;
                // In some CI environments the endpoint that we assume is still free
                // may be in use - perhaps grabbed by some eager concurrent process on the same system?
                // If this is so, we need to discard our recorded endpoint affinity for this test
                String assumptionMessage = "Could not bind to endpoint " + props.getProperty("yoko.orb.oa.endpoint");
                props.clear();
                props.putAll(originalProps);
                // and ensure the client fails an assumption on the calling test thread.
                throw new ServerSideException(e) {
                    ServerSideException resolve() {
                        throw new TestAbortedException(assumptionMessage, this);
                    }
                };
            }
            break;
        case STOP_SERVER:
            assertServer(IS_STARTED);
            oldServer.stop();
            System.out.println("### server stopped on " + oldServer.host + ":" + oldServer.port);
            if (!this.serverRef.compareAndSet(oldServer, null)) fail("unexpected concurrent modification of server instance");
            ORB_MAP.remove(uuid);
            this.nsUrl = null;
            bus.put(NAME_SERVICE_URL, "SERVER STOPPED");
            break;
        case KILL_SERVER:
            bus.log("killing server");
            if (oldServer != null) {
                oldServer.stop();
                if (!this.serverRef.compareAndSet(oldServer, null)) fail("unexpected concurrent modification of server instance");
            }
            this.nsUrl = null;
            bus.put(NAME_SERVICE_URL, "SERVER STOPPED");
            serverShutdown.countDown();
            break;
        default:
            throw (Error) fail("Unknown op type: " + op);
        }
    }

    /**
     * This must only be called on the client instance of this object.
     * @return the server ORB if the server is running in the same process
     */
    Optional<ORB> getServerOrb() {
        assertClientSide();
        return Optional.ofNullable(ORB_MAP.get(uuid));
    }

    void invoke(Method m) throws MethodInvocationFailed {
        assertClientSide();
        bus.put(MethodRequest.SEND, m);
        waitForCompletion(t -> new MethodInvocationFailed(m, t));
    }

    private Object invoke0(Method m) {
        assertServer(IS_STARTED);
        final Object[] params = Stream.of(m.getParameterTypes())
                .map(serverRef.get().paramMap::get)
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
        assertServer(IS_STARTED);
        this.testLogger = supplier.get();
    }

    void endLogging(Consumer<TestLogger> consumer) {
        assertClientSide();
        bus.put(EndLogging.END_LOGGING, consumer);
        waitForCompletion(t -> new LoggingFailed("Failure ending logging", t));
    }

    void endLogging0(Consumer<TestLogger> consumer) {
        assertServer(IS_STARTED);
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
            bus.log(LogLevel.INFO, "field = " + f);
            final Class<IMPL> implClass = (Class<IMPL>) Optionals.requireOneOf(
                    findAnnotation(f, ClientStub.class).map(ClientStub::value),
                    findAnnotation(f, CorbanameUrl.class).map(CorbanameUrl::value));

            IMPL o = newInstance(implClass, serverRef.get().paramMap);
            boolean useNameService = f.getType() == String.class;
            String result = getIor(f, o, useNameService);
            bus.put(f.getName(), result);
        } catch (Throwable throwable) {
            throw Throw.andThrowAgain(throwable);
        }
    }

    private <IMPL extends Remote, TIE extends Servant & Tie>
    String getIor(Field f, IMPL o, boolean useNameService) throws Exception {
        // create the tie
        if (!!!(o instanceof PortableRemoteObject)) PortableRemoteObject.exportObject(o);
        TIE tie = (TIE) Util.getTie(o);

        // if that didn't work try creating the tie directly
        if (tie == null) tie = newMatchingInstance(o.getClass(), "_*_Tie");

        // do up the tie
        tie.setTarget(o);
        // connect the tie to the POA
        serverRef.get().childPoa.activate_object(tie);
        // put the result on the bus
        String result;
        if (useNameService) {
            NamingContext root = NamingContextHelper.narrow(serverRef.get().orb.resolve_initial_references("NameService"));
            NameComponent[] cosName = {new NameComponent(f.getName(), "")};
            root.bind(cosName, tie.thisObject());
            result = this.nsUrl + "#" + f.getName();
        } else {
            result = serverRef.get().orb.object_to_string(tie.thisObject());
        }
        return result;
    }

    String exportObject(Field f) {
        assertClientSide();
        bus.put(FieldRequest.EXPORT, f);
        waitForCompletion(t -> new ObjectExportFailed(f, t));
        final String ior = bus.get(f.getName());
        bus.log(ior);
        return ior;
    }

    private <IMPL extends Remote, TIE extends Servant & Tie>
    void exportObject0(Field f) {
        assertServer(IS_STARTED);
        try {
            bus.log(LogLevel.INFO, "field = " + f);
            IMPL o = (IMPL) f.get(null);
            String result = getIor(f, o, false);
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
        final ServerSideException result = bus.get(Result.RESULT);
        if (result == null) return;
        throw wrapper.apply(result.resolve());
    }

    private <T> void completeRequest(String prefix, Consumer<T> consumer, T parameter) {
        assertServerSide();
        final String requestId = getNextRequestId();
        try {
            bus.log("Invoking " + requestId + " " + parameter);
            consumer.accept(parameter);
            // on successful completion, send back a null
            bus.put(Result.RESULT, null);
            bus.put(requestId, prefix + parameter + " completed normally");
        } catch (ServerSideException e) {
            bus.put(Result.RESULT, e);
            bus.put(requestId, prefix + parameter + " completed abnormally with exception " + e);
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

        /** To be overridden if specialised behaviour is required, such as returning or throwing a different exception */
        ServerSideException resolve() { return this; }
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

    static class ObjectExportFailed extends ServerResult {
        ObjectExportFailed(Field f, Throwable cause) { super("Received exception from server while trying to export object in final field:\n    " + f, cause); }
    }

    static class LoggingFailed extends ServerResult {
        LoggingFailed(String reason, Throwable t) { super(reason, t); }
    }
}

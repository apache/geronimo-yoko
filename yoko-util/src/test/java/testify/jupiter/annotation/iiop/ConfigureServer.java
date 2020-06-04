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

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.omg.CORBA.ORB;
import testify.bus.Bus;
import testify.jupiter.annotation.ConfigurePartRunner;
import testify.jupiter.annotation.iiop.ConfigureServer.AfterServer;
import testify.jupiter.annotation.iiop.ConfigureServer.BeforeServer;
import testify.jupiter.annotation.iiop.ConfigureServer.Control;
import testify.jupiter.annotation.iiop.ConfigureServer.CorbanameUrl;
import testify.jupiter.annotation.iiop.ConfigureServer.NameServiceUrl;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;
import testify.jupiter.annotation.iiop.ConfigureServer.UseWithServerOrb;
import testify.jupiter.annotation.iiop.ServerComms.ServerOp;
import testify.jupiter.annotation.impl.AnnotationButler;
import testify.jupiter.annotation.impl.SimpleParameterResolver;
import testify.jupiter.annotation.impl.Steward;
import testify.parts.PartRunner;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.rmi.PortableRemoteObject.narrow;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_ONLY;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_WRITE;
import static testify.jupiter.annotation.iiop.OrbSteward.args;
import static testify.jupiter.annotation.iiop.OrbSteward.props;
import static testify.jupiter.annotation.impl.PartRunnerSteward.getPartRunner;
import static testify.util.Reflect.setStaticField;

@Repeatable(ConfigureMultiServer.class)
@ExtendWith(ServerExtension.class)
@Target({ANNOTATION_TYPE, TYPE})
@ConfigureOrb
@ConfigurePartRunner
@Retention(RUNTIME)
public @interface ConfigureServer {
    String DEFAULT_SERVER_NAME = "server";
    String serverName() default DEFAULT_SERVER_NAME;
    boolean newProcess() default false;
    String[] jvmArgs() default {};
    /**
     * Define the config for the ORB this server will use.
     */
    ConfigureOrb orb() default @ConfigureOrb;

    /**
     * Annotate methods to be run in the server on ORB startup
     */
    @Target({ANNOTATION_TYPE, METHOD})
    @Retention(RUNTIME)
    @interface BeforeServer {
        /** A regular expression to match which servers to run against */
        String serverPattern() default ".*";
    }

    /**
     * Annotate methods to be run in the server after the tests are completed
     */
    @Target({ANNOTATION_TYPE, METHOD})
    @Retention(RUNTIME)
    public @interface AfterServer {
        /** A regular expression to match which servers to run against */
        String serverPattern() default ".*";
    }

    @Target({ANNOTATION_TYPE, TYPE})
    @Retention(RUNTIME)
    @interface UseWithServerOrb {
        /** A regular expression to match which servers to run against */
        String serverPattern() default ".*";
    }

    /**
     * Annotate a static field in a test to inject a remote stub
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    public @interface ClientStub {
        /** The implementation class of the remote object */
        Class<? extends Remote> value();
        /** A literal string to match the server name. Not a regular expression since the remote object can exist on only one server. */
        String serverName() default DEFAULT_SERVER_NAME;
    }

    /**
     * Annotate a static field in a test to inject a corbaname URL for a remote object implementation
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    public @interface CorbanameUrl {
        /** The implementation class of the remote object */
        Class<? extends Remote> value();
        /** A literal string to match the server name. Not a regular expression since the remote object can exist on only one server. */
        String serverName() default DEFAULT_SERVER_NAME;
    }

    /**
     * Annotate a static field in a test to inject the name service URL
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    public @interface NameServiceUrl {
        /** A literal string to match the server name. Not a regular expression since the remote object can exist on only one server. */
        String serverName() default DEFAULT_SERVER_NAME;
    }


    /**
     * Annotate a static field in a test to inject a server control object
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    public @interface Control {
        /** A literal string to match the server name. Not a regular expression since the controller controls exactly one server. */
        String serverName() default DEFAULT_SERVER_NAME;
    }
}

class ServerSteward extends Steward<ConfigureServer> {
    private final String name;
    private final List<Field> controlFields;
    private final List<Field> nameServiceUrlFields;
    private final List<Field> corbanameUrlFields;
    private final List<Field> clientStubFields;
    private final List<Method> beforeMethods;
    private final List<Method> afterMethods;
    private ServerComms serverComms;

    private ServerSteward(Class<?> testClass) {
        super(ConfigureServer.class, testClass);
        this.name = annotation.serverName();
        this.controlFields = AnnotationButler.forClass(Control.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(ServerControl.class)
                .filter(anno -> anno.serverName().equals(this.name))
                .recruit()
                .findFields(testClass);
        this.nameServiceUrlFields = AnnotationButler.forClass(NameServiceUrl.class)
                .requireTestAnnotation(ConfigureServer.class,
                        "the test server must have its name service configured",
                        cfg -> cfg.orb().nameService(),
                        anyOf(is(READ_ONLY), is(READ_WRITE)))
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(String.class)
                .filter(anno -> anno.serverName().equals(this.name))
                .recruit()
                .findFields(testClass);
        this.corbanameUrlFields = AnnotationButler.forClass(CorbanameUrl.class)
                .requireTestAnnotation(ConfigureServer.class,
                        "the test server must have its name service configured",
                        cfg -> cfg.orb().nameService(),
                        anyOf(is(READ_ONLY), is(READ_WRITE)))
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(String.class)
                .filter(anno -> anno.serverName().equals(this.name))
                .recruit()
                .findFields(testClass);
        this.clientStubFields = AnnotationButler.forClass(ClientStub.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(Remote.class)
                .filter(anno -> anno.serverName().equals(this.name))
                .recruit()
                .findFields(testClass);
        this.beforeMethods = AnnotationButler.forClass(BeforeServer.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertParameterTypes(Bus.class, ORB.class)
                .filter(anno -> Pattern.matches(anno.serverPattern(), this.name))
                .recruit()
                .findMethods(testClass);
        this.afterMethods = AnnotationButler.forClass(AfterServer.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertParameterTypes(Bus.class, ORB.class)
                .filter(anno -> Pattern.matches(anno.serverPattern(), this.name))
                .recruit()
                .findMethods(testClass);
        assertFalse(controlFields.isEmpty() && clientStubFields.isEmpty() && beforeMethods.isEmpty(), () -> ""
                + "The @" + ConfigureServer.class.getSimpleName() + " annotation on class " + testClass.getName() + " requires one of the following:"
                + "\n - EITHER the test must annotate a public static method with@" + ConfigureServer.BeforeServer.class.getSimpleName()
                + "\n - OR the test must annotate a public static field with@" + Control.class.getSimpleName()
                + "\n - OR the test must annotate a public static field with@" + ClientStub.class.getSimpleName());

        // blow up if the config is bogus
        if (annotation.newProcess()) return;
        if (annotation.jvmArgs().length > 0) throw new Error("The annotation @" + ConfigureServer.class.getSimpleName()
                + " cannot include JVM arguments unless newProcess is set to true");
    }

    Bus getBus(ExtensionContext ctx) {
        return getPartRunner(ctx).bus(name);
    }

    void beforeAll(ExtensionContext ctx) throws Exception{
        startServer(ctx);
        populateControlFields(ctx);
        injectNameServiceURL();
        instantiateServerObjects(ctx);
        beforeServer(ctx);
    }

    void afterAll(ExtensionContext ctx) {
        afterServer(ctx);
    }

    private void startServer(ExtensionContext ctx) {
        PartRunner runner = getPartRunner(ctx);
        // does this part run in a thread or a new process?
        if (annotation.newProcess()) runner.useNewJVMWhenForking(annotation.jvmArgs());
        else runner.useNewThreadWhenForking();

        final Properties props = props(annotation.orb(), ctx.getRequiredTestClass(), this::isServerOrbModifier);
        final String[] args = args(annotation.orb(), ctx.getRequiredTestClass(), this::isServerOrbModifier);
        serverComms = new ServerComms(name, props, args);
        serverComms.launch(runner);
        serverComms.control(ServerOp.START_SERVER);
    }

    private void populateControlFields(ExtensionContext ctx) {
        ServerControl serverControl =  new ServerControl(){
            public void start() {
                serverComms.control(ServerOp.START_SERVER);
                injectNameServiceURL();
                instantiateServerObjects(ctx);
            }
            public void stop() {
                serverComms.control(ServerOp.STOP_SERVER);
                injectNameServiceURL();
            }
        };
        this.controlFields.forEach(f -> setStaticField(f, serverControl));
    }

    private void injectNameServiceURL() {
        nameServiceUrlFields.stream().forEach(f -> setStaticField(f, serverComms.getNameServiceUrl()));
    }

    private void instantiateServerObjects(ExtensionContext ctx) {
        ORB clientOrb = OrbSteward.getOrb(ctx);
        corbanameUrlFields.stream().forEach(f -> {
            String url = serverComms.instantiate(f);
            setStaticField(f, url);
        });
        clientStubFields.stream().forEach(f -> {
            // instantiate the remote field on the server
            String ior = serverComms.instantiate(f);
            // instantiate the stub on the client
            setStaticField(f, narrow(clientOrb.string_to_object(ior), f.getType()));
        });
    }

    private void beforeServer(ExtensionContext ctx) throws Exception {
        // drive the before methods
        beforeMethods.stream().forEach(serverComms::invoke);
    }

    private void afterServer(ExtensionContext ctx) {
        // drive the after methods
        afterMethods.stream().forEach(serverComms::invoke);
    }

    private boolean isServerOrbModifier(Class<?> c) {
        return AnnotationSupport.findAnnotation(c, UseWithServerOrb.class).map(this::matchesAnnotation).orElse(false);
    }

    private boolean matchesAnnotation(UseWithServerOrb anno) { return Pattern.matches(anno.serverPattern(), this.name); }

    static ServerSteward getInstance(ExtensionContext ctx) {
        return Steward.getInstanceForContext(ctx, ServerSteward.class, ServerSteward::new);
    }

}

class ServerExtension implements BeforeAllCallback, SimpleParameterResolver<Bus>, AfterAllCallback {
    @Override
    public void beforeAll(ExtensionContext ctx) throws Exception {
        ServerSteward.getInstance(ctx).beforeAll(ctx);
    }

    @Override
    public boolean supportsParameter(ParameterContext ctx) { return ctx.getParameter().getType() == Bus.class; }
    @Override
    // Since the ServerSteward was retrieved from BeforeAll (i.e. in the test class context),
    // that is the one that will be found and reused from here (even if this is a test method context)
    public Bus resolveParameter(ExtensionContext ctx)  { return ServerSteward.getInstance(ctx).getBus(ctx); }

    @Override
    public void afterAll(ExtensionContext ctx) throws Exception {
        ServerSteward.getInstance(ctx).afterAll(ctx);
    }
}
/*
 * =============================================================================
 * Copyright (c) 2021 IBM Corporation and others.
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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.omg.CORBA.ORB;
import testify.bus.Bus;
import testify.jupiter.annotation.Summoner;
import testify.jupiter.annotation.impl.AnnotationButler;
import testify.jupiter.annotation.logging.TestLogger;
import testify.parts.PartRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static javax.rmi.PortableRemoteObject.narrow;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_ONLY;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_WRITE;
import static testify.jupiter.annotation.iiop.OrbSteward.args;
import static testify.jupiter.annotation.iiop.OrbSteward.props;
import static testify.jupiter.annotation.impl.PartRunnerSteward.getPartRunner;
import static testify.util.Reflect.setStaticField;

class ServerSteward {
    private static final Summoner<ConfigureServer, ServerSteward> SUMMONER = Summoner.forAnnotation(ConfigureServer.class, ServerSteward.class, ServerSteward::new);
    private final List<Field> controlFields;
    private final List<Field> nameServiceUrlFields;
    private final List<Field> corbanameUrlFields;
    private final List<Field> clientStubFields;
    private final List<Method> beforeMethods;
    private final List<Method> afterMethods;
    private final boolean newProcess;
    private final String[] jvmArgs;
    private final String serverName;
    private ServerComms serverComms;
    private ConfigureOrb serverOrbConfig;
    private ConfigureOrb clientOrbConfig;

    private ServerSteward(ConfigureServer config, ExtensionContext context) {
        this.newProcess = config.newProcess();
        this.jvmArgs = config.jvmArgs();
        this.serverName = config.serverName();
        this.serverOrbConfig = config.serverOrb();
        this.clientOrbConfig = config.clientOrb();
        Class<?> testClass = context.getRequiredTestClass();
        this.controlFields = AnnotationButler.forClass(ConfigureServer.Control.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(ServerControl.class)
                .filter(anno -> anno.serverName().equals(config.serverName()))
                .recruit()
                .findFields(testClass);
        this.nameServiceUrlFields = AnnotationButler.forClass(ConfigureServer.NameServiceUrl.class)
                .requireTestAnnotation(ConfigureServer.class,
                        "the test server must have its name service configured",
                        cfg -> cfg.serverOrb().nameService(),
                        anyOf(is(READ_ONLY), is(READ_WRITE)))
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(String.class)
                .filter(anno -> anno.serverName().equals(config.serverName()))
                .recruit()
                .findFields(testClass);
        this.corbanameUrlFields = AnnotationButler.forClass(ConfigureServer.CorbanameUrl.class)
                .requireTestAnnotation(ConfigureServer.class,
                        "the test server must have its name service configured",
                        cfg -> cfg.serverOrb().nameService(),
                        anyOf(is(READ_ONLY), is(READ_WRITE)))
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(String.class)
                .filter(anno -> anno.serverName().equals(config.serverName()))
                .recruit()
                .findFields(testClass);
        this.clientStubFields = AnnotationButler.forClass(ConfigureServer.ClientStub.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertFieldTypes(Remote.class)
                .filter(anno -> anno.serverName().equals(config.serverName()))
                .recruit()
                .findFields(testClass);
        this.beforeMethods = AnnotationButler.forClass(ConfigureServer.BeforeServer.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertParameterTypes(Bus.class, ORB.class)
                .filter(anno -> Pattern.matches(anno.serverPattern(), config.serverName()))
                .recruit()
                .findMethods(testClass);
        this.afterMethods = AnnotationButler.forClass(ConfigureServer.AfterServer.class)
                .requireTestAnnotation(ConfigureServer.class)
                .assertPublic()
                .assertStatic()
                .assertParameterTypes(Bus.class, ORB.class)
                .filter(anno -> Pattern.matches(anno.serverPattern(), config.serverName()))
                .recruit()
                .findMethods(testClass);
        assertFalse(controlFields.isEmpty() && clientStubFields.isEmpty() && beforeMethods.isEmpty(), () -> ""
                + "The @" + ConfigureServer.class.getSimpleName() + " annotation on class " + testClass.getName() + " requires one of the following:"
                + "\n - EITHER the test must annotate a public static method with@" + ConfigureServer.BeforeServer.class.getSimpleName()
                + "\n - OR the test must annotate a public static field with@" + ConfigureServer.Control.class.getSimpleName()
                + "\n - OR the test must annotate a public static field with@" + ConfigureServer.ClientStub.class.getSimpleName());

        // blow up if the config is bogus
        if (config.newProcess()) return;
        if (config.jvmArgs().length > 0) throw new Error("The annotation @" + ConfigureServer.class.getSimpleName()
                + " must not include JVM arguments unless newProcess is set to true");
    }

    Bus getBus(ExtensionContext ctx) {
        return getPartRunner(ctx).bus(serverName);
    }

    void beforeAll(ExtensionContext ctx) {
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
        if (newProcess) runner.useNewJVMWhenForking(jvmArgs);
        else runner.useNewThreadWhenForking();

        final Properties props = props(serverOrbConfig, ctx.getRequiredTestClass(), this::isServerOrbModifier);
        final String[] args = args(serverOrbConfig, ctx.getRequiredTestClass(), this::isServerOrbModifier);
        serverComms = new ServerComms(serverName, props, args);
        serverComms.launch(runner);
        serverComms.control(ServerComms.ServerOp.START_SERVER);
    }

    private void populateControlFields(ExtensionContext ctx) {
        ServerControl serverControl = new ServerControl() {
            public void start() {
                serverComms.control(ServerComms.ServerOp.START_SERVER);
                injectNameServiceURL();
                instantiateServerObjects(ctx);
            }

            public void stop() {
                serverComms.control(ServerComms.ServerOp.STOP_SERVER);
                injectNameServiceURL();
            }
        };
        this.controlFields.forEach(f -> setStaticField(f, serverControl));
    }

    private void injectNameServiceURL() {
        nameServiceUrlFields.forEach(f -> setStaticField(f, serverComms.getNameServiceUrl()));
    }

    private void instantiateServerObjects(ExtensionContext ctx) {
        ORB clientOrb = ServerSteward.getInstance(ctx).getClientOrb(ctx);
        corbanameUrlFields.forEach(f -> {
            String url = serverComms.instantiate(f);
            setStaticField(f, url);
        });
        clientStubFields.forEach(f -> {
            // instantiate the remote field on the server
            String ior = serverComms.instantiate(f);
            // instantiate the stub on the client
            setStaticField(f, narrow(clientOrb.string_to_object(ior), f.getType()));
        });
    }

    private void beforeServer(ExtensionContext ctx) {
        // drive the before methods
        beforeMethods.forEach(serverComms::invoke);
    }

    private void afterServer(ExtensionContext ctx) {
        // drive the after methods
        afterMethods.forEach(serverComms::invoke);
    }

    private boolean isServerOrbModifier(Class<?> c) {
        return findAnnotation(c, ConfigureOrb.UseWithOrb.class)
                .map(ConfigureOrb.UseWithOrb::value)
                .map(serverOrbConfig.value()::matches)
                .orElse(false);
    }


    static ServerSteward getInstance(ExtensionContext ctx) {
        return SUMMONER.forContext(ctx).summon().orElseThrow(Error::new); // if no ServerSteward can be found, this is an error in the framework
    }

    public ORB getClientOrb(ExtensionContext ctx) {
        return OrbSteward.getOrb(ctx, clientOrbConfig);
    }

    public void beginLogging(ExtensionContext ctx) {
        if (newProcess) serverComms.beginLogging(TestLogger.getLogStarter(ctx));
    }

    public void endLogging(ExtensionContext ctx) {
        if (newProcess) serverComms.endLogging(TestLogger.getLogFinisher(ctx));
    }
}

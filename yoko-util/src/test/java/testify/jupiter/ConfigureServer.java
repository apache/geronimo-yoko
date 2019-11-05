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
package testify.jupiter;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.omg.CORBA.ORB;
import testify.bus.Bus;
import testify.jupiter.ConfigureServer.RunAtServerStartup;
import testify.jupiter.ConfigureServer.UseWithServerOrb;
import testify.parts.PartRunner;
import testify.parts.ServerPart;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedMethods;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.ModifierSupport.isPublic;
import static org.junit.platform.commons.support.ModifierSupport.isStatic;
import static testify.jupiter.OrbSteward.args;
import static testify.jupiter.OrbSteward.props;

@Repeatable(ConfigureMultiServer.class)
@ExtendWith(ServerExtension.class)
@Target({ANNOTATION_TYPE, TYPE})
@ConfigureOrb
@ConfigurePartRunner
@Retention(RUNTIME)
public @interface ConfigureServer {
    Class<? extends ServerPart> value() default DefaultServerPart.class;
    String name() default "server";
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
    @interface RunAtServerStartup {
        /** A regular expression to match which servers to run against */
        String value() default ".*";
    }

    @Target({ANNOTATION_TYPE, TYPE})
    @Retention(RUNTIME)
    @interface UseWithServerOrb {
        /** A regular expression to match which servers to run against */
        String value() default ".*";
    }
}

class ServerSteward extends Steward<ConfigureServer> {
    private final ConfigureServer config;
    private final String name;

    private ServerSteward(Class<?> testClass) {
        super(ConfigureServer.class);
        this.config = getAnnotation(testClass);
        this.name = config.name();
        // blow up if the config is bogus
        if (config.newProcess()) return;
        if (config.jvmArgs().length > 0) throw new Error("The annotation @" + ConfigureServer.class.getSimpleName()
                + " cannot include JVM arguments unless newProcess is set to true");
    }

    ServerSteward(ConfigureServer config, String name) {
        super(ConfigureServer.class);
        this.config = config;
        this.name = name;
    }

    Bus getBus(ExtensionContext ctx) {
        return PartRunnerSteward.getPartRunner(ctx).bus(name);
    }

    void startServer(ExtensionContext ctx) {
        PartRunner runner = PartRunnerSteward.getPartRunner(ctx);
        // does this part run in a thread or a new process?
        if (config.newProcess()) runner.useNewJVMWhenForking(config.jvmArgs());
        else runner.useNewThreadWhenForking();
        final ServerPart part = config.value() == DefaultServerPart.class
                        ? new DefaultServerPart(config.name(), ctx)
                        : ServerPart.createPart(config.value());


        final Properties props = props(config.orb(), ctx.getRequiredTestClass(), this::isServerOrbModifier);
        final String[] args = args(config.orb(), ctx.getRequiredTestClass(), this::isServerOrbModifier);
        ServerPart.launch(runner, part, this.name, props, args);
    }

    private boolean isServerOrbModifier(Class<?> c) {
        return findAnnotation(c, UseWithServerOrb.class).map(this::matchesAnnotation).orElse(false);
    }

    private boolean matchesAnnotation(UseWithServerOrb anno) { return Pattern.matches(anno.value(), this.name); }

    static ServerSteward getInstance(ExtensionContext ctx) {
        return Steward.getInstanceForContext(ctx, ServerSteward.class, ServerSteward::new);
    }
}

class ServerExtension implements BeforeAllCallback, SimpleParameterResolver<Bus> {
    @Override
    public void beforeAll(ExtensionContext ctx) {
        ServerSteward.getInstance(ctx).startServer(ctx);
    }

    @Override
    public boolean supportsParameter(ParameterContext ctx) { return ctx.getParameter().getType() == Bus.class; }
    @Override
    // Since the ServerSteward was retrieved from BeforeAll (i.e. in the test class context),
    // that is the one that will be found and reused from here (even if this is a test method context)
    public Bus resolveParameter(ExtensionContext ctx)  { return ServerSteward.getInstance(ctx).getBus(ctx); }
}

class DefaultServerPart extends ServerPart {
    private final String serverName;
    private final List<Method> methods;

    DefaultServerPart(String serverName, ExtensionContext ctx) {
        this.serverName = serverName;
        final Class<?> testClass = ctx.getRequiredTestClass();
        this.methods = findAnnotatedMethods(testClass, RunAtServerStartup.class, HierarchyTraversalMode.TOP_DOWN)
                .stream()
                .peek(this::assertMethodIsStatic)
                .peek(this::assertMethodIsPublic)
                .peek(this::assertParameterTypesAreValid)
                .filter(this::methodIsForThisServer)
                .collect(Collectors.toList());
        assertFalse(methods.isEmpty(), () -> ""
                + "The @" + ConfigureServer.class.getSimpleName()
                + " annotation on class " + testClass.getName()
                + " must have a class value OR the test must annotate a static method with"
                + " @" + RunAtServerStartup.class.getSimpleName());
    }

    private boolean methodIsForThisServer(Method m) {
        String pattern = findAnnotation(m, RunAtServerStartup.class).orElseThrow(Error::new).value();
        return Pattern.matches(pattern, serverName);
    }

    private void assertMethodIsStatic(Method m) {
        assertTrue(isStatic(m), () -> ""
                + "The @" + RunAtServerStartup.class.getSimpleName()
                + " annotation must be used on only public static methods."
                + " It has been used on the non-static method: " + m);
    }

    private void assertMethodIsPublic(Method m) {
        assertTrue(isPublic(m), () -> ""
                + "The @" + RunAtServerStartup.class.getSimpleName()
                + " annotation must be used on only public static methods."
                + " It has been used on the non-public method: " + m);
    }

    private void assertParameterTypesAreValid(Method m) {
        for (Class<?> paramType: m.getParameterTypes()) {
            if (paramType == ORB.class) continue;
            if (paramType == Bus.class) continue;
            fail("@" + RunAtServerStartup.class.getSimpleName()
                    + " does not support parameter of type " + paramType.getName()
                    + " on method " + m);
        }
    }

    @Override
    protected void run(ORB orb, Bus bus) {
        for (Method m: methods) {
            final Class<?>[] types = m.getParameterTypes();
            Object[] params = new Object[types.length];
            for (int i = 0; i < params.length; i++) {
                if (types[i] == ORB.class) params[i] = orb;
                else params[i] = bus;
            }
            ReflectionSupport.invokeMethod(m, null, params);
        }
    }
}

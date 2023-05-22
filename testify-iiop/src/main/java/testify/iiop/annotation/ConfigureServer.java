/*
 * Copyright 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package testify.iiop.annotation;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import testify.bus.Bus;
import testify.annotation.ConfigurePartRunner;
import testify.annotation.logging.LoggingExtension;
import testify.util.Assertions;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.rmi.Remote;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static testify.iiop.annotation.ConfigureServer.Separation.INTER_ORB;
import static testify.iiop.annotation.ConfigureServer.ServerName.DEFAULT_SERVER;
import static testify.iiop.annotation.OrbSteward.getActivatedRootPoa;

@ExtendWith({ LoggingExtension.class, ServerExtension.class }) // ensure ordering in case logging is enabled
@Target({ANNOTATION_TYPE, TYPE})
@ConfigurePartRunner
@Retention(RUNTIME)
@Inherited
public @interface ConfigureServer {
    enum ServerName {DEFAULT_SERVER}
    enum Separation {COLLOCATED, INTER_ORB, INTER_PROCESS}

    ServerName serverName() default DEFAULT_SERVER;
    Separation separation() default INTER_ORB;
    String[] jvmArgs() default {};

    /** Define the config for the ORB the client for this server will use. */
    ConfigureOrb clientOrb() default @ConfigureOrb("client orb");

    /** Define the config for the ORB this server will use. */
    ConfigureOrb serverOrb() default @ConfigureOrb("server orb");

    /**
     * Annotate methods to be run in the server on ORB startup
     */
    @Target({ANNOTATION_TYPE, METHOD})
    @Retention(RUNTIME)
    @interface BeforeServer {
        ServerName value() default DEFAULT_SERVER;
    }

    /**
     * Annotate methods to be run in the server after the tests are completed
     */
    @Target({ANNOTATION_TYPE, METHOD})
    @Retention(RUNTIME)
    @interface AfterServer {
        ServerName value() default DEFAULT_SERVER;
    }

    /**
     * Annotate a static field in a test to inject a remote stub
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    @interface ClientStub {
        /** The implementation class of the remote object */
        Class<? extends Remote> value();
        /** Specify which server should host the remote object. */
        ServerName serverName() default DEFAULT_SERVER;
    }

    /**
     * Annotate a static field or method in a test to denote a server-side target object.
     * If the annotated member is a field, the value of the field is used directly as the target object.
     * If the annotated member is a method, the method is invoked and the return value is used as the target object.
     * The stub for the target object will be supplied on the client as a test method parameter of the same type.
     * <p>
     *     A method might be desirable if parameters are to be supplied to the object constructor.
     *     The method may have server-side runtime parameters. See @{@link ServerExtension.ParamType} for details.
     * </p>
     * <p>
     *     Note that an annotated method will be re-invoked each time the server starts.
     *     If any caching of the resulting object is required, the method must do it.
     * </p>
     * <p>
     *     Multiple members may be annotated with this annotation.
     *     The framework will only distinguish them by field or return type, which must inherit @{@link Remote}.
     *     If multiple members are annotated with the same type, behaviour is unspecified.
     * </p>
     *
     */
    @Target({ANNOTATION_TYPE, FIELD, METHOD})
    @Retention(RUNTIME)
    @interface RemoteImpl {
        ServerName serverName() default DEFAULT_SERVER;
    }

    /**
     * Annotate a static field in a test to inject a stub corresponding to a @RemoteImpl member of the same type.
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    @interface RemoteStub {
        ServerName serverName() default DEFAULT_SERVER;
    }


    /**
     * Annotate a static field in a test to inject a corbaname URL for a remote object implementation
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    @interface CorbanameUrl {
        /** The implementation class of the remote object */
        Class<? extends Remote> value();
        /** A literal string to match the server name. Not a regular expression since the remote object can exist on only one server. */
        ServerName serverName() default DEFAULT_SERVER;
    }

    /**
     * Annotate a static field in a test to inject the name service stub
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    @interface NameServiceStub {
        /** A literal string to match the server name. Not a regular expression since the remote object can exist on only one server. */
        ServerName serverName() default DEFAULT_SERVER;
    }

    /**
     * Annotate a static field in a test to inject the name service URL
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    @interface NameServiceUrl {
        /** A literal string to match the server name. Not a regular expression since the remote object can exist on only one server. */
        ServerName serverName() default DEFAULT_SERVER;
    }


    /**
     * Annotate a static field in a test to inject a server control object
     */
    @Target({ANNOTATION_TYPE, FIELD})
    @Retention(RUNTIME)
    @interface Control {
        /** A literal string to match the server name. Not a regular expression since the controller controls exactly one server. */
        ServerName serverName() default DEFAULT_SERVER;
    }
}

class ServerExtension implements
        BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback,
        BeforeTestExecutionCallback, AfterTestExecutionCallback,
        ParameterResolver {

    enum ParamType {
        BUS(Bus.class),
        CLIENT_ORB(ORB.class),
        ROOT_POA(POA.class);

        static final Set<Class<?>> SUPPORTED_TYPES = unmodifiableSet(Stream.of(values()).map(pt -> pt.type).collect(toSet()));
        final Class<?> type;

        ParamType(Class<?> type) { this.type = type; }

        static Optional<ParamType> forClass(Class<?> type) {
            for (ParamType t: values()) if (t.type == type) return Optional.of(t);
            return Optional.empty();
        }
    }

    @Override
    public void beforeAll(ExtensionContext ctx) {
        // check for conflicting annotations
        if (findAnnotation(ctx.getRequiredTestClass(), ConfigureOrb.class).isPresent()
                || findAnnotation(ctx.getElement(), ConfigureOrb.class).isPresent())
            fail(String.format("Use of @%s and @%s on the same test is unsupported.",
                    ConfigureOrb.class.getSimpleName(), ConfigureServer.class.getSimpleName()));
        ServerSteward.getInstance(ctx).beforeAll(ctx);
    }

    @Override
    public void beforeEach(ExtensionContext ctx) { ServerSteward.getInstance(ctx).beforeEach(ctx); }

    @Override
    public boolean supportsParameter(ParameterContext pCtx, ExtensionContext ctx) {
        final Class<?> type = pCtx.getParameter().getType();
        if (ParamType.forClass(type).isPresent()) return true;
        return (ServerSteward.getInstance(ctx).supportsParameter(type));
    }

    // Since the ServerSteward was retrieved from BeforeAll (i.e. in the test class context),
    // that is the one that will be found and reused from here (even if this is a test method context)
    @Override
    public Object resolveParameter(ParameterContext pCtx, ExtensionContext ctx) {
        final ServerSteward steward = ServerSteward.getInstance(ctx);
        final Class<?> type = pCtx.getParameter().getType();
        return ParamType
                .forClass(type)
                .map(t -> {
                    switch (t) {
                        case BUS: return steward.getBus(ctx);
                        case CLIENT_ORB: return steward.getClientOrb();
                        case ROOT_POA: return getActivatedRootPoa(steward.getClientOrb());
                        default: throw Assertions.failf("Unknown parameter type: %s", t);
                    }
                })
                .orElseGet(() -> steward.resolveParameter(type));
    }

    @Override
    public void beforeTestExecution(ExtensionContext ctx) { ServerSteward.getInstance(ctx).beforeTestExecution(ctx); }

    @Override
    public void afterTestExecution(ExtensionContext ctx) { ServerSteward.getInstance(ctx).afterTestExecution(ctx); }

    @Override
    public void afterAll(ExtensionContext ctx) { ServerSteward.getInstance(ctx).afterAll(ctx); }
}

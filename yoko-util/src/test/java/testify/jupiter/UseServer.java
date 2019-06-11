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

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.omg.CORBA.ORB;
import testify.bus.Bus;
import testify.jupiter.MultiServerExtension.MultiServerSupport;
import testify.parts.PartRunner;
import testify.parts.Server;
import testify.util.Stack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;
import java.util.function.Function;

// annotations
@Repeatable(UseServer.Container.class)
@ExtendWith(SingleServerExtension.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseServer {
    @ExtendWith(MultiServerExtension.class)
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Container {
        UseServer[] value();
    }
    Class<? extends Server> value();
    String name() default "server";
    boolean forkProcess() default false;
    String[] orbProps() default {};
    String[] orbArgs() default {};
    String traceSpec() default "default";
}

class DelegatingExtension<K, V> implements Extension {
    private final Function<ExtensionContext, K> keyFactory;
    private final Function<K, V> valueFactory;

    DelegatingExtension(Function<ExtensionContext, K> keyFactory, Function<K, V> valueFactory) {
        this.keyFactory = keyFactory;
        this.valueFactory = valueFactory;
    }

    V getDelegate(ExtensionContext context) {
        K key = keyFactory.apply(context);
        // bus not available yet so enable below statement to debug
        if (false) System.out.printf("%s invoked for %s%n", Stack.getCallingFrame(1), key);
        Namespace namespace = Namespace.create(key);
        final Store store = context.getStore(namespace);
        return (V) store.getOrComputeIfAbsent(key, valueFactory);
    }
}

class SingleServerClient {
    final PartRunner partRunner;
    final UseServer config;
    ORB orb;

    SingleServerClient(Class<?> testClass) {
        if (!testClass.isAnnotationPresent(UseServer.class))
            throw new IllegalStateException("The test " + testClass + " needs to use the @" + UseServer.class.getSimpleName() + " annotation");
        UseServer annotation = testClass.getAnnotation(UseServer.class);
        this.partRunner = PartRunner.create().useProcesses(annotation.forkProcess());
        this.config = annotation;
        String traceSpec = "default".equals(config.traceSpec()) ? config.value().getName() : config.traceSpec();
        partRunner.enableLogging(traceSpec, config.name());

    }

    ORB getOrb() {
        if (orb == null) {
            orb = ORB.init((String[]) null, props());
            partRunner.endWith("client", bus -> {
                bus.log("Calling orb.shutdown(true)");
                orb.shutdown(true);
                bus.log("orb shutdown complete, calling orb.destroy()");
                orb.destroy();
                bus.log("clientOrb destroyed");

            });
        }
        return orb;
    }

    Properties props(String...orbProps) {
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        for (String prop : orbProps) {
            String[] arr = prop.split("=", 2);
            props.put(arr[0], arr.length < 2 ? "" : arr[1]);
        }
        return props;
    }

    Bus getBus() {
        return partRunner.bus(config.name());
    }

    void startServer() {
        Server.launch(partRunner, config.value(), config.name(), props(config.orbProps()), config.orbArgs());
    }

    void stopServer() {
        partRunner.join();
    }
}

class SingleServerExtension extends DelegatingExtension<Class<?>, SingleServerClient> implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    SingleServerExtension() {
        super(ExtensionContext::getRequiredTestClass, SingleServerClient::new);
    }

    @Override
    public void beforeAll(ExtensionContext ctx) throws Exception {
        getDelegate(ctx).startServer();
    }

    @Override
    public void afterAll(ExtensionContext ctx) throws Exception {
        getDelegate(ctx).stopServer();
    }

    @Override
    public boolean supportsParameter(ParameterContext pCtx, ExtensionContext ctx) {
        return pCtx.getParameter().getType() == Bus.class || pCtx.getParameter().getType() == ORB.class;
    }

    @Override
    public Object resolveParameter(ParameterContext pCtx, ExtensionContext ctx)  {
        final Class<?> type = pCtx.getParameter().getType();
        if (type == Bus.class) return getDelegate(ctx).getBus();
        if (type == ORB.class) return getDelegate(ctx).getOrb();
        throw new ParameterResolutionException("Unexpected type: " + type);
    }
}

class MultiServerExtension extends DelegatingExtension<Class<?>, MultiServerSupport> implements ParameterResolver {

    static class MultiServerSupport {
        final PartRunner runner;

        MultiServerSupport(Class<?> testClass) {
            runner = PartRunner.create();
        }

    }

    MultiServerExtension() {
        super(ExtensionContext::getRequiredTestClass, MultiServerSupport::new);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == Bus.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (extensionContext.getTestClass().isPresent())
            throw new ParameterResolutionException("Bus parameter requested by " + extensionContext.getDisplayName() +
                    " cannot be resolved. Either use exactly one @" + UseServer.class.getSimpleName() +
                    " annotation or annotate test methods with @" + TestPerServer.class.getSimpleName() + ".");
        return getDelegate(extensionContext).runner.bus();
    }
}

@interface TestPerServer {}

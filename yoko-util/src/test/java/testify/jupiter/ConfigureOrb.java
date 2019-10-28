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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.omg.CORBA.ORB;
import org.omg.PortableInterceptor.ORBInitializer;
import testify.jupiter.OrbSteward.NullIiopConnectionHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.util.Arrays.asList;
import static testify.jupiter.OrbSteward.getOrb;

@ExtendWith(OrbExtension.class)
@Target({ANNOTATION_TYPE, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigureOrb {
    String args() default "";
    String props() default "";
    Class<? extends ORBInitializer>[] initialize() default {};
    Class<?> iiopConnectionHelper() default NullIiopConnectionHelper.class;
}

class OrbSteward extends Steward<ConfigureOrb> {
    static interface NullIiopConnectionHelper{}
    private static final String[] TEMPLATE_STRING_ARRAY = {};
    final ORB orb;
    private OrbSteward(Class<?> testClass) {
        super(ConfigureOrb.class);
        ConfigureOrb cfg = getAnnotation(testClass);
        this.orb = ORB.init(args(cfg), props(cfg)); }

    @Override
    // A CloseableResource stored in a context store is closed automatically when the context goes out of scope.
    // Note this happens *before* the correlated extension callback points (e.g. AfterEachCallback/AfterAllCallback)
    public void close() {
        orb.shutdown(true);
        orb.destroy();
    }

    /** Extract the orb arguments from a {@link ConfigureOrb} annotation */
    static String[] args(ConfigureOrb cfg) {
        List<String> args = new ArrayList<>(asList(cfg.args().split(" ")));
        // if an iiop connection helper has been provided, add the appropriate arg
        if (cfg.iiopConnectionHelper() != NullIiopConnectionHelper.class) {
            args.add("-IIOPconnectionHelper");
            args.add(cfg.iiopConnectionHelper().getName());
        }
        return args.toArray(TEMPLATE_STRING_ARRAY);
    }
    /** Extract the orb properties from a {@link ConfigureOrb} annotation */
    static Properties props(ConfigureOrb cfg) {
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        for (String prop : cfg.props().split(" ")) {
            if (prop.isEmpty()) continue;
            String[] arr = prop.split("=", 2);
            props.put(arr[0], arr.length < 2 ? "" : arr[1]);
        }
        // add initializer properties for each specified initializer class
        for (Class<? extends ORBInitializer> initializer: cfg.initialize()) {
            String name = ORBInitializer.class.getName() + "Class." + initializer.getName();
            // blow up if this has been specified twice since this suggests a configuration error
            Assertions.assertFalse(props.contains(name), initializer.getName() + " should only be configured in one place");
            props.put(name, "true");
        }
        return props;
    }

    /** Get a client ORB */
    static ORB getOrb(ExtensionContext ctx) { return Steward.getInstanceForContext(ctx, OrbSteward.class, OrbSteward::new).orb; }
}

/** Must be registered using the {@link ExtendWith} annotation */
class OrbExtension implements Extension, BeforeAllCallback, SimpleParameterResolver<ORB> {
    @Override
    // to ensure the ORB is created only once per test class, create the steward here
    public void beforeAll(ExtensionContext ctx) throws Exception { getOrb(ctx); }

    @Override
    // assume that any child class of org.omg.CORBA.ORB will be satisfied
    public boolean supportsParameter(ParameterContext ctx) { return ORB.class.isAssignableFrom(ctx.getParameter().getType()); }

    @Override
    // get the configured ORB for the context,
    // but if the context has a test method, use its parent instead
    // i.e. get an ORB for the test class, not for each test method
    public ORB resolveParameter(ExtensionContext ctx) { return getOrb(ctx.getTestMethod().flatMap(m -> ctx.getParent()).orElse(ctx)); }
}
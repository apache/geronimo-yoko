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
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import testify.jupiter.annotation.Summoner;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;
import testify.util.ArrayUtils;
import testify.util.Predicates;

import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.ModifierSupport.isPublic;
import static org.junit.platform.commons.support.ModifierSupport.isStatic;
import static testify.streams.Collectors.requireNoMoreThanOne;

class OrbSteward implements ExtensionContext.Store.CloseableResource {
    private static final Class<?> CONNECTION_HELPER_CLASS;
    private static final Class<?> EXTENDED_CONNECTION_HELPER_CLASS;

    static {
        try {
            CONNECTION_HELPER_CLASS = Class.forName("org.apache.yoko.orb.OCI.IIOP.ConnectionHelper");
            EXTENDED_CONNECTION_HELPER_CLASS = Class.forName("org.apache.yoko.orb.OCI.IIOP.ExtendedConnectionHelper");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    private final String orbName;

    @SuppressWarnings("unused")
    interface NullIiopConnectionHelper {
    }

    private final ConfigureOrb annotation;
    private ORB orb;

    OrbSteward(ConfigureOrb annotation) {
        this.annotation = annotation;
        this.orbName = annotation.value();
    }

    /**
     * Get the unique ORB for the supplied test context
     */
    private synchronized ORB getOrbInstance(ExtensionContext ctx) {
        if (orb != null) return orb;
        Class<?> testClass = ctx.getRequiredTestClass();
        this.orb = ORB.init(args(annotation, testClass, this::isOrbModifier), props(annotation, testClass, this::isOrbModifier));
        return this.orb;
    }

    @Override
    // A CloseableResource stored in a context store is closed automatically when the context goes out of scope.
    // Note this happens *before* the correlated extension callback points (e.g. AfterEachCallback/AfterAllCallback)
    public synchronized void close() {
        if (orb == null) return;
        orb.shutdown(true);
        orb.destroy();
        orb = null;
    }

    private boolean isOrbModifier(Class<?> c) {
        return findAnnotation(c, UseWithOrb.class)
                .map(UseWithOrb::value)
                .filter(orbName::matches)
                .isPresent();
    }

    /**
     * Check that the supplied types are known types to be used as ORB extensions
     */
    private static void validateOrbModifierType(Class<?> type) {
        assertTrue(isStatic(type), "Class " + type.getName() + " should be static");
        assertTrue(isPublic(type), "Class " + type.getName() + " should be public");
        // we know about ORB initializers
        if (ORBInitializer.class.isAssignableFrom(type)) return;
        // we also know about ConnectionHelpers
        if (CONNECTION_HELPER_CLASS.isAssignableFrom(type)) return;
        if (EXTENDED_CONNECTION_HELPER_CLASS.isAssignableFrom(type)) return;
        // we don't know about anything else!
        fail("Type " + type + " cannot be used with an ORB");
    }

    /**
     * Extract the orb arguments from a {@link ConfigureOrb} annotation
     */
    static String[] args(ConfigureOrb cfg, Class<?> testClass, Predicate<Class<?>> nestedClassFilter) {
        return ArrayUtils.concat(
                getNestedModifierTypes(testClass, nestedClassFilter)
                        .filter(Predicates.anyOf(
                                CONNECTION_HELPER_CLASS::isAssignableFrom,
                                EXTENDED_CONNECTION_HELPER_CLASS::isAssignableFrom))
                        .collect(requireNoMoreThanOne("Only one connection helper can be configured but two were supplied: %s, %s"))
                        .map(c -> ArrayUtils.concat(cfg.args(), "-IIOPconnectionHelper", c.getName()))
                        .orElse(cfg.args()),
                cfg.nameService().args);
    }

    private static Stream<Class<?>> getNestedModifierTypes(Class<?> testClass, Predicate<Class<?>> nestedClassFilter) {
        return Stream.of(testClass.getClasses())
                .filter(nestedClassFilter)
                .peek(OrbSteward::validateOrbModifierType);
    }

    /**
     * Extract the orb properties from a {@link ConfigureOrb} annotation.
     *
     * @param cfg               the annotation to inspect for properties
     * @param testClass         the test class on which the annotation is specified
     * @param nestedClassFilter a boolean predicate to test whether to process the nested annotation types
     */
    static Properties props(ConfigureOrb cfg, Class<?> testClass, Predicate<Class<?>> nestedClassFilter) {
        Properties props = new Properties();
        props.put("yoko.orb.id", cfg.value());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        for (String prop : cfg.props()) {
            if (prop.isEmpty()) continue;
            String[] arr = prop.split("=", 2);
            props.put(arr[0], arr.length < 2 ? "" : arr[1]);
        }
        // add initializer properties for each specified initializer class
        //noinspection unchecked
        getNestedModifierTypes(testClass, nestedClassFilter)
                .filter(ORBInitializer.class::isAssignableFrom)
                .forEachOrdered(initializer -> addORBInitializerProp(props, (Class<? extends ORBInitializer>) initializer));
        // add initializer property for name service if configured
        cfg.nameService().getInitializerClass().ifPresent(c -> addORBInitializerProp(props, c));
        return props;
    }

    private static void addORBInitializerProp(Properties props, Class<? extends ORBInitializer> initializer) {
        String name = ORBInitializer.class.getName() + "Class." + initializer.getName();
        // blow up if this has been specified twice since this suggests a configuration error
        Assertions.assertFalse(props.contains(name), initializer.getName() + " should only be configured in one place");
        props.put(name, "true");
    }

    private static final Summoner<ConfigureOrb, OrbSteward> SUMMONER = Summoner.forAnnotation(ConfigureOrb.class, OrbSteward.class, OrbSteward::new);

    static ORB getOrb(ExtensionContext ctx) {
        return SUMMONER.forContext(ctx).requestSteward()
                .map(steward -> steward.getOrbInstance(ctx))
                .orElseThrow(Error::new); // error in framework, not calling code
    }

    static ORB getOrb(ExtensionContext ctx, ConfigureOrb config) {
        return SUMMONER.forContext(ctx).requireSteward(config).getOrbInstance(ctx);
    }

    static POA getActivatedRootPoa(ORB orb) {
        try {
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();
            return rootPOA;
        } catch (InvalidName invalidName) {
            throw new ParameterResolutionException("Could not resolve initial reference \"RootPOA\"");
        } catch (AdapterInactive adapterInactive) {
            throw new ParameterResolutionException("Could not activate POA manager for root POA", adapterInactive);
        }
    }
}

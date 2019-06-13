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

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.omg.CORBA.ORB;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Supplier;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

@ExtendWith(OrbExtension.class)
@Target({ANNOTATION_TYPE, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Orb {
    String[] props() default {};
    String[] args() default {};
    boolean reuse() default true;
}

class OrbExtension implements Extension, ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext pCtx, ExtensionContext ctx) {
        // assume that any child class of org.omg.CORBA.ORB will be satisfied
        return ORB.class.isAssignableFrom(pCtx.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext pCtx, ExtensionContext ctx) {
        final Optional<Orb> pCfg = pCtx.findAnnotation(Orb.class);
        final Optional<Orb> mCfg = ctx.getTestMethod().flatMap(this::config);
        final Optional<Orb> cCfg = ctx.getTestClass().flatMap(this::config);


        if (pCfg.isPresent() || mCfg.isPresent()) {
            Orb config = pCfg.orElseGet(mCfg::get);
            return newOrb(ctx, config);
        }

        Orb config = cCfg.orElseThrow(this::throwNoConfig);
        if (config.reuse()) {
            return getOrb(ctx.getRoot(), config);
        } else {
            return newOrb(ctx, config);
        }

    }

    private ORB newOrb(Orb config) {
        return ORB.init(config.args(), props(config.props()));
    }

    private Optional<Orb> config(AnnotatedElement elem) {
        return findAnnotation(elem, Orb.class);
    }

    public ParameterResolutionException throwNoConfig() {
        return new ParameterResolutionException(
                "This parameter or method or class must have an @" + Orb.class.getSimpleName() + " annotation");
    }

    public ORB newOrb(ExtensionContext ctx, Orb config) {
        ORB orb = newOrb(config);
        ctx.getStore(Namespace.create(OrbExtension.class)).put(orb, new OrbCloser(orb));
        return orb;
    }

    public ORB getOrb(ExtensionContext ctx, Orb config) {
        return ctx.getStore(Namespace.create(OrbExtension.class)).getOrComputeIfAbsent("ORB", k -> newOrb(config), ORB.class);
    }

    static class OrbCloser implements AutoCloseable {
        final ORB orb;
        OrbCloser(ORB orb) {this.orb = orb;}

        @Override
        public void close() throws Exception {
            orb.shutdown(true);
            orb.destroy();
        }
    }

    static Properties props(String...orbProps) {
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        for (String prop : orbProps) {
            String[] arr = prop.split("=", 2);
            props.put(arr[0], arr.length < 2 ? "" : arr[1]);
        }
        return props;
    }
}
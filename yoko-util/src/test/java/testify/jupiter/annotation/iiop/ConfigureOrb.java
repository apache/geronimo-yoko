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

import org.apache.yoko.orb.spi.naming.NameServiceInitializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.omg.PortableInterceptor.ORBInitializer;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.NONE;

@ExtendWith(OrbExtension.class)
@Target({ANNOTATION_TYPE, TYPE})
@Retention(RUNTIME)
@Inherited
public @interface ConfigureOrb {
    enum NameService {
        NONE,
        READ_ONLY(NameServiceInitializer.class, NameServiceInitializer.NS_REMOTE_ACCESS_ARG, "readOnly"),
        READ_WRITE(NameServiceInitializer.class, NameServiceInitializer.NS_REMOTE_ACCESS_ARG, "readWrite");
        final String[] args;
        private final Class<? extends ORBInitializer> initializerClass;

        NameService() {
            this.args = new String[0];
            this.initializerClass = null;
        }

        NameService(Class<? extends ORBInitializer> initializerClass, String...args) {
            this.args = args;
            this.initializerClass = initializerClass;
        }

        Optional<Class<? extends ORBInitializer>> getInitializerClass() {
            return Optional.ofNullable(initializerClass);
        }
    }

    String value() default "orb";
    String[] args() default "";
    String[] props() default "";
    NameService nameService() default NONE;


    @Target({ANNOTATION_TYPE, TYPE})
    @Retention(RUNTIME)
    @interface UseWithOrb {
        // TODO: maybe set the initializer classes in the ORB config
        // TODO: use enums to identify ORBs
        // TODO: configure differently for @ConfigureServer
        String value() default ".*";
    }
}

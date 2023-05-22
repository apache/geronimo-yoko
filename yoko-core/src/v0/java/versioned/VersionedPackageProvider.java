/*
 * Copyright 2021 IBM Corporation and others.
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
package versioned;

import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.PackageProvider;

import java.lang.reflect.InvocationTargetException;

/**
 * This should allow testing class resolution via the provider registry.
 */
public class VersionedPackageProvider extends PackageProvider {
    private static LocalFactory LOCAL_FACTORY = new LocalFactory() {
        @Override
        public Class<?> forName(String clsName) throws ClassNotFoundException {
            return Class.forName(clsName);
        }

        @Override
        public Object newInstance(Class cls) throws InstantiationException, IllegalAccessException {
            try {
                return cls.getConstructor().newInstance();
            } catch (InvocationTargetException|NoSuchMethodException e) {
                throw (InstantiationException) new InstantiationException().initCause(e);
            }
        }
    };

    public VersionedPackageProvider() {
        super(LOCAL_FACTORY, "versioned");
    }
}

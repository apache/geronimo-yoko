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
package org.apache.yoko.rmispec.util;

import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;

public final class Activator extends AbstractBundleActivator {
    private enum MyLocalFactory implements LocalFactory {
        INSTANCE;
        @Override
        public Class<?> forName(String clsName) throws ClassNotFoundException {
            return Class.forName(clsName);
        }

        @Override
        public Object newInstance(Class cls) throws IllegalAccessException {
            // no Info objects are passed to the activator's parent constructor
            // so no service instances can be requested
            throw new IllegalAccessException("Cannot instantiate class " + cls);
        }
    }

    public Activator() {
        super(MyLocalFactory.INSTANCE,
                "javax.rmi",
                "javax.rmi.CORBA",
                "org.omg.stub.java.rmi");
    }
}

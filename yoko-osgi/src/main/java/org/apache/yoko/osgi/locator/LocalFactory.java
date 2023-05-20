/*
 * Copyright 2018 IBM Corporation and others.
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
package org.apache.yoko.osgi.locator;

public interface LocalFactory {
    /** Implement using {@link Class#forName(String)} <em>within the target bundle</em> to avoid security issues. */
    Class<?> forName(String clsName) throws ClassNotFoundException;

    /** Implement using {@link Class#newInstance()} <em>within the target bundle</em> to avoid security issues. */
    Object newInstance(Class cls) throws InstantiationException, IllegalAccessException;
}

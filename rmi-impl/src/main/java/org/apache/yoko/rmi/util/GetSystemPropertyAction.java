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

package org.apache.yoko.rmi.util;

import java.security.PrivilegedAction;

/**
 * Simple utility class for retrieving a system property
 * value using the AccessController.
 */
public class GetSystemPropertyAction implements PrivilegedAction {
    // property name to retrieve
    String name;
    // potential default value
    String defaultValue = null;

    /**
     * Retrive a value using the name with no default value.
     *
     * @param name   The property name.
     */
    public GetSystemPropertyAction(String name) {
        this.name = name;
        this.defaultValue = null;
    }

    /**
     * Retrieve a property using a name and a specified default value.
     *
     * @param name   The property name.
     * @param defaultValue
     *               The default value if the property has not been set.
     */
    public GetSystemPropertyAction(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Perform the AccessController action of retrieving the system property.
     *
     * @return The retrieved property.  Returns either null or the
     *         specified default value if this has not been set.
     */
    public java.lang.Object run() {
        if (defaultValue == null) {
            return System.getProperty(name);
        }
        else {
            return System.getProperty(name, defaultValue);
        }
    }
}


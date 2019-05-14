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
package test.parts;

import static test.parts.SerialUtil.stringify;
import static test.parts.SerialUtil.unstringify;

public interface UserBus extends Bus {
    String get(String user, String key);
    String getOwn(String key);
    default Object get(Enum<?> key) { return unstringify(getFullyQualifiedName(key)); }
    default Object get(String user, Enum<?> key) { return unstringify(get(user, getFullyQualifiedName(key))); }
    default Object getOwn(Enum<?>key) { return unstringify(getOwn(getFullyQualifiedName(key))); }
    default void put(Enum<?>key) { put(key, key); }
    default void put(Enum<?>key, Object value) { put(getFullyQualifiedName(key), stringify(value)); }

    static String getFullyQualifiedName(Enum<?> e) { return e.getDeclaringClass().getName() + '.' + e.name(); }
}

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
package testify.util;

import java.util.HashMap;
import java.util.Map;

public enum Maps {
    ;
    private static <K, V> Map<K, V> put(Map<K, V> map, K k, V v) { map.put(k, v); return map; }
    public static <K, V> Map<K, V> of(K k, V v) { return put(new HashMap<>(), k, v);}
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) { return put(of(k1, v1), k2, v2); }
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) { return put(of(k1, v1, k2, v2), k3, v3); }
}

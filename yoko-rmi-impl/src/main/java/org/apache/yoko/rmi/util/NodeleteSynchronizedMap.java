/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NodeleteSynchronizedMap implements Map {
    Map global;

    ThreadLocal local = new ThreadLocal() {
        public Object initialValue() {
            return NodeleteSynchronizedMap.this.initialValue();
        }
    };

    public NodeleteSynchronizedMap() {
        global = Collections.synchronizedMap(initialValue());
    }

    private Map localMap() {
        return (Map) local.get();
    }

    public int size() {
        return global.size();
    }

    public boolean isEmpty() {
        return global.isEmpty();
    }

    public boolean containsKey(java.lang.Object key) {
        Map local = localMap();
        if (local.containsKey(key)) {
            return true;
        }

        if (global.containsKey(key)) {
            local.put(key, global.get(key));
            return true;
        } else {
            return false;
        }
    }

    public boolean containsValue(java.lang.Object val) {
        Map local = localMap();
        if (local.containsValue(val)) {
            return true;
        }

        return global.containsValue(val);
    }

    public java.lang.Object get(java.lang.Object key) {
        Map local = localMap();
        Object val = local.get(key);
        if (val != null)
            return val;

        if (local.containsKey(key)) {
            return null;
        }

        val = global.get(key);
        if (val != null) {
            local.put(key, val);
        }

        return val;
    }

    public boolean equals(java.lang.Object other) {
        return global.equals(other);
    }

    public int hashCode() {
        return global.hashCode();
    }

    public java.util.Set keySet() {
        return global.keySet();
    }

    public java.util.Collection values() {
        return global.values();
    }

    public java.util.Set entrySet() {
        return global.entrySet();
    }

    public java.lang.Object put(java.lang.Object key, java.lang.Object val) {
        return global.put(key, val);
    }

    public java.lang.Object remove(java.lang.Object key) {
        throw new UnsupportedOperationException();
    }

    public void putAll(java.util.Map other) {
        global.putAll(other);
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Map initialValue() {
        return new HashMap();
    }
}

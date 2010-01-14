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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Vector;
import java.util.WeakHashMap;

public class ClassLoaderLocal {

    private Object key = new Object();

    private static class ClassLocalMap extends HashMap {
        ClassLocalMap() {
        }
    }

    private static final ClassLocalMap globalMap = new ClassLocalMap();

    private String name;

    public ClassLoaderLocal() {
        this("<anonymous>");
    }

    public ClassLoaderLocal(String name) {
        this.name = name;
    }

    public Object initialValue() {
        return null;
    }
    
    public void set(Object o) {
        ClassLocalMap map = getLoaderLocalMap();
        synchronized (map) {
            map.put(key, o);
        }
    }
    
    public void setGlobal(Object o) {
        synchronized (globalMap) {
            globalMap.put(key, o);
        }
    }
    
    public Object getGlobal() {
        return getOrCreate(globalMap);
    }

    public Object get() {
        return getOrCreate(getLoaderLocalMap());
    }


    private Object getOrCreate(ClassLocalMap globalMap) {
        synchronized (globalMap) {
            if (globalMap.containsKey(key)) {
                return globalMap.get(key);
            } else {
                Object init = initialValue();
                globalMap.put(key, init);
                return init;
            }
        }
    }
    
    // table for tracking the CL to map relationships.  We're
    // using a WeakHashMap to prevent us pinning class loaders. 
    static private WeakHashMap localMaps = new WeakHashMap(); 

    private ClassLocalMap getLoaderLocalMap() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        ClassLocalMap map;
        if (cl == null) {
            map = globalMap;
        } else {
            synchronized (localMaps) {
                // create a local map and store in our tracking table. 
                map = (ClassLocalMap)localMaps.get(cl); 
                if (map == null) {
                    map = new ClassLocalMap();
                    localMaps.put(cl, map); 
                }
            }
        }
        return map;
    }
}

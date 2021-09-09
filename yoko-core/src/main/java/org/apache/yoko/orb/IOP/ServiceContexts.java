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
package org.apache.yoko.orb.IOP;

import org.apache.yoko.util.IntegerComparator;
import org.omg.IOP.ServiceContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableMap;

public final class ServiceContexts implements Iterable<ServiceContext> {
    private static final ServiceContext[] EMPTY_SERVICE_CONTEXT_ARRAY = {};
    public static final ServiceContexts EMPTY = unmodifiable(EMPTY_SERVICE_CONTEXT_ARRAY);

    private final MutableServiceContexts mutableContexts;
    private final Collection<ServiceContext> contexts;

    public ServiceContexts() {
        this(new TreeMap<Integer, ServiceContext>(IntegerComparator.UNSIGNED));
    }

    public ServiceContexts(Iterable<ServiceContext> contexts) {
        this();
        for (ServiceContext sc: contexts) this.mutableContexts.add(sc);
    }

    /**
     * Create an unmodifiable list of service contexts
     */
    public static ServiceContexts unmodifiable(ServiceContext...contexts) {
        return new ServiceContexts(unmodifiableMap(asMap(contexts)));
    }

    private static Map<Integer, ServiceContext> asMap(ServiceContext...contexts) {
        Map<Integer, ServiceContext> map = new TreeMap<>(IntegerComparator.UNSIGNED);
        for (ServiceContext context: contexts) map.put(context.context_id, context);
        return map;
    }


    private ServiceContexts(Map<Integer, ServiceContext> contexts) {
        mutableContexts = new MutableServiceContexts(contexts);
        this.contexts = unmodifiableCollection(contexts.values());
    }

    public int size() { return this.contexts.size();}

    @Override
    public Iterator<ServiceContext> iterator() { return this.contexts.iterator(); }

    public boolean isEmpty() {return contexts.isEmpty();}

    public MutableServiceContexts mutable() { return mutableContexts; }

    public ServiceContext get(int id) {return mutableContexts.get(id);}

    public ServiceContext[] toArray() { return contexts.toArray(EMPTY_SERVICE_CONTEXT_ARRAY); }

    @Override
    public String toString() {
        return "ServiceContexts" + contexts;
    }
}

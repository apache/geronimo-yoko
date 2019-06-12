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
package testify.jupiter;

import testify.bus.Bus;
import testify.parts.Server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class MultiServerClient extends ServerlessClient {
    final Map<String, UseServer> configMap;

    static MultiServerClient create(Class<?> testClass) {
        if (!testClass.isAnnotationPresent(MultiServer.class))
            throw new IllegalStateException("The test " + testClass + " should have multiple @" + UseServer.class.getSimpleName() + " annotations");
        return new MultiServerClient(testClass.getAnnotation(MultiServer.class).value());
    }

    MultiServerClient(UseServer...configs) {
        super(false);
        // count up how many of each name we have
        Map<String, AtomicInteger> nameCount = new HashMap<>();
        for (UseServer config: configs) {
            nameCount.computeIfAbsent(config.name(), s -> new AtomicInteger()).incrementAndGet();
        }
        final Collection<AtomicInteger> counts = nameCount.values();
        // throw away any names from our count that are already unique
        counts.removeAll(counts.stream().filter(a -> a.get() == 1).collect(toList()));
        // and reset any others to zero
        counts.forEach(a -> a.set(0));

        // create the config map of unique part names to configs
        // use a linked hash map as this preserves insertion order
        Map<String, UseServer> map = new LinkedHashMap<>();
        for (UseServer config: configs) {
            String name = config.name();
            if (nameCount.containsKey(name)) name += "#" + nameCount.get(name).incrementAndGet();
            map.put(name, config);
        }
        this.configMap = Collections.unmodifiableMap(map);
    }

    Stream<Bus> getBuses() { return configMap.keySet().stream().map(partRunner::bus); }

    void startServers() {
        configMap.forEach(this::startServer);
    }

    void startServer(String name, UseServer config) {
        // set the forking mode from the config
        partRunner.useProcesses(config.forkProcesses());
        // launch it with the computed name
        Server.launch(partRunner, config.value(), name, props(config.orbProps()), config.orbArgs());
    }
}

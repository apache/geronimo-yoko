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

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import testify.bus.Bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MultiServerExtension.class)
@ConfigurePartRunner

public @interface ConfigureMultiServer {
    ConfigureServer[] value();
}

class MultiServerSteward extends Steward<ConfigureMultiServer> {
    final List<ServerSteward> stewards;

    private MultiServerSteward(Class<?> testClass) {
        super(ConfigureMultiServer.class, ConfigureServer.class);
        ConfigureServer[] configs = getAnnotation(testClass).value();
        // count up how many of each name we have
        Map<String, AtomicInteger> nameCount = new HashMap<>();
        for (ConfigureServer config: configs) {
            nameCount.computeIfAbsent(config.name(), s -> new AtomicInteger()).incrementAndGet();
        }
        final Collection<AtomicInteger> counts = nameCount.values();
        // throw away any names from our count that are already unique
        counts.removeAll(counts.stream().filter(a -> a.get() == 1).collect(toList()));
        // and reset any others to zero
        counts.forEach(a -> a.set(0));

        // create the config map of unique part names to configs
        // use a linked hash map as this preserves insertion order
        this.stewards = Stream.of(configs)
                .map(cfg -> {
                    String name = cfg.name();
                    if (nameCount.containsKey(name)) name += "#" + nameCount.get(name).incrementAndGet();
                    return new ServerSteward(cfg, name);
                })
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    Stream<Bus> buses(ExtensionContext ctx) { return stewards.stream().map(s -> s.getBus(ctx)); }

    void startServers(ExtensionContext ctx) { stewards.forEach(s -> s.startServer(ctx)); }

    static MultiServerSteward getInstance(ExtensionContext ctx) {
        return Steward.getInstanceForContext(ctx, MultiServerSteward.class, MultiServerSteward::new);
    }
}

class MultiServerExtension implements BeforeAllCallback, SimpleArgumentsProvider<Bus> {
    @Override
    public void beforeAll(ExtensionContext ctx) { MultiServerSteward.getInstance(ctx).startServers(ctx); }

    @Override
    public Stream<Bus> provideArgs(ExtensionContext ctx) { return MultiServerSteward.getInstance(ctx).buses(ctx); }
}

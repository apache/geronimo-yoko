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
package testify.bus;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface BusWrapper extends Bus {
    RawBus bus();

    String transform(String key);

    @Override
    default String get(String key) { return bus().get(transform(key)); }

    @Override
    default void onMsg(String key, Consumer<String> action) { bus().onMsg(transform(key), action);}

    @Override
    default Bus forUser(String user) { return bus().forUser(user); }

    @Override
    default void forEach(BiConsumer<String, String> action) { bus().forEach(action); }

    @Override
    default void put(String key, String value) { bus().put(transform(key), value); }

    @Override
    default void log(LogLevel level, String message) { Bus.super.log(level, transform(message)); }
}


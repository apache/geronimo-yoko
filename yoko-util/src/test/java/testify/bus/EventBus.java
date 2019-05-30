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

import java.util.function.Consumer;

public interface EventBus extends RawBus {
    interface TypeRef<T> {
        Class<? extends Enum> getDeclaringClass();
        String name();
        default T unstringify(String s) { return (T) SerialUtil.unstringify(s); }
        default String stringify(T t) { return SerialUtil.stringify(t); }
        default String fullName() { return getDeclaringClass().getTypeName() + '.' + name(); }
    }

    default <K extends Enum<K> & TypeRef<T>, T> T get(K key) { return key.unstringify(get(key.fullName())); }
    default <K extends Enum<K> & TypeRef<? super T>, T> void put(K key, T value) { put(key.fullName(), key.stringify(value)); }
    default <K extends Enum<K> & TypeRef<T>, T> void put(K key) { put(key, null); }
    default <K extends Enum<K> & TypeRef<T>, T> void onMsg(K key, Consumer<T> action) {
        onMsg(key.fullName(), s -> action.accept(key.unstringify(s)));
    }

    default <K extends Enum<K> & TypeRef<K>> void onMsg(K key, Runnable action) {
        onMsg(key, s -> action.run());
    }
}

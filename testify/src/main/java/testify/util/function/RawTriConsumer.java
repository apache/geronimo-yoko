/*
 * Copyright 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package testify.util.function;

import org.opentest4j.AssertionFailedError;

/** A consumer of three things. */
@FunctionalInterface
public interface RawTriConsumer<T,U,V> {
    void acceptRaw(T t, U u, V v) throws Exception;

    default void accept(T t, U u, V v) {
        try {
            acceptRaw(t, u, v);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionFailedError("", e);
        }
    }

    default RawTriConsumer<T, U, V> andThen(RawTriConsumer<? super T, ? super U, ? super V> after) {
        return (t,u,v) -> {
            acceptRaw(t, u, v);
            after.acceptRaw(t, u, v);
        };
    }

    default RawBiConsumer<U,V> curry(T t) { return (u,v) -> acceptRaw(t, u, v);}
}

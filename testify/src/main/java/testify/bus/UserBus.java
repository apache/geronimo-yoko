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
package testify.bus;

import testify.streams.BiStream;

import java.util.function.Consumer;

// Although some of the methods here are fluent in design
// (i.e. they return a Bus object suitable for method chaining)
// the internal implementations are expected to return null.
// The fluency is an affordance purely for the code calling
// objects accessible outside the package.
@SuppressWarnings("UnusedReturnValue")
interface UserBus {
    String user();
    Bus put(String key, String value);
    boolean hasKey(String key);
    String peek(String key);
    String get(String key);
    Bus onMsg(String key, Consumer<String> action);
    BiStream<String, String> biStream();
}

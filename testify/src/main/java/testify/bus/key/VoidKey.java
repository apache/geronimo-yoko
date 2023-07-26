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
package testify.bus.key;

/**
 * A specialised type spec that does not support any value (other than <code>null</code>).
 * This is intended to be used as a signal in itself, e.g. for syncing between threads.
 */
public interface VoidKey extends TypeKey<Void> {
    default String stringify(Void v) { return "null"; }
    default Void unstringify(String s) { return null; }
}

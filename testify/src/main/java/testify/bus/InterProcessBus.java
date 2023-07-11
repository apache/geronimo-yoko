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

import testify.io.EasyCloseable;

@SuppressWarnings("UnusedReturnValue")
public interface InterProcessBus extends SimpleBus, EasyCloseable {
    InterProcessBus addProcess(String name, Process proc);

    /**
     * Allow a parent process to communicate with its child processes.
     */
    static InterProcessBus createParent() { return new InterProcessBusImpl(true); }

    /**
     * Create a child process that uses its {@link System#in} and {@link System#out}
     * to communicate with its parent process.
     */
    // By returning only a SimpleBus, we prevent a child process from nesting further children.
    // This is probably for the best! Revisit if necessary.
    static SimpleBus createChild() { return new InterProcessBusImpl(false); }
}

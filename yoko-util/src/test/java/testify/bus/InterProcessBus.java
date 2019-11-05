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

import testify.io.EasyCloseable;

@SuppressWarnings("UnusedReturnValue")
public interface InterProcessBus extends SimpleBus, EasyCloseable {
    InterProcessBus addProcess(String name, Process proc);

    /**
     * Allow a master (parent) process to communicate with its slave (child) processes.
     */
    static InterProcessBus createMaster() { return new InterProcessBusImpl(true); }

    /**
     * Create a slave (child) process that uses its {@link System#in} and {@link System#out}
     * to communicate with its master (parent) process.
     */
    // By returning only a SimpleBus, we prevent a child process from nesting further children.
    // This is probably for the best! Revisit if necessary.
    static SimpleBus createSlave() { return new InterProcessBusImpl(false); }
}

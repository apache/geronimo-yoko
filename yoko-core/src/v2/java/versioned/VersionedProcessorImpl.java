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
package versioned;

import testify.bus.Bus;

import java.rmi.RemoteException;

/**
 * This class is loaded from a child class loader
 * providing an invocation context for any remote method calls.
 */
public class VersionedProcessorImpl extends acme.ProcessorImpl implements VersionedProcessor {
    public VersionedProcessorImpl(Bus bus) { super(bus); }

    @Override
    public String getVersion() throws RemoteException {
        return "v2";
    }
}

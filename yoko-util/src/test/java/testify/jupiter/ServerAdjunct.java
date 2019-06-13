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
import testify.parts.ServerPart;

class ServerAdjunct extends ServerlessAdjunct {
    final Server config;

    static ServerAdjunct create(Class<?> testClass) {
        if (!testClass.isAnnotationPresent(Server.class))
            throw new IllegalStateException("The test " + testClass + " needs to use the @" + Server.class.getSimpleName() + " annotation");
        return new ServerAdjunct(testClass.getAnnotation(Server.class));
    }

    ServerAdjunct(Server config) {
        super(config.forkProcesses());
        this.config = config;
        String traceSpec = "default".equals(config.trace()) ? config.value().getName() : config.trace();
        partRunner.enableLogging(traceSpec, config.name());
    }

    Bus getBus() {
        return partRunner.bus(config.name());
    }

    void startServer() {
        ServerPart.launch(partRunner, config.value(), config.name(), OrbExtension.props(config.orbProps()), config.orbArgs());
    }
}

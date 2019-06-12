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
 */package testify.jupiter;

import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.omg.CORBA.ORB;
import testify.parts.PartRunner;

import java.util.Properties;

class ServerlessClient implements CloseableResource {
    final PartRunner partRunner;
    ORB orb;

    static ServerlessClient create(Class<?> testClass) {
        if (!testClass.isAnnotationPresent(Serverless.class))
            throw new IllegalStateException("The test " + testClass + " needs to use the @" + Serverless.class.getSimpleName() + " annotation");
        return new ServerlessClient(testClass.getAnnotation(Serverless.class));
    }

    public ServerlessClient(Serverless config) {
        this(config.forkProcesses());
        partRunner.enableLogging(config.trace());
    }

    ServerlessClient(boolean forkProcesses) { this.partRunner = PartRunner.create().useProcesses(forkProcesses); }

    @Override
    public void close() throws Throwable {
        // A CloseableResource stored in a context store is closed automatically when the context goes out of scope.
        // Note this happens *before* the correlated extension callback points (e.g. AfterEachCallback/AfterAllCallback)
        partRunner.join();
    }

    ORB getOrb() {
        if (orb == null) {
            orb = ORB.init((String[]) null, props());
            partRunner.endWith("client", bus -> {
                bus.log("Calling orb.shutdown(true)");
                orb.shutdown(true);
                bus.log("orb shutdown complete, calling orb.destroy()");
                orb.destroy();
                bus.log("clientOrb destroyed");
            });
        }
        return orb;
    }

    static Properties props(String...orbProps) {
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        for (String prop : orbProps) {
            String[] arr = prop.split("=", 2);
            props.put(arr[0], arr.length < 2 ? "" : arr[1]);
        }
        return props;
    }
}

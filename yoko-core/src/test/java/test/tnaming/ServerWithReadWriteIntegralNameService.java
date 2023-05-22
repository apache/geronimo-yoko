/*
 * Copyright 2015 IBM Corporation and others.
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
package test.tnaming;

import static org.apache.yoko.orb.spi.naming.NameServiceInitializer.NS_ORB_INIT_PROP;
import static org.apache.yoko.orb.spi.naming.NameServiceInitializer.NS_REMOTE_ACCESS_ARG;
import static org.apache.yoko.orb.spi.naming.RemoteAccess.readWrite;

import java.util.Properties;

public class ServerWithReadWriteIntegralNameService {
    public static void main(String args[]) throws Exception {
        final String refFile = args[0];
        System.out.println("1");
        Properties props = new Properties();
        System.out.println("2");
        props.put(NS_ORB_INIT_PROP, "");
        System.out.println("3");
        props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port " + Util.NS_PORT);
        System.out.println("4");
        try (Server s = new Server(refFile, props, NS_REMOTE_ACCESS_ARG, readWrite.name())) {
            System.out.println("5");
            s.bindObjectFactories();
            s.run();
            
        }
    }
}

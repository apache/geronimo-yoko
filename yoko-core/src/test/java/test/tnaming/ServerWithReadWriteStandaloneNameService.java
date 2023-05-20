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

import java.util.Properties;

import org.apache.yoko.orb.CosNaming.tnaming.TransientNameService;

public class ServerWithReadWriteStandaloneNameService {
    public static void main(String args[]) throws Exception {
        final String refFile = args[0];
        try (TransientNameService service = new TransientNameService("localhost", Util.NS_PORT)) {
            System.out.println("Starting transient name service");
            service.run();
            System.out.println("Transient name service started");
            Properties props = new Properties();
            props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port " + (Util.NS_PORT+1));
            props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
            props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
            
            try (Server s = new Server(refFile, props, "-ORBInitRef", "NameService=" + Util.NS_LOC)) {
                s.run();
            }
        }
    }
}

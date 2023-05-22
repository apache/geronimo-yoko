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
package test.pi;

import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableInterceptor.*;

final public class Collocated {
    public static void main(String[] args) throws Exception {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");

        ORB orb = null;

        try {
            Client.ClientRegisterInterceptors(props, true);
            Server.ServerRegisterInterceptors(props);

            props.put("yoko.orb.id", "myORB");
            orb = ORB.init(args, props);
            Server.ServerRun(orb, true, args);
            Client.ClientRun(orb, true, args);

            //
            // The ORB must be totally shutdown before the servants
            // are deleted.
            //
            orb.shutdown(true);

            Server.ServerCleanup();
        } finally {
            if (orb != null) {
                orb.destroy();
            }
        }

    }
}

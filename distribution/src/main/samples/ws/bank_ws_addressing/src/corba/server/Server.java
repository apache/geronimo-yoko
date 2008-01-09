/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package corba.server;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;

import org.apache.yoko.orb.OB.BootManager;
import org.apache.yoko.orb.OB.BootManagerHelper;
import org.apache.yoko.orb.OB.BootManagerPackage.AlreadyExists;

public class Server
{
    static int
    run(ORB orb, String[] args) throws UserException
    {
        //
        // Resolve Root POA
        //
        POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

        //
        // Get a reference to the POA manager
        //
        POAManager manager = rootPOA.the_POAManager();

        //
        // Create implementation object
        //
        Bank_impl bankImpl = new Bank_impl(orb, rootPOA);
        Bank bank = bankImpl._this(orb);

        // Add reference to the boot manager
        try {
            byte[] oid = ("Bank").getBytes();
            BootManager bootManager = BootManagerHelper.narrow(
                orb.resolve_initial_references("BootManager"));
            bootManager.add_binding(oid, bank);
        } catch (InvalidName ex) {
            throw new RuntimeException();
        } catch (AlreadyExists ex) {
            throw new RuntimeException();
        }
        
        //
        // Run implementation
        //
        manager.activate();
        orb.run();

        return 0;
    }

    public static void
    main(String args[])
    {
        Properties props = System.getProperties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                  "org.apache.yoko.orb.CORBA.ORBSingleton");
        props.put("yoko.orb.id", "Bank-Server");
        // for this demo, start on localhost, port 40000
        props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port 40000");
                

        int status = 0;
        ORB orb = null;

        try
        {
            orb = ORB.init(args, props);
            status = run(orb, args);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            status = 1;
        }

        if(orb != null)
        {
            try
            {
                orb.destroy();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}

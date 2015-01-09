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

package test.pi;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.POAManagerPackage.*;
import org.omg.PortableInterceptor.*;

import java.io.*;

public final class Server extends test.common.TestBase {
    private static TestInterface_impl impl;

    private static TestInterfaceDSI_impl dsiImpl;

    private static String refFile = "TestInterface.ref";

    private static TestLocator_impl locatorImpl;

    static void ServerRegisterInterceptors(java.util.Properties props) {
        props.put("org.omg.PortableInterceptor.ORBInitializerClass."
                + "test.pi.ServerORBInitializer_impl", "");
    }

    static int ServerRun(ORB orb, boolean nonBlocking, String[] args)
            throws org.omg.CORBA.UserException {
    	try {
    		Class.forName("test.pi.ServerORBInitializer_impl");
    	}
    	catch(ClassNotFoundException e) {
    		e.printStackTrace();
    	}
        //
        // Resolve Root POA
        //
        POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

        //
        // Activate the POA manager
        //
        POAManager manager = poa.the_POAManager();
        manager.activate();

        //
        // This will use ORB::create_policy to create all the POA policies
        // and our custom policy
        //
        Any any = orb.create_any();

        //
        // Create policies for the POA
        //
        Policy[] policies = new Policy[6];

        LifespanPolicyValueHelper.insert(any, LifespanPolicyValue.PERSISTENT);
        policies[0] = orb.create_policy(LIFESPAN_POLICY_ID.value, any);
        IdAssignmentPolicyValueHelper.insert(any,
                IdAssignmentPolicyValue.USER_ID);
        policies[1] = orb.create_policy(ID_ASSIGNMENT_POLICY_ID.value, any);
        RequestProcessingPolicyValueHelper.insert(any,
                RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
        policies[2] = orb
                .create_policy(REQUEST_PROCESSING_POLICY_ID.value, any);
        ServantRetentionPolicyValueHelper.insert(any,
                ServantRetentionPolicyValue.NON_RETAIN);
        policies[3] = orb.create_policy(SERVANT_RETENTION_POLICY_ID.value, any);
        ImplicitActivationPolicyValueHelper.insert(any,
                ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        policies[4] = orb.create_policy(IMPLICIT_ACTIVATION_POLICY_ID.value,
                any);
        any.insert_long(10);
        policies[5] = orb.create_policy(MY_SERVER_POLICY_ID.value, any);

        POA persistentPOA = poa.create_POA("persistent", manager, policies);

        //
        // Create implementation objects
        //
        TestInterface_impl impl = new TestInterface_impl(orb, persistentPOA);
        byte[] oid = ("test").getBytes();
        org.omg.CORBA.Object objImpl = persistentPOA.create_reference_with_id(
                oid, "IDL:TestInterface:1.0");
        TestInterfaceDSI_impl dsiImpl = new TestInterfaceDSI_impl(orb,
                persistentPOA);
        oid = ("testDSI").getBytes();
        org.omg.CORBA.Object objDSIImpl = persistentPOA
                .create_reference_with_id(oid, "IDL:TestInterface:1.0");

        locatorImpl = new TestLocator_impl(orb, impl, dsiImpl);
        ServantLocator locator = locatorImpl._this(orb);
        persistentPOA.set_servant_manager(locator);

        //
        // Save references. This must be done after POA manager
        // activation, otherwise there is a potential for a race
        // condition between the client sending request and the server
        // not being ready yet.
        //
        try {
            FileOutputStream file = new FileOutputStream(refFile);
            PrintWriter out = new PrintWriter(file);

            String strimpl = orb.object_to_string(objImpl);
            out.println(strimpl);

            strimpl = orb.object_to_string(objDSIImpl);
            out.println(strimpl);

            out.flush();
            file.close();
        } catch (IOException ex) {
            System.err.println("Can't write to `" + ex.getMessage() + "'");
            return 1;
        }

        org.omg.IOP.CodecFactory factory = org.omg.IOP.CodecFactoryHelper
                .narrow(orb.resolve_initial_references("CodecFactory"));
        assertTrue(factory != null);

        ServerRequestInterceptor interceptor = new ServerTestInterceptor_impl(
                orb, factory);
        ServerORBInitializer_impl.serverProxyManager.setInterceptor(0,
                interceptor);

        if (!nonBlocking) {
            //
            // Give up control to the ORB
            //
            orb.run();

            //
            // Clean up
            //
            ServerCleanup();
        }

        return 0;
    }

    static void ServerCleanup() {
        File file = new File(refFile);
        file.delete();
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            ServerRegisterInterceptors(props);

            props.put("yoko.orb.id", "myORB");
            orb = ORB.init(args, props);
            status = ServerRun(orb, false, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}

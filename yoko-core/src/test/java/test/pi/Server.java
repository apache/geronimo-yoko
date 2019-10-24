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

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryHelper;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;
import org.omg.PortableServer.IMPLICIT_ACTIVATION_POLICY_ID;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicyValueHelper;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValueHelper;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.LifespanPolicyValueHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicyValueHelper;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValueHelper;
import test.common.TestBase;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

import static org.junit.Assert.*;

public final class Server extends TestBase {
    private static String refFile = "TestInterface.ref";

    private static TestLocator_impl locatorImpl;

    static void ServerRegisterInterceptors(Properties props) {
        props.put("org.omg.PortableInterceptor.ORBInitializerClass." + "test.pi.ServerORBInitializer_impl", "");
    }

    static void ServerRun(ORB orb, boolean nonBlocking, String[] args) throws Exception {
        try (PrintWriter out = new PrintWriter(new FileWriter(refFile))) {
            try {
                Object c = Class.forName("test.pi.ServerORBInitializer_impl");
                System.out.println("Got class " + c);
                //
                // Resolve Root POA
                //
                POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
                System.out.println("Got root POA");
                //
                // Activate the POA manager
                //
                POAManager manager = poa.the_POAManager();
                manager.activate();
                System.out.println("Activated root poa manager");
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
                IdAssignmentPolicyValueHelper.insert(any, IdAssignmentPolicyValue.USER_ID);
                policies[1] = orb.create_policy(ID_ASSIGNMENT_POLICY_ID.value, any);
                RequestProcessingPolicyValueHelper.insert(any, RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
                policies[2] = orb.create_policy(REQUEST_PROCESSING_POLICY_ID.value, any);
                ServantRetentionPolicyValueHelper.insert(any, ServantRetentionPolicyValue.NON_RETAIN);
                policies[3] = orb.create_policy(SERVANT_RETENTION_POLICY_ID.value, any);
                ImplicitActivationPolicyValueHelper.insert(any, ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
                policies[4] = orb.create_policy(IMPLICIT_ACTIVATION_POLICY_ID.value, any);
                any.insert_long(10);
                policies[5] = orb.create_policy(MY_SERVER_POLICY_ID.value, any);

                POA persistentPOA = poa.create_POA("persistent", manager, policies);

                //
                // Create implementation objects
                //
                TestInterface_impl impl = new TestInterface_impl(orb, persistentPOA);
                byte[] oid = ("test").getBytes();
                org.omg.CORBA.Object objImpl = persistentPOA.create_reference_with_id(oid, "IDL:TestInterface:1.0");
                TestInterfaceDSI_impl dsiImpl = new TestInterfaceDSI_impl(orb, persistentPOA);
                oid = ("testDSI").getBytes();
                org.omg.CORBA.Object objDSIImpl = persistentPOA.create_reference_with_id(oid, "IDL:TestInterface:1.0");

                locatorImpl = new TestLocator_impl(orb, impl, dsiImpl);
                ServantLocator locator = locatorImpl._this(orb);
                persistentPOA.set_servant_manager(locator);

                CodecFactory factory = CodecFactoryHelper.narrow(orb.resolve_initial_references("CodecFactory"));
                assertTrue(factory != null);

                ServerRequestInterceptor interceptor = new ServerTestInterceptor_impl(orb, factory);
                ServerORBInitializer_impl.serverProxyManager.setInterceptor(0, interceptor);

                System.out.println("About to write refs");
                
                //
                // Save references. This must be done after POA manager
                // activation, otherwise there is a potential for a race
                // condition between the client sending request and the server
                // not being ready yet.
                //
                writeRef(orb, out, objImpl);
                writeRef(orb, out, objDSIImpl);
                out.flush();
                System.out.println("Wrote refs");

                
                if (!nonBlocking) {
                    //
                    // Give up control to the ORB
                    //
                    System.out.println("About to call orb.run()");
                    orb.run();
                    System.out.println("orb.run() returned");
                    //
                    // Clean up
                    //
                    ServerCleanup();
                }

                return;
            } catch (Throwable e) {
                e.printStackTrace(out);
                e.printStackTrace(System.out);
                throw e;
            }
        }
    }

    protected static void writeRef(ORB orb, PrintWriter out, org.omg.CORBA.Object objImpl) {
        out.println("ref:");
        out.println(orb.object_to_string(objImpl));
    }

    static void ServerCleanup() {
        File file = new File(refFile);
        file.delete();
    }

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");

        ORB orb = null;

        try {
            ServerRegisterInterceptors(props);

            props.put("yoko.orb.id", "myORB");
            orb = ORB.init(args, props);
            ServerRun(orb, false, args);
        } finally {
            if (orb != null) {
                orb.destroy();
            }
        }
    }
}

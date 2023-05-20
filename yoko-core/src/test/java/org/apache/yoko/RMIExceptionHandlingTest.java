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
package org.apache.yoko;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.rmi.PortableRemoteObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import test.rmi.exceptionhandling.MyAppException;
import test.rmi.exceptionhandling.MyClientRequestInterceptor;
import test.rmi.exceptionhandling.MyRuntimeException;
import test.rmi.exceptionhandling.MyServerRequestInterceptor;
import test.rmi.exceptionhandling.Thrower;
import test.rmi.exceptionhandling.ThrowerImpl;
import test.rmi.exceptionhandling._ThrowerImpl_Tie;

@SuppressWarnings("serial")
public class RMIExceptionHandlingTest {
    private static ORB serverOrb;
    private static ORB clientOrb;
    private static String ior;
    private static MyAppException mae = null;
    private static MyRuntimeException mre = null;

    private static ORB initOrb(Properties props, String... args) {
        return ORB.init(args, props);
    }

    private static void initExceptions() {
        if (mae == null) mae = new MyAppException();
        if (mre == null) mre = new MyRuntimeException();
    }

    @BeforeClass
    public static void createServerORB() throws Exception {
        initExceptions();
        ThrowerImpl.myAppException = mae;
        ThrowerImpl.myRuntimeException = mre;
        serverOrb = initOrb(new Properties() {{
            put("org.omg.PortableInterceptor.ORBInitializerClass." + MyClientRequestInterceptor.class.getName(),"");
            put("org.omg.PortableInterceptor.ORBInitializerClass." + MyServerRequestInterceptor.class.getName(),"");
        }});
        POA poa = POAHelper.narrow(serverOrb.resolve_initial_references("RootPOA"));
        poa.the_POAManager().activate();

        _ThrowerImpl_Tie tie = new _ThrowerImpl_Tie();
        tie.setTarget(new ThrowerImpl(serverOrb));

        poa.activate_object(tie);
        ior = serverOrb.object_to_string(tie.thisObject());
        System.out.println(ior);
    }

    @BeforeClass
    public static void createClientORB() {
        clientOrb = initOrb(new Properties() {{
            put("org.omg.PortableInterceptor.ORBInitializerClass." + MyClientRequestInterceptor.class.getName(),"");
        }});
    }

    @AfterClass
    public static void shutdownServerORB() {
        serverOrb.shutdown(true);
        serverOrb.destroy();
        ior = null;
    }

    @AfterClass
    public static void shutdownClientORB() {
        clientOrb.shutdown(true);
        clientOrb.destroy();
    }

    @Test(expected=MyRuntimeException.class)
    public void testRuntimeException() throws RemoteException {
        getThrower(clientOrb).throwRuntimeException();
    }

    @Test(expected=MyAppException.class)
    public void testAppException() throws RemoteException, MyAppException {
        getThrower(clientOrb).throwAppException();
    }

    private Thrower getThrower(ORB orb) {
        Object o = orb.string_to_object(ior);
        Thrower thrower = (Thrower) PortableRemoteObject.narrow(o, Thrower.class);
        return thrower;
    }

    public static void main(String...args) throws Exception {
        if (0 == args.length) {
            System.out.println("Starting server");
            mae = new MyAppException();
            mre = new MyRuntimeException();
            createServerORB();
            System.out.println(serverOrb.getClass().getName());
            serverOrb.run();
        } else {
            System.out.println("Starting client");
            ior = args[0];
            createClientORB();
            System.out.println(clientOrb.getClass().getName());

            RMIExceptionHandlingTest test = new RMIExceptionHandlingTest();
            try {
                test.testRuntimeException();
                throw new Exception("no exception seen");
            } catch (MyRuntimeException e) {
            }
        }
    }
}

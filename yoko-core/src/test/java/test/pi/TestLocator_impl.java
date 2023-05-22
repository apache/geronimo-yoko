/*
 * Copyright 2010 IBM Corporation and others.
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

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

//
// ServantLocator implementation to provide location forwarding
//
final class TestLocator_impl extends ServantLocatorPOA {
    private ORB orb_;

    private TestInterface_impl test_;

    private TestInterfaceDSI_impl testDSI_;

    TestLocator_impl(ORB orb, TestInterface_impl test,
            TestInterfaceDSI_impl testDSI) {
        orb_ = orb;
        test_ = test;
        testDSI_ = testDSI;
    }

    public Servant preinvoke(byte[] oid, POA poa, String operation,
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder the_cookie)
            throws org.omg.PortableServer.ForwardRequest {
        String oidString = new String(oid);

        //
        // Request for object "test" or "testDSI"
        //
        if (oidString.equals("test") || oidString.equals("testDSI")) {
            //
            // Location forward requested? Location forward back to
            // the same object. (The client-side interceptor consumes
            // the location forward).
            //
            if (operation.equals("location_forward")) {
                org.omg.CORBA.Object obj = poa.create_reference_with_id(oid,
                        "IDL:TestInterface:1.0");
                throw new org.omg.PortableServer.ForwardRequest(obj);
            }

            if (oidString.equals("test"))
                return test_;
            return testDSI_;
        }

        //
        // Fail
        //
        throw new OBJECT_NOT_EXIST();
    }

    public void postinvoke(byte[] oid, POA poa, String operation,
            java.lang.Object the_cookie, Servant the_servant) {
    }
}

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
package test.poa;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.ServantLocatorPackage.*;

final class TestDSIRef_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB orb_;

    private String name_;

    private boolean compare_;

    private boolean defaultServant_;

    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    TestDSIRef_impl(ORB orb, String name, boolean compare) {
        orb_ = orb;
        name_ = name;
        compare_ = compare;
    }

    void setDefaultServant(boolean b) {
        defaultServant_ = b;
    }

    //
    // Standard IDL to Java Mapping
    //

    public void invoke(ServerRequest request) {
        String name = request.operation();

        if (!name.equals("aMethod")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            Any any = orb_.create_any();
            BAD_OPERATIONHelper.insert(any, new BAD_OPERATION());
            request.set_exception(any);
            return;
        }

        //
        // 8.3.1: "Unless it calls set_exception, the DIR must call arguments
        // exactly once, even if the operation signature contains no
        // parameters."
        //
        NVList list = orb_.create_list(0);
        request.arguments(list);

        org.omg.CORBA.Object currentObj = null;
        try {
            currentObj = orb_.resolve_initial_references("POACurrent");
        } catch (UserException ex) {
        }

        org.omg.PortableServer.Current current = null;
        if (currentObj != null)
            current = org.omg.PortableServer.CurrentHelper.narrow(currentObj);
        TEST(current != null);

        byte[] oid = null;
        try {
            oid = current.get_object_id();
        } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
            throw new RuntimeException();
        }
        String oidString = new String(oid);

        if (compare_)
            TEST(oidString.equals(name_));

        org.omg.PortableServer.Servant servant = null;
        try {
            servant = current.get_servant();
        } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
            throw new RuntimeException();
        }
        TEST(servant == this);

        if (defaultServant_) {
            POA poa = null;
            try {
                poa = current.get_POA();
            } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
                throw new RuntimeException();
            }
            byte[] servantId = null;
            try {
                servantId = poa.servant_to_id(this);
            } catch (ServantNotActive ex) {
                throw new RuntimeException();
            } catch (WrongPolicy ex) {
                throw new RuntimeException();
            }
            TEST(servantId.length == oid.length);
            TEST(servantId.equals(oid));
        }
    }

    static final String[] interfaces_ = { "IDL:Test:1.0" };

    public String[] _all_interfaces(POA poa, byte[] oid) {
        return interfaces_;
    }
}

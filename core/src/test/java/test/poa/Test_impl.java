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

package test.poa;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

final class Test_impl extends TestPOA {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    private POA poa_;

    org.omg.PortableServer.Current current_;

    private String name_;

    private boolean compare_;

    Test_impl(ORB orb, String name, boolean compare) {
        name_ = name;
        compare_ = compare;

        org.omg.CORBA.Object currentObj = null;

        try {
            currentObj = orb.resolve_initial_references("POACurrent");
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
        }

        TEST(currentObj != null);
        current_ = org.omg.PortableServer.CurrentHelper.narrow(currentObj);
        TEST(current_ != null);
    }

    Test_impl(ORB orb, POA poa) {
        poa_ = poa;
        name_ = "";
        compare_ = false;

        org.omg.CORBA.Object currentObj = null;

        try {
            currentObj = orb.resolve_initial_references("POACurrent");
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
        }

        TEST(currentObj != null);
        current_ = org.omg.PortableServer.CurrentHelper.narrow(currentObj);
        TEST(current_ != null);
    }

    public void aMethod() {
        if (compare_) {
            byte[] oid = null;
            try {
                oid = current_.get_object_id();
            } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
                throw new RuntimeException();
            }

            String oidString = new String(oid);

            TEST(oidString.equals(name_));
        }
    }

    public POA _default_POA() {
        if (poa_ != null)
            return poa_;
        return super._default_POA();
    }
}

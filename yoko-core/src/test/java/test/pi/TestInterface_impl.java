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

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import test.pi.TestInterfacePackage.*;

final class TestInterface_impl extends TestInterfacePOA {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    private ORB orb_;

    private POA poa_;

    private org.omg.PortableInterceptor.Current current_;

    TestInterface_impl(ORB orb, POA poa) {
        orb_ = orb;
        poa_ = poa;

        try {
            org.omg.CORBA.Object obj = orb
                    .resolve_initial_references("PICurrent");
            current_ = org.omg.PortableInterceptor.CurrentHelper.narrow(obj);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
        }
        TEST(current_ != null);
    }

    // ----------------------------------------------------------------------
    // TestInterface_impl public member implementation
    // ----------------------------------------------------------------------

    public void noargs() {
    }

    public void noargs_oneway() {
    }

    public void systemexception() {
        throw new NO_IMPLEMENT();
    }

    public void userexception() throws user {
        throw new user();
    }

    public void location_forward() {
        TEST(false);
    }

    public void test_service_context() {
        //
        // Test: get_slot
        //
        Any slotData = null;
        try {
            slotData = current_.get_slot(0);
        } catch (org.omg.PortableInterceptor.InvalidSlot ex) {
            TEST(false);
        }
        int v = slotData.extract_long();
        TEST(v == 10);

        //
        // Test: set_slot
        //
        slotData.insert_long(20);
        try {
            current_.set_slot(0, slotData);
        } catch (org.omg.PortableInterceptor.InvalidSlot ex) {
            TEST(false);
        }
    }

    public String string_attrib() {
        return "TEST";
    }

    public void string_attrib(String param) {
        TEST(param.equals("TEST"));
    }

    public void one_string_in(String param) {
        TEST(param.equals("TEST"));
    }

    public void one_string_inout(StringHolder param) {
        TEST(param.value.equals("TESTINOUT"));
        param.value = "TEST";
    }

    public void one_string_out(StringHolder param) {
        param.value = "TEST";
    }

    public String one_string_return() {
        return "TEST";
    }

    public s struct_attrib() {
        s r = new s();
        r.sval = "TEST";
        return r;
    }

    public void struct_attrib(s param) {
        TEST(param.sval.equals("TEST"));
    }

    public void one_struct_in(s param) {
        TEST(param.sval.equals("TEST"));
    }

    public void one_struct_inout(sHolder param) {
        param.value.sval = "TEST";
    }

    public void one_struct_out(sHolder param) {
        param.value = new s();
        param.value.sval = "TEST";
    }

    public s one_struct_return() {
        s r = new s();
        r.sval = "TEST";
        return r;
    }

    public void deactivate() {
        System.out.println("TestInterface_Impl.deactivate() - calling orb.shutdown(false)");
        orb_.shutdown(false);
        System.out.println("TestInterface_Impl.deactivate() - returned from orb.shutdown(false)");
    }

    public POA _default_POA() {
        return poa_;
    }
}

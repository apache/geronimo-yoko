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
import test.pi.TestInterfacePackage.*;

final class TestInterfaceDSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
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

    TestInterfaceDSI_impl(ORB orb, POA poa) {
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
    // TestInterfaceDSI_impl public member implementation
    // ----------------------------------------------------------------------

    static final String[] interfaces_ = { "IDL:TestInterface:1.0" };

    public String[] _all_interfaces(POA poa, byte[] oid) {
        return interfaces_;
    }

    public boolean _is_a(String name) {
        if (name.equals("IDL:TestInterface:1.0")) {
            return true;
        }

        return super._is_a(name);
    }

    public void invoke(ServerRequest request) {
        String name = request.operation();

        if (name.equals("noargs")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            return;
        }

        if (name.equals("noargs_oneway")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            return;
        }

        if (name.equals("systemexception")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            Any result = orb_.create_any();
            NO_IMPLEMENTHelper.insert(result, new NO_IMPLEMENT());
            request.set_exception(result);
            return;
        }

        if (name.equals("userexception")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            Any result = orb_.create_any();
            userHelper.insert(result, new user());
            request.set_exception(result);
            return;
        }

        if (name.equals("location_forward")) {
            TEST(false);
            return;
        }

        if (name.equals("test_service_context")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

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

            return;
        }

        if (name.equals("_get_string_attrib")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            Any result = orb_.create_any();
            result.insert_string("TEST");
            request.set_result(result);

            return;
        }

        if (name.equals("_set_string_attrib")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(orb_.create_string_tc(0));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            String param = any.extract_string();
            TEST(param.equals("TEST"));

            return;
        }

        if (name.equals("one_string_in")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(orb_.create_string_tc(0));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            String param = any.extract_string();
            TEST(param.equals("TEST"));

            return;
        }

        if (name.equals("one_string_inout")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(orb_.create_string_tc(0));
            list.add_value("", any, org.omg.CORBA.ARG_INOUT.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            String param = any.extract_string();
            TEST(param.equals("TESTINOUT"));
            any.insert_string("TEST");

            return;
        }

        if (name.equals("one_string_out")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(orb_.create_string_tc(0));
            list.add_value("", any, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            any.insert_string("TEST");

            return;
        }

        if (name.equals("one_string_return")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            Any result = orb_.create_any();
            result.insert_string("TEST");
            request.set_result(result);

            return;
        }

        if (name.equals("_get_struct_attrib")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            s rc = new s();
            rc.sval = "TEST";

            Any result = orb_.create_any();
            sHelper.insert(result, rc);
            request.set_result(result);

            return;
        }

        if (name.equals("_set_struct_attrib")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(sHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            s param = sHelper.extract(any);
            TEST(param.sval.equals("TEST"));

            return;
        }

        if (name.equals("one_struct_in")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(sHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            s param = sHelper.extract(any);
            TEST(param.sval.equals("TEST"));

            return;
        }

        if (name.equals("one_struct_inout")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(sHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_INOUT.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            s param = sHelper.extract(any);
            TEST(param.sval.equals("TESTINOUT"));
            s rc = new s();
            rc.sval = "TEST";
            sHelper.insert(any, rc);

            return;
        }

        if (name.equals("one_struct_out")) {
            NVList list = orb_.create_list(0);
            Any any = orb_.create_any();
            any.type(sHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);

            try {
                any = list.item(0).value();
            } catch (Bounds ex) {
                TEST(false);
            }
            s rc = new s();
            rc.sval = "TEST";
            sHelper.insert(any, rc);

            return;
        }

        if (name.equals("one_struct_return")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            s rc = new s();
            rc.sval = "TEST";

            Any result = orb_.create_any();
            sHelper.insert(result, rc);
            request.set_result(result);

            return;
        }

        if (name.equals("deactivate")) {
            NVList list = orb_.create_list(0);
            request.arguments(list);

            orb_.shutdown(false);

            return;
        }

        System.err.println("DSI implementation: unknown operation: " + name);

        NVList list = orb_.create_list(0);
        request.arguments(list);

        Any exAny = orb_.create_any();
        BAD_OPERATIONHelper.insert(exAny, new BAD_OPERATION());
        request.set_exception(exAny);
    }
}

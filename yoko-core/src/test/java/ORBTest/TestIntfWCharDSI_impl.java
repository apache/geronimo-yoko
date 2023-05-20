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
package ORBTest;

import org.omg.CORBA.*;

class TestIntfWCharDSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB m_orb;

    private ORBTest_WChar.Intf m_ti;

    TestIntfWCharDSI_impl(ORB orb, ORBTest_WChar.Intf ti) {
        m_orb = orb;
        m_ti = ti;
    }

    static final String[] m_ids = { "IDL:ORBTest_WChar/Intf:1.0" };

    public String[] _all_interfaces(org.omg.PortableServer.POA poa,
            byte[] object_id) {
        return m_ids;
    }

    public void invoke(ServerRequest request) {
        String name = request.operation();

        boolean ex;
        if (name.length() > 2 && name.endsWith("Ex")) {
            name = name.substring(0, name.length() - 2);
            ex = true;
        } else {
            ex = false;
        }

        if (name.equals("_get_attrWChar")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            char ret = m_ti.attrWChar();

            Any any = m_orb.create_any();
            any.insert_wchar(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrWChar")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_wchar));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            char arg = any.extract_wchar();

            m_ti.attrWChar(arg);

            return;
        }

        if (name.equals("opWChar")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_wchar));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_wchar));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            char arg0 = any0.extract_wchar();
            CharHolder arg1 = new CharHolder();
            arg1.value = any1.extract_wchar();
            CharHolder arg2 = new CharHolder();

            char ret = m_ti.opWChar(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_WChar.ExWCharHelper.insert(exAny,
                        new ORBTest_WChar.ExWChar(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_wchar(ret);
                request.set_result(result);
                any1.insert_wchar(arg1.value);
                any2.insert_wchar(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrWString")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            String ret = m_ti.attrWString();

            Any any = m_orb.create_any();
            any.insert_wstring(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrWString")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_wstring));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            String arg = any.extract_wstring();

            m_ti.attrWString(arg);

            return;
        }

        if (name.equals("opWString")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_wstring));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_wstring));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            String arg0 = any0.extract_wstring();
            StringHolder arg1 = new StringHolder();
            arg1.value = any1.extract_wstring();
            StringHolder arg2 = new StringHolder();

            String ret = m_ti.opWString(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_WChar.ExWStringHelper.insert(exAny,
                        new ORBTest_WChar.ExWString(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_wstring(ret);
                request.set_result(result);
                any1.insert_wstring(arg1.value);
                any2.insert_wstring(arg2.value);
            }

            return;
        }

        System.err.println("DSI implementation: unknown operation: " + name);

        NVList list = m_orb.create_list(0);
        request.arguments(list);

        Any exAny = m_orb.create_any();
        BAD_OPERATIONHelper.insert(exAny, new BAD_OPERATION());
        request.set_exception(exAny);
    }
}

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

class TestIntfLongLongDSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB m_orb;

    private ORBTest_LongLong.Intf m_ti;

    TestIntfLongLongDSI_impl(ORB orb, ORBTest_LongLong.Intf ti) {
        m_orb = orb;
        m_ti = ti;
    }

    static final String[] m_ids = { "IDL:ORBTest_LongLong/Intf:1.0" };

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

        if (name.equals("_get_attrLongLong")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            long ret = m_ti.attrLongLong();

            Any any = m_orb.create_any();
            any.insert_longlong(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrLongLong")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_longlong));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            long arg = any.extract_longlong();

            m_ti.attrLongLong(arg);

            return;
        }

        if (name.equals("opLongLong")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_longlong));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_longlong));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            long arg0 = any0.extract_longlong();
            LongHolder arg1 = new LongHolder();
            arg1.value = any1.extract_longlong();
            LongHolder arg2 = new LongHolder();

            long ret = m_ti.opLongLong(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_LongLong.ExLongLongHelper.insert(exAny,
                        new ORBTest_LongLong.ExLongLong(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_longlong(ret);
                request.set_result(result);
                any1.insert_longlong(arg1.value);
                any2.insert_longlong(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrULongLong")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            long ret = m_ti.attrULongLong();

            Any any = m_orb.create_any();
            any.insert_ulonglong(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrULongLong")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_ulonglong));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            long arg = any.extract_ulonglong();

            m_ti.attrULongLong(arg);

            return;
        }

        if (name.equals("opULongLong")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_ulonglong));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_ulonglong));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            long arg0 = any0.extract_ulonglong();
            LongHolder arg1 = new LongHolder();
            arg1.value = any1.extract_ulonglong();
            LongHolder arg2 = new LongHolder();

            long ret = m_ti.opULongLong(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_LongLong.ExULongLongHelper.insert(exAny,
                        new ORBTest_LongLong.ExULongLong(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_ulonglong(ret);
                request.set_result(result);
                any1.insert_ulonglong(arg1.value);
                any2.insert_ulonglong(arg2.value);
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

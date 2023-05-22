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

class TestIntfBasicDSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB m_orb;

    private ORBTest_Basic.Intf m_ti;

    private Any aVariableUnion;

    TestIntfBasicDSI_impl(ORB orb, ORBTest_Basic.Intf ti) {
        m_orb = orb;
        m_ti = ti;
    }

    static final String[] m_ids = { "IDL:ORBTest_Basic/Intf:1.0" };

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

        if (name.equals("opVoid")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            if (ex) {
                Any any = m_orb.create_any();
                ORBTest_Basic.ExVoidHelper.insert(any,
                        new ORBTest_Basic.ExVoid());
                request.set_exception(any);
            }

            return;
        }

        if (name.equals("_get_attrShort")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            short ret = m_ti.attrShort();

            Any any = m_orb.create_any();
            any.insert_short(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrShort")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_short));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            short arg = any.extract_short();

            m_ti.attrShort(arg);

            return;
        }

        if (name.equals("opShort")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_short));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_short));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            short arg0 = any0.extract_short();
            ShortHolder arg1 = new ShortHolder();
            arg1.value = any1.extract_short();
            ShortHolder arg2 = new ShortHolder();

            short ret = m_ti.opShort(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExShortHelper.insert(exAny,
                        new ORBTest_Basic.ExShort(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_short(ret);
                request.set_result(result);
                any1.insert_short(arg1.value);
                any2.insert_short(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrUShort")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            short ret = m_ti.attrUShort();

            Any any = m_orb.create_any();
            any.insert_ushort(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrUShort")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_ushort));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            short arg = any.extract_ushort();

            m_ti.attrUShort(arg);

            return;
        }

        if (name.equals("opUShort")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_ushort));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_ushort));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            short arg0 = any0.extract_ushort();
            ShortHolder arg1 = new ShortHolder();
            arg1.value = any1.extract_ushort();
            ShortHolder arg2 = new ShortHolder();

            short ret = m_ti.opUShort(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExUShortHelper.insert(exAny,
                        new ORBTest_Basic.ExUShort(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_ushort(ret);
                request.set_result(result);
                any1.insert_ushort(arg1.value);
                any2.insert_ushort(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrLong")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            int ret = m_ti.attrLong();

            Any any = m_orb.create_any();
            any.insert_long(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrLong")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_long));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            int arg = any.extract_long();

            m_ti.attrLong(arg);

            return;
        }

        if (name.equals("opLong")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_long));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_long));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            int arg0 = any0.extract_long();
            IntHolder arg1 = new IntHolder();
            arg1.value = any1.extract_long();
            IntHolder arg2 = new IntHolder();

            int ret = m_ti.opLong(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExLongHelper.insert(exAny,
                        new ORBTest_Basic.ExLong(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_long(ret);
                request.set_result(result);
                any1.insert_long(arg1.value);
                any2.insert_long(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrULong")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            int ret = m_ti.attrULong();

            Any any = m_orb.create_any();
            any.insert_ulong(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrULong")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_ulong));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            int arg = any.extract_ulong();

            m_ti.attrULong(arg);

            return;
        }

        if (name.equals("opULong")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_ulong));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_ulong));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            int arg0 = any0.extract_ulong();
            IntHolder arg1 = new IntHolder();
            arg1.value = any1.extract_ulong();
            IntHolder arg2 = new IntHolder();

            int ret = m_ti.opULong(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExULongHelper.insert(exAny,
                        new ORBTest_Basic.ExULong(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_ulong(ret);
                request.set_result(result);
                any1.insert_ulong(arg1.value);
                any2.insert_ulong(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrFloat")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            float ret = m_ti.attrFloat();

            Any any = m_orb.create_any();
            any.insert_float(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrFloat")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_float));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            float arg = any.extract_float();

            m_ti.attrFloat(arg);

            return;
        }

        if (name.equals("opFloat")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_float));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_float));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            float arg0 = any0.extract_float();
            FloatHolder arg1 = new FloatHolder();
            arg1.value = any1.extract_float();
            FloatHolder arg2 = new FloatHolder();

            float ret = m_ti.opFloat(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExFloatHelper.insert(exAny,
                        new ORBTest_Basic.ExFloat(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_float(ret);
                request.set_result(result);
                any1.insert_float(arg1.value);
                any2.insert_float(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrDouble")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            double ret = m_ti.attrDouble();

            Any any = m_orb.create_any();
            any.insert_double(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrDouble")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_double));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            double arg = any.extract_double();

            m_ti.attrDouble(arg);

            return;
        }

        if (name.equals("opDouble")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_double));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_double));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            double arg0 = any0.extract_double();
            DoubleHolder arg1 = new DoubleHolder();
            arg1.value = any1.extract_double();
            DoubleHolder arg2 = new DoubleHolder();

            double ret = m_ti.opDouble(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExDoubleHelper.insert(exAny,
                        new ORBTest_Basic.ExDouble(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_double(ret);
                request.set_result(result);
                any1.insert_double(arg1.value);
                any2.insert_double(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrBoolean")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            boolean ret = m_ti.attrBoolean();

            Any any = m_orb.create_any();
            any.insert_boolean(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrBoolean")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_boolean));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            boolean arg = any.extract_boolean();

            m_ti.attrBoolean(arg);

            return;
        }

        if (name.equals("opBoolean")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_boolean));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_boolean));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            boolean arg0 = any0.extract_boolean();
            BooleanHolder arg1 = new BooleanHolder();
            arg1.value = any1.extract_boolean();
            BooleanHolder arg2 = new BooleanHolder();

            boolean ret = m_ti.opBoolean(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExBooleanHelper.insert(exAny,
                        new ORBTest_Basic.ExBoolean(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_boolean(ret);
                request.set_result(result);
                any1.insert_boolean(arg1.value);
                any2.insert_boolean(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrChar")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            char ret = m_ti.attrChar();

            Any any = m_orb.create_any();
            any.insert_char(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrChar")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_char));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            char arg = any.extract_char();

            m_ti.attrChar(arg);

            return;
        }

        if (name.equals("opChar")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_char));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_char));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            char arg0 = any0.extract_char();
            CharHolder arg1 = new CharHolder();
            arg1.value = any1.extract_char();
            CharHolder arg2 = new CharHolder();

            char ret = m_ti.opChar(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExCharHelper.insert(exAny,
                        new ORBTest_Basic.ExChar(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_char(ret);
                request.set_result(result);
                any1.insert_char(arg1.value);
                any2.insert_char(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrOctet")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            byte ret = m_ti.attrOctet();

            Any any = m_orb.create_any();
            any.insert_octet(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrOctet")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_octet));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            byte arg = any.extract_octet();

            m_ti.attrOctet(arg);

            return;
        }

        if (name.equals("opOctet")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_octet));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_octet));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            byte arg0 = any0.extract_octet();
            ByteHolder arg1 = new ByteHolder();
            arg1.value = any1.extract_octet();
            ByteHolder arg2 = new ByteHolder();

            byte ret = m_ti.opOctet(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExOctetHelper.insert(exAny,
                        new ORBTest_Basic.ExOctet(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_octet(ret);
                request.set_result(result);
                any1.insert_octet(arg1.value);
                any2.insert_octet(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrString")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            String ret = m_ti.attrString();

            Any any = m_orb.create_any();
            any.insert_string(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrString")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_string));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            String arg = any.extract_string();

            m_ti.attrString(arg);

            return;
        }

        if (name.equals("opString")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_string));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_string));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            String arg0 = any0.extract_string();
            StringHolder arg1 = new StringHolder();
            arg1.value = any1.extract_string();
            StringHolder arg2 = new StringHolder();

            String ret = m_ti.opString(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExStringHelper.insert(exAny,
                        new ORBTest_Basic.ExString(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_string(ret);
                request.set_result(result);
                any1.insert_string(arg1.value);
                any2.insert_string(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrAny")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any ret = m_ti.attrAny();

            Any any = m_orb.create_any();
            any.insert_any(ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrAny")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_any));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            Any arg = any.extract_any();

            m_ti.attrAny(arg);

            return;
        }

        if (name.equals("opAny")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(m_orb.get_primitive_tc(TCKind.tk_any));
            any1.type(m_orb.get_primitive_tc(TCKind.tk_any));
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            Any arg0 = any0.extract_any();
            AnyHolder arg1 = new AnyHolder();
            arg1.value = any1.extract_any();
            AnyHolder arg2 = new AnyHolder();

            Any ret = m_ti.opAny(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExAnyHelper.insert(exAny,
                        new ORBTest_Basic.ExAny(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                result.insert_any(ret);
                request.set_result(result);
                any1.insert_any(arg1.value);
                any2.insert_any(arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrTestEnum")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            ORBTest_Basic.TestEnum ret = m_ti.attrTestEnum();

            Any any = m_orb.create_any();
            ORBTest_Basic.TestEnumHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrTestEnum")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.TestEnumHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            ORBTest_Basic.TestEnum arg = (ORBTest_Basic.TestEnumHelper
                    .extract(any));

            m_ti.attrTestEnum(arg);

            return;
        }

        if (name.equals("opTestEnum")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.TestEnumHelper.type());
            any1.type(ORBTest_Basic.TestEnumHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            ORBTest_Basic.TestEnum arg0 = (ORBTest_Basic.TestEnumHelper
                    .extract(any0));
            ORBTest_Basic.TestEnumHolder arg1 = (new ORBTest_Basic.TestEnumHolder());
            arg1._read(any1.create_input_stream());
            ORBTest_Basic.TestEnumHolder arg2 = (new ORBTest_Basic.TestEnumHolder());

            ORBTest_Basic.TestEnum ret = m_ti.opTestEnum(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExTestEnumHelper.insert(exAny,
                        new ORBTest_Basic.ExTestEnum(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.TestEnumHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.TestEnumHelper.insert(any1, arg1.value);
                ORBTest_Basic.TestEnumHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrIntf")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            ORBTest_Basic.Intf ret = m_ti.attrIntf();

            Any any = m_orb.create_any();
            ORBTest_Basic.IntfHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrIntf")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.IntfHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            ORBTest_Basic.Intf arg = ORBTest_Basic.IntfHelper.extract(any);

            m_ti.attrIntf(arg);

            return;
        }

        if (name.equals("opIntf")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.IntfHelper.type());
            any1.type(ORBTest_Basic.IntfHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);

            ORBTest_Basic.Intf arg0 = ORBTest_Basic.IntfHelper.extract(any0);
            ORBTest_Basic.IntfHolder arg1 = new ORBTest_Basic.IntfHolder();
            arg1._read(any1.create_input_stream());
            ORBTest_Basic.IntfHolder arg2 = new ORBTest_Basic.IntfHolder();

            ORBTest_Basic.Intf ret = m_ti.opIntf(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExIntfHelper.insert(exAny,
                        new ORBTest_Basic.ExIntf(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.IntfHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.IntfHelper.insert(any1, arg1.value);
                ORBTest_Basic.IntfHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrFixedStruct")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            ORBTest_Basic.FixedStruct ret = m_ti.attrFixedStruct();

            Any any = m_orb.create_any();
            ORBTest_Basic.FixedStructHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrFixedStruct")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.FixedStructHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            ORBTest_Basic.FixedStruct arg = ORBTest_Basic.FixedStructHelper
                    .extract(any);

            m_ti.attrFixedStruct(arg);

            return;
        }

        if (name.equals("opFixedStruct")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.FixedStructHelper.type());
            any1.type(ORBTest_Basic.FixedStructHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            ORBTest_Basic.FixedStruct arg0 = (ORBTest_Basic.FixedStructHelper
                    .extract(any0));
            ORBTest_Basic.FixedStructHolder arg1 = (new ORBTest_Basic.FixedStructHolder());
            arg1.value = ORBTest_Basic.FixedStructHelper.extract(any1);
            ORBTest_Basic.FixedStructHolder arg2 = (new ORBTest_Basic.FixedStructHolder());

            ORBTest_Basic.FixedStruct ret = (m_ti.opFixedStruct(arg0, arg1,
                    arg2));

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExFixedStructHelper.insert(exAny,
                        new ORBTest_Basic.ExFixedStruct(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.FixedStructHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.FixedStructHelper.insert(any1, arg1.value);
                ORBTest_Basic.FixedStructHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrVariableStruct")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            ORBTest_Basic.VariableStruct ret = m_ti.attrVariableStruct();

            Any any = m_orb.create_any();
            ORBTest_Basic.VariableStructHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrVariableStruct")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.VariableStructHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            ORBTest_Basic.VariableStruct arg = (ORBTest_Basic.VariableStructHelper
                    .extract(any));

            m_ti.attrVariableStruct(arg);

            return;
        }

        if (name.equals("opVariableStruct")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.VariableStructHelper.type());
            any1.type(ORBTest_Basic.VariableStructHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            ORBTest_Basic.VariableStruct arg0 = (ORBTest_Basic.VariableStructHelper
                    .extract(any0));
            ORBTest_Basic.VariableStructHolder arg1 = (new ORBTest_Basic.VariableStructHolder());
            arg1.value = ORBTest_Basic.VariableStructHelper.extract(any1);
            ORBTest_Basic.VariableStructHolder arg2 = (new ORBTest_Basic.VariableStructHolder());

            ORBTest_Basic.VariableStruct ret = (m_ti.opVariableStruct(arg0,
                    arg1, arg2));

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExVariableStructHelper.insert(exAny,
                        new ORBTest_Basic.ExVariableStruct(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.VariableStructHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.VariableStructHelper.insert(any1, arg1.value);
                ORBTest_Basic.VariableStructHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrFixedUnion")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            ORBTest_Basic.FixedUnion ret = m_ti.attrFixedUnion();

            Any any = m_orb.create_any();
            ORBTest_Basic.FixedUnionHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrFixedUnion")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.FixedUnionHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            ORBTest_Basic.FixedUnion arg = (ORBTest_Basic.FixedUnionHelper
                    .extract(any));

            m_ti.attrFixedUnion(arg);

            return;
        }

        if (name.equals("opFixedUnion")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.FixedUnionHelper.type());
            any1.type(ORBTest_Basic.FixedUnionHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            ORBTest_Basic.FixedUnion arg0 = (ORBTest_Basic.FixedUnionHelper
                    .extract(any0));
            ORBTest_Basic.FixedUnionHolder arg1 = (new ORBTest_Basic.FixedUnionHolder());
            arg1.value = ORBTest_Basic.FixedUnionHelper.extract(any1);
            ORBTest_Basic.FixedUnionHolder arg2 = (new ORBTest_Basic.FixedUnionHolder());

            ORBTest_Basic.FixedUnion ret = m_ti.opFixedUnion(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExFixedUnionHelper.insert(exAny,
                        new ORBTest_Basic.ExFixedUnion(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.FixedUnionHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.FixedUnionHelper.insert(any1, arg1.value);
                ORBTest_Basic.FixedUnionHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrVariableUnion")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            ORBTest_Basic.VariableUnion ret = m_ti.attrVariableUnion();

            Any any = m_orb.create_any();
            ORBTest_Basic.VariableUnionHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrVariableUnion")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.VariableUnionHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            ORBTest_Basic.VariableUnion arg = ORBTest_Basic.VariableUnionHelper
                    .extract(any);

            m_ti.attrVariableUnion(arg);

            return;
        }

        if (name.equals("opVariableUnion")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.VariableUnionHelper.type());
            any1.type(ORBTest_Basic.VariableUnionHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            ORBTest_Basic.VariableUnion arg0 = (ORBTest_Basic.VariableUnionHelper
                    .extract(any0));
            ORBTest_Basic.VariableUnionHolder arg1 = (new ORBTest_Basic.VariableUnionHolder());
            arg1.value = ORBTest_Basic.VariableUnionHelper.extract(any1);
            ORBTest_Basic.VariableUnionHolder arg2 = (new ORBTest_Basic.VariableUnionHolder());

            ORBTest_Basic.VariableUnion ret = m_ti.opVariableUnion(arg0, arg1,
                    arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExVariableUnionHelper.insert(exAny,
                        new ORBTest_Basic.ExVariableUnion(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.VariableUnionHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.VariableUnionHelper.insert(any1, arg1.value);
                ORBTest_Basic.VariableUnionHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrStringSequence")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            String[] ret = m_ti.attrStringSequence();

            Any any = m_orb.create_any();
            ORBTest_Basic.StringSequenceHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrStringSequence")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.StringSequenceHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            String[] arg = ORBTest_Basic.StringSequenceHelper.extract(any);

            m_ti.attrStringSequence(arg);

            return;
        }

        if (name.equals("opStringSequence")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.StringSequenceHelper.type());
            any1.type(ORBTest_Basic.StringSequenceHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            String[] arg0 = ORBTest_Basic.StringSequenceHelper.extract(any0);
            ORBTest_Basic.StringSequenceHolder arg1 = (new ORBTest_Basic.StringSequenceHolder());
            arg1.value = ORBTest_Basic.StringSequenceHelper.extract(any1);
            ORBTest_Basic.StringSequenceHolder arg2 = (new ORBTest_Basic.StringSequenceHolder());

            String[] ret = m_ti.opStringSequence(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExStringSequenceHelper.insert(exAny,
                        new ORBTest_Basic.ExStringSequence(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.StringSequenceHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.StringSequenceHelper.insert(any1, arg1.value);
                ORBTest_Basic.StringSequenceHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrFixedArray")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            short[][][] ret = m_ti.attrFixedArray();

            Any any = m_orb.create_any();
            ORBTest_Basic.FixedArrayHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrFixedArray")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.FixedArrayHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            short[][][] arg = ORBTest_Basic.FixedArrayHelper.extract(any);

            m_ti.attrFixedArray(arg);

            return;
        }

        if (name.equals("opFixedArray")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.FixedArrayHelper.type());
            any1.type(ORBTest_Basic.FixedArrayHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            short[][][] arg0 = ORBTest_Basic.FixedArrayHelper.extract(any0);
            ORBTest_Basic.FixedArrayHolder arg1 = (new ORBTest_Basic.FixedArrayHolder());
            arg1.value = ORBTest_Basic.FixedArrayHelper.extract(any1);
            ORBTest_Basic.FixedArrayHolder arg2 = (new ORBTest_Basic.FixedArrayHolder());

            short[][][] ret = m_ti.opFixedArray(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExFixedArrayHelper.insert(exAny,
                        new ORBTest_Basic.ExFixedArray(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.FixedArrayHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.FixedArrayHelper.insert(any1, arg1.value);
                ORBTest_Basic.FixedArrayHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrVariableArray")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            String[][] ret = m_ti.attrVariableArray();

            Any any = m_orb.create_any();
            ORBTest_Basic.VariableArrayHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrVariableArray")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.VariableArrayHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            String[][] arg = ORBTest_Basic.VariableArrayHelper.extract(any);

            m_ti.attrVariableArray(arg);

            return;
        }

        if (name.equals("opVariableArray")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.VariableArrayHelper.type());
            any1.type(ORBTest_Basic.VariableArrayHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            String[][] arg0 = ORBTest_Basic.VariableArrayHelper.extract(any0);
            ORBTest_Basic.VariableArrayHolder arg1 = (new ORBTest_Basic.VariableArrayHolder());
            arg1.value = ORBTest_Basic.VariableArrayHelper.extract(any1);
            ORBTest_Basic.VariableArrayHolder arg2 = (new ORBTest_Basic.VariableArrayHolder());

            String[][] ret = m_ti.opVariableArray(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExVariableArrayHelper.insert(exAny,
                        new ORBTest_Basic.ExVariableArray(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.VariableArrayHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.VariableArrayHelper.insert(any1, arg1.value);
                ORBTest_Basic.VariableArrayHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrFixedArraySequence")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            short[][][][] ret = m_ti.attrFixedArraySequence();

            Any any = m_orb.create_any();
            ORBTest_Basic.FixedArraySequenceHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrFixedArraySequence")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.FixedArraySequenceHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            short[][][][] arg = (ORBTest_Basic.FixedArraySequenceHelper
                    .extract(any));

            m_ti.attrFixedArraySequence(arg);

            return;
        }

        if (name.equals("opFixedArraySequence")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.FixedArraySequenceHelper.type());
            any1.type(ORBTest_Basic.FixedArraySequenceHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            short[][][][] arg0 = (ORBTest_Basic.FixedArraySequenceHelper
                    .extract(any0));
            ORBTest_Basic.FixedArraySequenceHolder arg1 = (new ORBTest_Basic.FixedArraySequenceHolder());
            arg1.value = ORBTest_Basic.FixedArraySequenceHelper.extract(any1);
            ORBTest_Basic.FixedArraySequenceHolder arg2 = (new ORBTest_Basic.FixedArraySequenceHolder());

            short[][][][] ret = m_ti.opFixedArraySequence(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExFixedArraySequenceHelper.insert(exAny,
                        new ORBTest_Basic.ExFixedArraySequence(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.FixedArraySequenceHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.FixedArraySequenceHelper.insert(any1, arg1.value);
                ORBTest_Basic.FixedArraySequenceHelper.insert(any2, arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrVariableArraySequence")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            String[][][] ret = m_ti.attrVariableArraySequence();

            Any any = m_orb.create_any();
            ORBTest_Basic.VariableArraySequenceHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrVariableArraySequence")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.VariableArraySequenceHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            String[][][] arg = (ORBTest_Basic.VariableArraySequenceHelper
                    .extract(any));

            m_ti.attrVariableArraySequence(arg);

            return;
        }

        if (name.equals("opVariableArraySequence")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.VariableArraySequenceHelper.type());
            any1.type(ORBTest_Basic.VariableArraySequenceHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            String[][][] arg0 = (ORBTest_Basic.VariableArraySequenceHelper
                    .extract(any0));
            ORBTest_Basic.VariableArraySequenceHolder arg1 = new ORBTest_Basic.VariableArraySequenceHolder();
            arg1.value = (ORBTest_Basic.VariableArraySequenceHelper
                    .extract(any1));
            ORBTest_Basic.VariableArraySequenceHolder arg2 = (new ORBTest_Basic.VariableArraySequenceHolder());

            String[][][] ret = m_ti.opVariableArraySequence(arg0, arg1, arg2);

            if (ex) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExVariableArraySequenceHelper.insert(exAny,
                        new ORBTest_Basic.ExVariableArraySequence(ret));
                request.set_exception(exAny);
            } else {
                Any result = m_orb.create_any();
                ORBTest_Basic.VariableArraySequenceHelper.insert(result, ret);
                request.set_result(result);
                ORBTest_Basic.VariableArraySequenceHelper.insert(any1,
                        arg1.value);
                ORBTest_Basic.VariableArraySequenceHelper.insert(any2,
                        arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrFixedArrayBoundSequence")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            short[][][][] ret = m_ti.attrFixedArraySequence();

            Any any = m_orb.create_any();
            ORBTest_Basic.FixedArraySequenceHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrFixedArrayBoundSequence")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.FixedArrayBoundSequenceHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            short[][][][] arg = ORBTest_Basic.FixedArrayBoundSequenceHelper
                    .extract(any);

            m_ti.attrFixedArrayBoundSequence(arg);

            return;
        }

        if (name.equals("opFixedArrayBoundSequence")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.FixedArrayBoundSequenceHelper.type());
            any1.type(ORBTest_Basic.FixedArrayBoundSequenceHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            short[][][][] arg0 = ORBTest_Basic.FixedArrayBoundSequenceHelper
                    .extract(any0);
            ORBTest_Basic.FixedArrayBoundSequenceHolder arg1 = new ORBTest_Basic.FixedArrayBoundSequenceHolder();
            arg1._read(any1.create_input_stream());
            ORBTest_Basic.FixedArrayBoundSequenceHolder arg2 = new ORBTest_Basic.FixedArrayBoundSequenceHolder();

            short[][][][] ret = m_ti
                    .opFixedArrayBoundSequence(arg0, arg1, arg2);

            if (ex) {
                Any any = m_orb.create_any();
                ORBTest_Basic.ExFixedArrayBoundSequenceHelper.insert(any,
                        new ORBTest_Basic.ExFixedArrayBoundSequence(ret));
                request.set_exception(any);
            } else {
                Any any = m_orb.create_any();
                ORBTest_Basic.FixedArrayBoundSequenceHelper.insert(any, ret);
                request.set_result(any);
                ORBTest_Basic.FixedArrayBoundSequenceHelper.insert(any1,
                        arg1.value);
                ORBTest_Basic.FixedArrayBoundSequenceHelper.insert(any2,
                        arg2.value);
            }

            return;
        }

        if (name.equals("_get_attrVariableArrayBoundSequence")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            String[][][] ret = m_ti.attrVariableArrayBoundSequence();

            Any any = m_orb.create_any();
            ORBTest_Basic.VariableArrayBoundSequenceHelper.insert(any, ret);
            request.set_result(any);

            return;
        }

        if (name.equals("_set_attrVariableArrayBoundSequence")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(ORBTest_Basic.VariableArrayBoundSequenceHelper.type());
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            String[][][] arg = ORBTest_Basic.VariableArrayBoundSequenceHelper
                    .extract(any);

            m_ti.attrVariableArrayBoundSequence(arg);

            return;
        }

        if (name.equals("opVariableArrayBoundSequence")) {
            NVList list = m_orb.create_list(0);
            Any any0 = m_orb.create_any();
            Any any1 = m_orb.create_any();
            Any any2 = m_orb.create_any();
            any0.type(ORBTest_Basic.VariableArrayBoundSequenceHelper.type());
            any1.type(ORBTest_Basic.VariableArrayBoundSequenceHelper.type());
            list.add_value("", any0, org.omg.CORBA.ARG_IN.value);
            list.add_value("", any1, org.omg.CORBA.ARG_INOUT.value);
            list.add_value("", any2, org.omg.CORBA.ARG_OUT.value);
            request.arguments(list);
            String[][][] arg0 = ORBTest_Basic.VariableArrayBoundSequenceHelper
                    .extract(any0);
            ORBTest_Basic.VariableArrayBoundSequenceHolder arg1 = new ORBTest_Basic.VariableArrayBoundSequenceHolder();
            arg1._read(any1.create_input_stream());
            ORBTest_Basic.VariableArrayBoundSequenceHolder arg2 = new ORBTest_Basic.VariableArrayBoundSequenceHolder();

            String[][][] ret = m_ti.opVariableArrayBoundSequence(arg0, arg1,
                    arg2);

            if (ex) {
                Any any = m_orb.create_any();
                ORBTest_Basic.ExVariableArrayBoundSequenceHelper.insert(any,
                        new ORBTest_Basic.ExVariableArrayBoundSequence(ret));
                request.set_exception(any);
            } else {
                Any any = m_orb.create_any();
                ORBTest_Basic.VariableArrayBoundSequenceHelper.insert(any, ret);
                request.set_result(any);
                ORBTest_Basic.VariableArrayBoundSequenceHelper.insert(any1,
                        arg1.value);
                ORBTest_Basic.VariableArrayBoundSequenceHelper.insert(any2,
                        arg2.value);
            }

            return;
        }

        if (name.equals("opExRecursiveStruct")) {
            NVList list = m_orb.create_list(0);
            request.arguments(list);

            try {
                m_ti.opExRecursiveStruct();
            } catch (ORBTest_Basic.ExRecursiveStruct ex_recursive_struct) {
                Any exAny = m_orb.create_any();
                ORBTest_Basic.ExRecursiveStructHelper
                        .insert(exAny, new ORBTest_Basic.ExRecursiveStruct(
                                ex_recursive_struct.us, ex_recursive_struct.rs));
                request.set_exception(exAny);
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

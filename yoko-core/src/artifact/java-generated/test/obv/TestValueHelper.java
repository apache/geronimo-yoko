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
package test.obv;

//
// IDL:TestValue:1.0
//
final public class TestValueHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TestValue val)
    {
        any.insert_Value(val, type());
    }

    public static TestValue
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            java.io.Serializable _ob_v = any.extract_Value();
            if(_ob_v == null || _ob_v instanceof TestValue)
                return (TestValue)_ob_v;
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            org.omg.CORBA.ValueMember[] members = new org.omg.CORBA.ValueMember[1];

            members[0] = new org.omg.CORBA.ValueMember();
            members[0].name = "count";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
            members[0].access = org.omg.CORBA.PUBLIC_MEMBER.value;

            typeCode_ = orb.create_value_tc(id(), "TestValue", org.omg.CORBA.VM_NONE.value, null, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestValue:1.0";
    }

    public static TestValue
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        return (TestValue)((org.omg.CORBA_2_3.portable.InputStream)in).read_value(id());
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestValue val)
    {
        if(!(out instanceof org.omg.CORBA_2_3.portable.OutputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_value(val, id());
    }

    public static TestValue
    create(org.omg.CORBA.ORB orb,
           int l)
    {
        TestValueValueFactory _ob_f = _OB_getFactory(orb);
        return _ob_f.create(l);
    }

    private static TestValueValueFactory
    _OB_getFactory(org.omg.CORBA.ORB orb)
    {
        org.omg.CORBA.portable.ValueFactory _ob_f = ((org.omg.CORBA_2_3.ORB)orb).lookup_value_factory(id());
        if(_ob_f == null)
            throw new org.omg.CORBA.MARSHAL(1, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        return (TestValueValueFactory)_ob_f;
    }
}

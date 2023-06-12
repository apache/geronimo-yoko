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
// IDL:TestULongBox:1.0
//
final public class TestULongBoxHelper implements org.omg.CORBA.portable.BoxedValueHelper
{
    private static final TestULongBoxHelper _instance = new TestULongBoxHelper();

    public static void
    insert(org.omg.CORBA.Any any, TestULongBox val)
    {
        any.insert_Value((java.io.Serializable)val, type());
    }

    public static TestULongBox
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            java.io.Serializable _ob_v = any.extract_Value();
            if(_ob_v == null || _ob_v instanceof TestULongBox)
                return (TestULongBox)_ob_v;
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
            typeCode_ = orb.create_value_box_tc(id(), "TestULongBox", orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong));
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestULongBox:1.0";
    }

    public static TestULongBox
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        return (TestULongBox)((org.omg.CORBA_2_3.portable.InputStream)in).read_value(_instance);
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestULongBox val)
    {
        if(!(out instanceof org.omg.CORBA_2_3.portable.OutputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_value((java.io.Serializable)val, _instance);
    }

    public java.io.Serializable
    read_value(org.omg.CORBA.portable.InputStream in)
    {
        int _ob_v;
        _ob_v = in.read_ulong();
        return new TestULongBox(_ob_v);
    }

    public void
    write_value(org.omg.CORBA.portable.OutputStream out, java.io.Serializable val)
    {
        if(!(val instanceof TestULongBox))
            throw new org.omg.CORBA.MARSHAL();
        int _ob_value = ((TestULongBox)val).value;
        out.write_ulong(_ob_value);
    }

    public String
    get_id()
    {
        return id();
    }
}

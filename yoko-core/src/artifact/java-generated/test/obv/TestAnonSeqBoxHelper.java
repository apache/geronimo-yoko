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
// IDL:TestAnonSeqBox:1.0
//
final public class TestAnonSeqBoxHelper implements org.omg.CORBA.portable.BoxedValueHelper
{
    private static final TestAnonSeqBoxHelper _instance = new TestAnonSeqBoxHelper();

    public static void
    insert(org.omg.CORBA.Any any, short[] val)
    {
        any.insert_Value((java.io.Serializable)val, type());
    }

    public static short[]
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            java.io.Serializable _ob_v = any.extract_Value();
            if(_ob_v == null || _ob_v instanceof short[])
                return (short[])_ob_v;
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
            typeCode_ = orb.create_value_box_tc(id(), "TestAnonSeqBox", orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short)));
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestAnonSeqBox:1.0";
    }

    public static short[]
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        return (short[])((org.omg.CORBA_2_3.portable.InputStream)in).read_value(_instance);
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, short[] val)
    {
        if(!(out instanceof org.omg.CORBA_2_3.portable.OutputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_value((java.io.Serializable)val, _instance);
    }

    public java.io.Serializable
    read_value(org.omg.CORBA.portable.InputStream in)
    {
        short[] _ob_v;
        int len0 = in.read_ulong();
        _ob_v = new short[len0];
        in.read_short_array(_ob_v, 0, len0);
        return (java.io.Serializable)_ob_v;
    }

    public void
    write_value(org.omg.CORBA.portable.OutputStream out, java.io.Serializable val)
    {
        if(!(val instanceof short[]))
            throw new org.omg.CORBA.MARSHAL();
        short[] _ob_value = (short[])val;
        int len0 = _ob_value.length;
        out.write_ulong(len0);
        out.write_short_array(_ob_value, 0, len0);
    }

    public String
    get_id()
    {
        return id();
    }
}

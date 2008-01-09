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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/StringValue:1.0
//
public class StringValueHelper implements org.omg.CORBA.portable.BoxedValueHelper
{
    private static final StringValueHelper _instance = new StringValueHelper();

    public static void
    insert(org.omg.CORBA.Any any, String val)
    {
        any.insert_Value((java.io.Serializable)val, type());
    }

    public static String
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            java.io.Serializable _ob_v = any.extract_Value();
            if(_ob_v == null || _ob_v instanceof String)
                return (String)_ob_v;
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
            typeCode_ = orb.create_value_box_tc(id(), "StringValue", orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string));
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/StringValue:1.0";
    }

    public static String
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        return (String)((org.omg.CORBA_2_3.portable.InputStream)in).read_value(_instance);
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, String val)
    {
        if(!(out instanceof org.omg.CORBA_2_3.portable.OutputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_value((java.io.Serializable)val, _instance);
    }

    public java.io.Serializable
    read_value(org.omg.CORBA.portable.InputStream in)
    {
        String _ob_v;
        _ob_v = in.read_string();
        return (java.io.Serializable)_ob_v;
    }

    public void
    write_value(org.omg.CORBA.portable.OutputStream out, java.io.Serializable val)
    {
        if(!(val instanceof String))
            throw new org.omg.CORBA.MARSHAL();
        String _ob_value = (String)val;
        out.write_string(_ob_value);
    }

    public String
    get_id()
    {
        return id();
    }
}

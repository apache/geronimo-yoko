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
// IDL:omg.org/CORBA/DefinitionKind:1.0
//
public abstract class DefinitionKindHelper
{
    public static void
    insert(org.omg.CORBA.Any any, DefinitionKind val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static DefinitionKind
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            String[] members = new String[26];
            members[0] = "dk_none";
            members[1] = "dk_all";
            members[2] = "dk_Attribute";
            members[3] = "dk_Constant";
            members[4] = "dk_Exception";
            members[5] = "dk_Interface";
            members[6] = "dk_Module";
            members[7] = "dk_Operation";
            members[8] = "dk_Typedef";
            members[9] = "dk_Alias";
            members[10] = "dk_Struct";
            members[11] = "dk_Union";
            members[12] = "dk_Enum";
            members[13] = "dk_Primitive";
            members[14] = "dk_String";
            members[15] = "dk_Sequence";
            members[16] = "dk_Array";
            members[17] = "dk_Repository";
            members[18] = "dk_Wstring";
            members[19] = "dk_Fixed";
            members[20] = "dk_Value";
            members[21] = "dk_ValueBox";
            members[22] = "dk_ValueMember";
            members[23] = "dk_Native";
            members[24] = "dk_AbstractInterface";
            members[25] = "dk_LocalInterface";
            typeCode_ = orb.create_enum_tc(id(), "DefinitionKind", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/DefinitionKind:1.0";
    }

    public static DefinitionKind
    read(org.omg.CORBA.portable.InputStream in)
    {
        DefinitionKind _ob_v;
        _ob_v = DefinitionKind.from_int(in.read_ulong());
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, DefinitionKind val)
    {
        out.write_ulong(val.value());
    }
}

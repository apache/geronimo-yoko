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
// IDL:omg.org/CORBA/ValueDescription:1.0
//
final public class ValueDescriptionHelper
{
    public static void
    insert(org.omg.CORBA.Any any, ValueDescription val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static ValueDescription
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[10];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "name";
            members[0].type = IdentifierHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "id";
            members[1].type = RepositoryIdHelper.type();

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "is_abstract";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "is_custom";
            members[3].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "defined_in";
            members[4].type = RepositoryIdHelper.type();

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "version";
            members[5].type = VersionSpecHelper.type();

            members[6] = new org.omg.CORBA.StructMember();
            members[6].name = "supported_interfaces";
            members[6].type = RepositoryIdSeqHelper.type();

            members[7] = new org.omg.CORBA.StructMember();
            members[7].name = "abstract_base_values";
            members[7].type = RepositoryIdSeqHelper.type();

            members[8] = new org.omg.CORBA.StructMember();
            members[8].name = "is_truncatable";
            members[8].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[9] = new org.omg.CORBA.StructMember();
            members[9].name = "base_value";
            members[9].type = RepositoryIdHelper.type();

            typeCode_ = orb.create_struct_tc(id(), "ValueDescription", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/ValueDescription:1.0";
    }

    public static ValueDescription
    read(org.omg.CORBA.portable.InputStream in)
    {
        ValueDescription _ob_v = new ValueDescription();
        _ob_v.name = IdentifierHelper.read(in);
        _ob_v.id = RepositoryIdHelper.read(in);
        _ob_v.is_abstract = in.read_boolean();
        _ob_v.is_custom = in.read_boolean();
        _ob_v.defined_in = RepositoryIdHelper.read(in);
        _ob_v.version = VersionSpecHelper.read(in);
        _ob_v.supported_interfaces = RepositoryIdSeqHelper.read(in);
        _ob_v.abstract_base_values = RepositoryIdSeqHelper.read(in);
        _ob_v.is_truncatable = in.read_boolean();
        _ob_v.base_value = RepositoryIdHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, ValueDescription val)
    {
        IdentifierHelper.write(out, val.name);
        RepositoryIdHelper.write(out, val.id);
        out.write_boolean(val.is_abstract);
        out.write_boolean(val.is_custom);
        RepositoryIdHelper.write(out, val.defined_in);
        VersionSpecHelper.write(out, val.version);
        RepositoryIdSeqHelper.write(out, val.supported_interfaces);
        RepositoryIdSeqHelper.write(out, val.abstract_base_values);
        out.write_boolean(val.is_truncatable);
        RepositoryIdHelper.write(out, val.base_value);
    }
}

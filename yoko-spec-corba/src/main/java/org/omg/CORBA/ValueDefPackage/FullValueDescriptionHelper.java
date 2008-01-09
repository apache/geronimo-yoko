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

package org.omg.CORBA.ValueDefPackage;

//
// IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0
//
final public class FullValueDescriptionHelper
{
    public static void
    insert(org.omg.CORBA.Any any, FullValueDescription val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static FullValueDescription
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[15];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "name";
            members[0].type = org.omg.CORBA.IdentifierHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "id";
            members[1].type = org.omg.CORBA.RepositoryIdHelper.type();

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "is_abstract";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "is_custom";
            members[3].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "defined_in";
            members[4].type = org.omg.CORBA.RepositoryIdHelper.type();

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "version";
            members[5].type = org.omg.CORBA.VersionSpecHelper.type();

            members[6] = new org.omg.CORBA.StructMember();
            members[6].name = "operations";
            members[6].type = org.omg.CORBA.OpDescriptionSeqHelper.type();

            members[7] = new org.omg.CORBA.StructMember();
            members[7].name = "attributes";
            members[7].type = org.omg.CORBA.AttrDescriptionSeqHelper.type();

            members[8] = new org.omg.CORBA.StructMember();
            members[8].name = "members";
            members[8].type = org.omg.CORBA.ValueMemberSeqHelper.type();

            members[9] = new org.omg.CORBA.StructMember();
            members[9].name = "initializers";
            members[9].type = org.omg.CORBA.InitializerSeqHelper.type();

            members[10] = new org.omg.CORBA.StructMember();
            members[10].name = "supported_interfaces";
            members[10].type = org.omg.CORBA.RepositoryIdSeqHelper.type();

            members[11] = new org.omg.CORBA.StructMember();
            members[11].name = "abstract_base_values";
            members[11].type = org.omg.CORBA.RepositoryIdSeqHelper.type();

            members[12] = new org.omg.CORBA.StructMember();
            members[12].name = "is_truncatable";
            members[12].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[13] = new org.omg.CORBA.StructMember();
            members[13].name = "base_value";
            members[13].type = org.omg.CORBA.RepositoryIdHelper.type();

            members[14] = new org.omg.CORBA.StructMember();
            members[14].name = "type";
            members[14].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_TypeCode);

            typeCode_ = orb.create_struct_tc(id(), "FullValueDescription", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0";
    }

    public static FullValueDescription
    read(org.omg.CORBA.portable.InputStream in)
    {
        FullValueDescription _ob_v = new FullValueDescription();
        _ob_v.name = org.omg.CORBA.IdentifierHelper.read(in);
        _ob_v.id = org.omg.CORBA.RepositoryIdHelper.read(in);
        _ob_v.is_abstract = in.read_boolean();
        _ob_v.is_custom = in.read_boolean();
        _ob_v.defined_in = org.omg.CORBA.RepositoryIdHelper.read(in);
        _ob_v.version = org.omg.CORBA.VersionSpecHelper.read(in);
        _ob_v.operations = org.omg.CORBA.OpDescriptionSeqHelper.read(in);
        _ob_v.attributes = org.omg.CORBA.AttrDescriptionSeqHelper.read(in);
        _ob_v.members = org.omg.CORBA.ValueMemberSeqHelper.read(in);
        _ob_v.initializers = org.omg.CORBA.InitializerSeqHelper.read(in);
        _ob_v.supported_interfaces = org.omg.CORBA.RepositoryIdSeqHelper.read(in);
        _ob_v.abstract_base_values = org.omg.CORBA.RepositoryIdSeqHelper.read(in);
        _ob_v.is_truncatable = in.read_boolean();
        _ob_v.base_value = org.omg.CORBA.RepositoryIdHelper.read(in);
        _ob_v.type = in.read_TypeCode();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, FullValueDescription val)
    {
        org.omg.CORBA.IdentifierHelper.write(out, val.name);
        org.omg.CORBA.RepositoryIdHelper.write(out, val.id);
        out.write_boolean(val.is_abstract);
        out.write_boolean(val.is_custom);
        org.omg.CORBA.RepositoryIdHelper.write(out, val.defined_in);
        org.omg.CORBA.VersionSpecHelper.write(out, val.version);
        org.omg.CORBA.OpDescriptionSeqHelper.write(out, val.operations);
        org.omg.CORBA.AttrDescriptionSeqHelper.write(out, val.attributes);
        org.omg.CORBA.ValueMemberSeqHelper.write(out, val.members);
        org.omg.CORBA.InitializerSeqHelper.write(out, val.initializers);
        org.omg.CORBA.RepositoryIdSeqHelper.write(out, val.supported_interfaces);
        org.omg.CORBA.RepositoryIdSeqHelper.write(out, val.abstract_base_values);
        out.write_boolean(val.is_truncatable);
        org.omg.CORBA.RepositoryIdHelper.write(out, val.base_value);
        out.write_TypeCode(val.type);
    }
}

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

package org.omg.CORBA.InterfaceDefPackage;

//
// IDL:omg.org/CORBA/InterfaceDef/FullInterfaceDescription:1.0
//
final public class FullInterfaceDescriptionHelper
{
    public static void
    insert(org.omg.CORBA.Any any, FullInterfaceDescription val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static FullInterfaceDescription
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[8];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "name";
            members[0].type = org.omg.CORBA.IdentifierHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "id";
            members[1].type = org.omg.CORBA.RepositoryIdHelper.type();

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "defined_in";
            members[2].type = org.omg.CORBA.RepositoryIdHelper.type();

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "version";
            members[3].type = org.omg.CORBA.VersionSpecHelper.type();

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "operations";
            members[4].type = org.omg.CORBA.OpDescriptionSeqHelper.type();

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "attributes";
            members[5].type = org.omg.CORBA.AttrDescriptionSeqHelper.type();

            members[6] = new org.omg.CORBA.StructMember();
            members[6].name = "base_interfaces";
            members[6].type = org.omg.CORBA.RepositoryIdSeqHelper.type();

            members[7] = new org.omg.CORBA.StructMember();
            members[7].name = "type";
            members[7].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_TypeCode);

            typeCode_ = orb.create_struct_tc(id(), "FullInterfaceDescription", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/InterfaceDef/FullInterfaceDescription:1.0";
    }

    public static FullInterfaceDescription
    read(org.omg.CORBA.portable.InputStream in)
    {
        FullInterfaceDescription _ob_v = new FullInterfaceDescription();
        _ob_v.name = org.omg.CORBA.IdentifierHelper.read(in);
        _ob_v.id = org.omg.CORBA.RepositoryIdHelper.read(in);
        _ob_v.defined_in = org.omg.CORBA.RepositoryIdHelper.read(in);
        _ob_v.version = org.omg.CORBA.VersionSpecHelper.read(in);
        _ob_v.operations = org.omg.CORBA.OpDescriptionSeqHelper.read(in);
        _ob_v.attributes = org.omg.CORBA.AttrDescriptionSeqHelper.read(in);
        _ob_v.base_interfaces = org.omg.CORBA.RepositoryIdSeqHelper.read(in);
        _ob_v.type = in.read_TypeCode();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, FullInterfaceDescription val)
    {
        org.omg.CORBA.IdentifierHelper.write(out, val.name);
        org.omg.CORBA.RepositoryIdHelper.write(out, val.id);
        org.omg.CORBA.RepositoryIdHelper.write(out, val.defined_in);
        org.omg.CORBA.VersionSpecHelper.write(out, val.version);
        org.omg.CORBA.OpDescriptionSeqHelper.write(out, val.operations);
        org.omg.CORBA.AttrDescriptionSeqHelper.write(out, val.attributes);
        org.omg.CORBA.RepositoryIdSeqHelper.write(out, val.base_interfaces);
        out.write_TypeCode(val.type);
    }
}

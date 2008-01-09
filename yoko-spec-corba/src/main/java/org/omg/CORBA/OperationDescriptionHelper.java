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
// IDL:omg.org/CORBA/OperationDescription:1.0
//
final public class OperationDescriptionHelper
{
    public static void
    insert(org.omg.CORBA.Any any, OperationDescription val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static OperationDescription
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
            members[2].name = "defined_in";
            members[2].type = RepositoryIdHelper.type();

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "version";
            members[3].type = VersionSpecHelper.type();

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "result";
            members[4].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_TypeCode);

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "mode";
            members[5].type = OperationModeHelper.type();

            members[6] = new org.omg.CORBA.StructMember();
            members[6].name = "contexts";
            members[6].type = ContextIdSeqHelper.type();

            members[7] = new org.omg.CORBA.StructMember();
            members[7].name = "parameters";
            members[7].type = ParDescriptionSeqHelper.type();

            members[8] = new org.omg.CORBA.StructMember();
            members[8].name = "exceptions";
            members[8].type = ExcDescriptionSeqHelper.type();

            members[9] = new org.omg.CORBA.StructMember();
            members[9].name = "natives";
            members[9].type = NativeDescriptionSeqHelper.type();

            typeCode_ = orb.create_struct_tc(id(), "OperationDescription", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/OperationDescription:1.0";
    }

    public static OperationDescription
    read(org.omg.CORBA.portable.InputStream in)
    {
        OperationDescription _ob_v = new OperationDescription();
        _ob_v.name = IdentifierHelper.read(in);
        _ob_v.id = RepositoryIdHelper.read(in);
        _ob_v.defined_in = RepositoryIdHelper.read(in);
        _ob_v.version = VersionSpecHelper.read(in);
        _ob_v.result = in.read_TypeCode();
        _ob_v.mode = OperationModeHelper.read(in);
        _ob_v.contexts = ContextIdSeqHelper.read(in);
        _ob_v.parameters = ParDescriptionSeqHelper.read(in);
        _ob_v.exceptions = ExcDescriptionSeqHelper.read(in);
        _ob_v.natives = NativeDescriptionSeqHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, OperationDescription val)
    {
        IdentifierHelper.write(out, val.name);
        RepositoryIdHelper.write(out, val.id);
        RepositoryIdHelper.write(out, val.defined_in);
        VersionSpecHelper.write(out, val.version);
        out.write_TypeCode(val.result);
        OperationModeHelper.write(out, val.mode);
        ContextIdSeqHelper.write(out, val.contexts);
        ParDescriptionSeqHelper.write(out, val.parameters);
        ExcDescriptionSeqHelper.write(out, val.exceptions);
        NativeDescriptionSeqHelper.write(out, val.natives);
    }
}

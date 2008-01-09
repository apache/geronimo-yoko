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

package org.omg.GIOP;

//
// IDL:omg.org/GIOP/TargetAddress:1.0
//
final public class TargetAddressHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TargetAddress val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TargetAddress
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
            org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[3];

            members[0] = new org.omg.CORBA.UnionMember();
            members[0].name = "object_key";
            members[0].type = orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet));
            members[0].label = orb.create_any();
            members[0].label.insert_short((short)(0L));

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "profile";
            members[1].type = org.omg.IOP.TaggedProfileHelper.type();
            members[1].label = orb.create_any();
            members[1].label.insert_short((short)(1L));

            members[2] = new org.omg.CORBA.UnionMember();
            members[2].name = "ior";
            members[2].type = IORAddressingInfoHelper.type();
            members[2].label = orb.create_any();
            members[2].label.insert_short((short)(2L));

            org.omg.CORBA.TypeCode discType = AddressingDispositionHelper.type();
            typeCode_ = orb.create_union_tc(id(), "TargetAddress", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/GIOP/TargetAddress:1.0";
    }

    public static TargetAddress
    read(org.omg.CORBA.portable.InputStream in)
    {
        TargetAddress _ob_v = new TargetAddress();
        short _ob_d;
        _ob_d = AddressingDispositionHelper.read(in);

        switch(_ob_d)
        {
        case (short)0:
        {
            byte[] _ob_m;
            int len0 = in.read_ulong();
            _ob_m = new byte[len0];
            in.read_octet_array(_ob_m, 0, len0);
            _ob_v.object_key(_ob_m);
            break;
        }

        case (short)1:
        {
            org.omg.IOP.TaggedProfile _ob_m;
            _ob_m = org.omg.IOP.TaggedProfileHelper.read(in);
            _ob_v.profile(_ob_m);
            break;
        }

        case (short)2:
        {
            IORAddressingInfo _ob_m;
            _ob_m = IORAddressingInfoHelper.read(in);
            _ob_v.ior(_ob_m);
            break;
        }

        default:
            _ob_v.__default(_ob_d);
            break;
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TargetAddress val)
    {
        short _ob_d = val.discriminator();
        AddressingDispositionHelper.write(out, _ob_d);

        switch(_ob_d)
        {
        case (short)0:
        {
            byte[] _ob_m = val.object_key();
            int len0 = _ob_m.length;
            out.write_ulong(len0);
            out.write_octet_array(_ob_m, 0, len0);
            break;
        }

        case (short)1:
        {
            org.omg.IOP.TaggedProfile _ob_m = val.profile();
            org.omg.IOP.TaggedProfileHelper.write(out, _ob_m);
            break;
        }

        case (short)2:
        {
            IORAddressingInfo _ob_m = val.ior();
            IORAddressingInfoHelper.write(out, _ob_m);
            break;
        }

        default:
            break;
        }
    }
}

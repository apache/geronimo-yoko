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

package org.apache.yoko.orb.OB.CorbalocProtocolPackage;

//
// IDL:orb.yoko.apache.org/OB/CorbalocProtocol/ObjectKey:1.0
//
final public class ObjectKeyHelper
{
    public static void
    insert(org.omg.CORBA.Any any, byte[] val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static byte[]
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else

            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
                org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            typeCode_ = orb.create_alias_tc(id(), "ObjectKey", orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet)));
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:orb.yoko.apache.org/OB/CorbalocProtocol/ObjectKey:1.0";
    }

    public static byte[]
    read(org.omg.CORBA.portable.InputStream in)
    {
        byte[] _ob_v;
        int len0 = in.read_ulong();
        _ob_v = new byte[len0];
        in.read_octet_array(_ob_v, 0, len0);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, byte[] val)
    {
        int len0 = val.length;
        out.write_ulong(len0);
        out.write_octet_array(val, 0, len0);
    }
}

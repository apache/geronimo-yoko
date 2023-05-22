/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.orb.IMR;

import org.apache.yoko.util.MinorCodes;

//
// IDL:orb.yoko.apache.org/IMR/ServerFactory:1.0
//
final public class ServerFactoryHelper
{
    public static void
    insert(org.omg.CORBA.Any any, ServerFactory val)
    {
        any.insert_Object(val, type());
    }

    public static ServerFactory
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return narrow(any.extract_Object());

        throw new org.omg.CORBA.BAD_OPERATION(
            MinorCodes
                    .describeBadOperation(MinorCodes.MinorTypeMismatch),
            MinorCodes.MinorTypeMismatch, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            typeCode_ = orb.create_interface_tc(id(), "ServerFactory");
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:orb.yoko.apache.org/IMR/ServerFactory:1.0";
    }

    public static ServerFactory
    read(org.omg.CORBA.portable.InputStream in)
    {
        org.omg.CORBA.Object _ob_v = in.read_Object();

        try
        {
            return (ServerFactory)_ob_v;
        }
        catch(ClassCastException ex)
        {
        }

        org.omg.CORBA.portable.ObjectImpl _ob_impl;
        _ob_impl = (org.omg.CORBA.portable.ObjectImpl)_ob_v;
        _ServerFactoryStub _ob_stub = new _ServerFactoryStub();
        _ob_stub._set_delegate(_ob_impl._get_delegate());
        return _ob_stub;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, ServerFactory val)
    {
        out.write_Object(val);
    }

    public static ServerFactory
    narrow(org.omg.CORBA.Object val)
    {
        if(val != null)
        {
            try
            {
                return (ServerFactory)val;
            }
            catch(ClassCastException ex)
            {
            }

            if(val._is_a(id()))
            {
                org.omg.CORBA.portable.ObjectImpl _ob_impl;
                _ServerFactoryStub _ob_stub = new _ServerFactoryStub();
                _ob_impl = (org.omg.CORBA.portable.ObjectImpl)val;
                _ob_stub._set_delegate(_ob_impl._get_delegate());
                return _ob_stub;
            }

            throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                .describeBadParam(MinorCodes.MinorIncompatibleObjectType),
                MinorCodes.MinorIncompatibleObjectType,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        return null;
    }

    public static ServerFactory
    unchecked_narrow(org.omg.CORBA.Object val)
    {
        if(val != null)
        {
            try
            {
                return (ServerFactory)val;
            }
            catch(ClassCastException ex)
            {
            }

            org.omg.CORBA.portable.ObjectImpl _ob_impl;
            _ServerFactoryStub _ob_stub = new _ServerFactoryStub();
            _ob_impl = (org.omg.CORBA.portable.ObjectImpl)val;
            _ob_stub._set_delegate(_ob_impl._get_delegate());
            return _ob_stub;
        }

        return null;
    }
}

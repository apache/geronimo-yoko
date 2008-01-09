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

package org.apache.yoko.orb.OBPortableServer;

import org.apache.yoko.orb.IMR.ServerDomain;
import org.apache.yoko.orb.IMR._ServerDomainStub;

//
// IDL:orb.yoko.apache.org/IMR/ServerDomain:1.0
//
final public class ServerDomainHelper {
    public static void insert(org.omg.CORBA.Any any, ServerDomain val) {
        any.insert_Object(val, type());
    }

    public static ServerDomain extract(org.omg.CORBA.Any any) {
        if (any.type().equivalent(type()))
            return narrow(any.extract_Object());


        throw new org.omg.CORBA.BAD_OPERATION(
            org.apache.yoko.orb.OB.MinorCodes
                    .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
            org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode type() {
        if (typeCode_ == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            typeCode_ = orb.create_interface_tc(id(), "ServerDomain");
        }

        return typeCode_;
    }

    public static String id() {
        return "IDL:orb.yoko.apache.org/IMR/ServerDomain:1.0";
    }

    public static ServerDomain read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA.Object _ob_v = in.read_Object();

        try {
            return (ServerDomain) _ob_v;
        } catch (ClassCastException ex) {
        }

        org.omg.CORBA.portable.ObjectImpl _ob_impl;
        _ob_impl = (org.omg.CORBA.portable.ObjectImpl) _ob_v;
        _ServerDomainStub _ob_stub = new _ServerDomainStub();
        _ob_stub._set_delegate(_ob_impl._get_delegate());
        return _ob_stub;
    }

    public static void write(org.omg.CORBA.portable.OutputStream out,
            ServerDomain val) {
        out.write_Object(val);
    }

    public static ServerDomain narrow(org.omg.CORBA.Object val) {
        if (val != null) {
            try {
                return (ServerDomain) val;
            } catch (ClassCastException ex) {
            }

            if (val._is_a(id())) {
                org.omg.CORBA.portable.ObjectImpl _ob_impl;
                _ServerDomainStub _ob_stub = new _ServerDomainStub();
                _ob_impl = (org.omg.CORBA.portable.ObjectImpl) val;
                _ob_stub._set_delegate(_ob_impl._get_delegate());
                return _ob_stub;
            }

            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType), 
                org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType, 
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        return null;
    }

    public static ServerDomain unchecked_narrow(org.omg.CORBA.Object val) {
        if (val != null) {
            try {
                return (ServerDomain) val;
            } catch (ClassCastException ex) {
            }

            org.omg.CORBA.portable.ObjectImpl _ob_impl;
            _ServerDomainStub _ob_stub = new _ServerDomainStub();
            _ob_impl = (org.omg.CORBA.portable.ObjectImpl) val;
            _ob_stub._set_delegate(_ob_impl._get_delegate());
            return _ob_stub;
        }

        return null;
    }
}

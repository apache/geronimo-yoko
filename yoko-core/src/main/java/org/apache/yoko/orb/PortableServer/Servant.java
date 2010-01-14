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

package org.apache.yoko.orb.PortableServer;

//
// Servant is the base class for proprietary skeletons with full
// interceptor support
//
abstract public class Servant extends org.omg.PortableServer.Servant {
    protected org.omg.CORBA.portable.InputStream _OB_preUnmarshal(
            org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        Delegate d = (Delegate) _get_delegate();
        return d._OB_preUnmarshal(this, up);
    }

    protected void _OB_unmarshalEx(org.apache.yoko.orb.OB.Upcall up,
            org.omg.CORBA.SystemException ex)
            throws org.apache.yoko.orb.OB.LocationForward {
        Delegate d = (Delegate) _get_delegate();
        d._OB_unmarshalEx(this, up, ex);
    }

    protected void _OB_postUnmarshal(org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        Delegate d = (Delegate) _get_delegate();
        d._OB_postUnmarshal(this, up);
    }

    protected void _OB_postinvoke(org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        Delegate d = (Delegate) _get_delegate();
        d._OB_postinvoke(this, up);
    }

    protected org.omg.CORBA.portable.OutputStream _OB_preMarshal(
            org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        Delegate d = (Delegate) _get_delegate();
        return d._OB_preMarshal(this, up);
    }

    protected void _OB_marshalEx(org.apache.yoko.orb.OB.Upcall up,
            org.omg.CORBA.SystemException ex)
            throws org.apache.yoko.orb.OB.LocationForward {
        Delegate d = (Delegate) _get_delegate();
        d._OB_unmarshalEx(this, up, ex);
    }

    protected void _OB_postMarshal(org.apache.yoko.orb.OB.Upcall up)
            throws org.apache.yoko.orb.OB.LocationForward {
        Delegate d = (Delegate) _get_delegate();
        d._OB_postMarshal(this, up);
    }

    protected void _OB_setArgDesc(org.apache.yoko.orb.OB.Upcall up,
            org.apache.yoko.orb.OB.ParameterDesc[] argDesc,
            org.apache.yoko.orb.OB.ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) {
        Delegate d = (Delegate) _get_delegate();
        d._OB_setArgDesc(this, up, argDesc, retDesc, exceptionTC);
    }

    protected org.omg.CORBA.portable.OutputStream _OB_beginUserException(
            org.apache.yoko.orb.OB.Upcall up, org.omg.CORBA.UserException ex) {
        Delegate d = (Delegate) _get_delegate();
        return d._OB_beginUserException(this, up, ex);
    }

    abstract public void _OB_dispatch(org.apache.yoko.orb.OB.Upcall _ob_up)
            throws org.apache.yoko.orb.OB.LocationForward;
}

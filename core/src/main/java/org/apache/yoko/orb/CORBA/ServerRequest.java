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

package org.apache.yoko.orb.CORBA;

public class ServerRequest extends org.omg.CORBA.ServerRequest {
    private org.omg.PortableServer.DynamicImplementation servant_;

    private org.apache.yoko.orb.PortableServer.Delegate delegate_;

    private org.apache.yoko.orb.OB.Upcall up_;

    private org.omg.CORBA.portable.InputStream in_;

    private org.omg.CORBA.NVList arguments_;

    private org.omg.CORBA.Context ctx_;

    private org.omg.CORBA.Any result_;

    private org.omg.CORBA.Any exception_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String operation() {
        return up_.operation();
    }

    public void arguments(org.omg.CORBA.NVList parameters) {
        if (arguments_ != null)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUseOfDSIArguments),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUseOfDSIArguments,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        try {
            arguments_ = parameters;
            in_ = delegate_._OB_preUnmarshal(servant_, up_);
            try {
                for (int i = 0; i < parameters.count(); i++) {
                    org.omg.CORBA.NamedValue nv = parameters.item(i);

                    if (nv.flags() != org.omg.CORBA.ARG_OUT.value)
                        nv.value().read_value(in_, nv.value().type());
                }
            } catch (org.omg.CORBA.Bounds ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (org.omg.CORBA.SystemException ex) {
                delegate_._OB_unmarshalEx(servant_, up_, ex);
            }
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
            //
            // Translate into a RuntimeException to bypass standardized
            // interfaces
            //
            throw new org.apache.yoko.orb.OB.RuntimeLocationForward(ex.ior,
                    ex.perm);
        }

        if (up_ instanceof org.apache.yoko.orb.OB.PIUpcall) {
            org.apache.yoko.orb.OB.PIUpcall piup = (org.apache.yoko.orb.OB.PIUpcall) up_;
            piup.setArguments(parameters);
        }
    }

    public org.omg.CORBA.Context ctx() {
        if (arguments_ == null || ctx_ != null || result_ != null
                || exception_ != null)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUseOfDSIContext),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUseOfDSIContext,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        try {
            try {
                int len = in_.read_ulong();
                String[] strings = new String[len];
                for (int i = 0; i < len; i++)
                    strings[i] = in_.read_string();
                ctx_ = new Context(up_.orbInstance().getORB(), "", strings);
            } catch (org.omg.CORBA.SystemException ex) {
                delegate_._OB_unmarshalEx(servant_, up_, ex);
            }
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
            //
            // Translate into a RuntimeException to bypass standardized
            // interfaces
            //
            throw new org.apache.yoko.orb.OB.RuntimeLocationForward(ex.ior,
                    ex.perm);
        }

        return ctx_;
    }

    public void set_result(org.omg.CORBA.Any value) {
        if (arguments_ == null || result_ != null || exception_ != null)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUseOfDSIResult),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUseOfDSIResult,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        result_ = value;

        if (up_ instanceof org.apache.yoko.orb.OB.PIUpcall) {
            org.apache.yoko.orb.OB.PIUpcall piup = (org.apache.yoko.orb.OB.PIUpcall) up_;
            piup.setResult(value);
        }
    }

    public void set_exception(org.omg.CORBA.Any value) {
        if (arguments_ == null)
            throw new org.omg.CORBA.BAD_INV_ORDER("arguments() has not "
                    + "been called");

        if (result_ != null)
            throw new org.omg.CORBA.BAD_INV_ORDER("set_result() has already "
                    + "been called");

        if (exception_ != null)
            throw new org.omg.CORBA.BAD_INV_ORDER("set_exception() has "
                    + "already been called");

        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(value.type());
        if (origTC.kind() != org.omg.CORBA.TCKind.tk_except)
            throw new org.omg.CORBA.BAD_PARAM(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorNoExceptionInAny),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoExceptionInAny,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        exception_ = value;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ServerRequest(org.omg.PortableServer.DynamicImplementation servant,
            org.apache.yoko.orb.OB.Upcall upcall) {
        servant_ = servant;
        delegate_ = (org.apache.yoko.orb.PortableServer.Delegate) servant
                ._get_delegate();
        up_ = upcall;
    }

    public org.omg.CORBA.Any _OB_exception() {
        return exception_;
    }

    public void _OB_finishUnmarshal()
            throws org.apache.yoko.orb.OB.LocationForward {
        if (arguments_ == null)
            delegate_._OB_preUnmarshal(servant_, up_);

        delegate_._OB_postUnmarshal(servant_, up_);
    }

    public void _OB_postinvoke() throws org.apache.yoko.orb.OB.LocationForward {
        if (exception_ == null)
            delegate_._OB_postinvoke(servant_, up_);
    }

    public void _OB_doMarshal() throws org.apache.yoko.orb.OB.LocationForward {
        if (exception_ != null) {
            org.omg.CORBA.TypeCode tc = exception_.type();
            String id = null;
            try {
                id = tc.id();
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }

            if (org.apache.yoko.orb.OB.Util.isSystemException(id)) {
                org.omg.CORBA.portable.InputStream in = exception_
                        .create_input_stream();
                org.omg.CORBA.SystemException ex = org.apache.yoko.orb.OB.Util
                        .unmarshalSystemException(in);
                throw ex;
            } else {
                up_.setUserException(exception_);
            }
        } else {
            org.omg.CORBA.portable.OutputStream out = delegate_._OB_preMarshal(
                    servant_, up_);

            try {
                if (result_ != null)
                    result_.write_value(out);

                if (arguments_ != null) {
                    try {
                        for (int i = 0; i < arguments_.count(); i++) {
                            org.omg.CORBA.NamedValue nv = arguments_.item(i);
                            if (nv.flags() != org.omg.CORBA.ARG_IN.value) {
                                nv.value().write_value(out);
                            }
                        }
                    } catch (org.omg.CORBA.Bounds ex) {
                        org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                    }
                }
            } catch (org.omg.CORBA.SystemException ex) {
                delegate_._OB_marshalEx(servant_, up_, ex);
            }

            delegate_._OB_postMarshal(servant_, up_);
        }
    }
}

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

package org.apache.yoko.orb.PortableInterceptor;

final class ArgumentStrategySII extends ArgumentStrategy {
    protected org.apache.yoko.orb.OB.ParameterDesc[] argDesc_;

    protected org.apache.yoko.orb.OB.ParameterDesc retDesc_;

    protected org.omg.CORBA.TypeCode[] exceptionTC_;

    // ------------------------------------------------------------------
    // ArgumentStrategySII private and protected member implementations
    // ------------------------------------------------------------------

    //
    // Fill the contents of the Any using a ParameterDesc descriptor
    //
    protected void fillAny(org.omg.CORBA.Any any,
            org.apache.yoko.orb.OB.ParameterDesc desc) {
        org.omg.CORBA.portable.OutputStream out = orb_.create_output_stream();
        desc.param._write(out);
        any.read_value(out.create_input_stream(), desc.tc);
    }

    // ------------------------------------------------------------------
    // ArgumentStrategySII package member implementations
    // ------------------------------------------------------------------

    ArgumentStrategySII(org.omg.CORBA.ORB orb,
            org.apache.yoko.orb.OB.ParameterDesc[] argDesc,
            org.apache.yoko.orb.OB.ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) {
        super(orb);

        argDesc_ = argDesc;
        retDesc_ = retDesc;
        exceptionTC_ = exceptionTC;
    }

    //
    // Get the arguments
    //
    org.omg.Dynamic.Parameter[] arguments() {
        if (!argsAvail_)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall)
                            + ": arguments unavailable",
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        int nargDesc = 0;
        if (argDesc_ != null)
            nargDesc = argDesc_.length;
        org.omg.Dynamic.Parameter[] pl = new org.omg.Dynamic.Parameter[nargDesc];
        for (int i = 0; i < nargDesc; i++) {
            pl[i] = new org.omg.Dynamic.Parameter();
            pl[i].argument = orb_.create_any();

            switch (argDesc_[i].mode) {
            case 0: // in
                pl[i].mode = org.omg.CORBA.ParameterMode.PARAM_IN;
                fillAny(pl[i].argument, argDesc_[i]);
                break;

            case 1: // out
                pl[i].mode = org.omg.CORBA.ParameterMode.PARAM_OUT;
                //
                // The value may only be copied if there is a successful
                // response
                //
                if (resultAvail_)
                    fillAny(pl[i].argument, argDesc_[i]);
                break;

            case 2: // inout
                pl[i].mode = org.omg.CORBA.ParameterMode.PARAM_INOUT;
                fillAny(pl[i].argument, argDesc_[i]);
                break;

            default:
                org.apache.yoko.orb.OB.Assert._OB_assert(false);
            }
        }

        return pl;
    }

    //
    // Get the exceptions
    //
    org.omg.CORBA.TypeCode[] exceptions() {
        if (!exceptAvail_)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall)
                            + ": exceptions unavailable",
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        int nexceptionTC = 0;
        if (exceptionTC_ != null)
            nexceptionTC = exceptionTC_.length;
        org.omg.CORBA.TypeCode[] e = new org.omg.CORBA.TypeCode[nexceptionTC];
        if (nexceptionTC > 0)
            System.arraycopy(exceptionTC_, 0, e, 0, nexceptionTC);

        return e;
    }

    //
    // Get the result
    //
    org.omg.CORBA.Any result() {
        if (!resultAvail_)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall)
                            + ": result unavailable",
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        org.omg.CORBA.Any any = orb_.create_any();
        if (retDesc_ != null)
            fillAny(any, retDesc_);
        else
            any.type(orb_.get_primitive_tc(org.omg.CORBA.TCKind.tk_void));
        return any;
    }

    //
    // Set the result (server side only)
    //
    void setResult(org.omg.CORBA.Any any) {
        org.apache.yoko.orb.OB.Assert._OB_assert(false);
    }
}

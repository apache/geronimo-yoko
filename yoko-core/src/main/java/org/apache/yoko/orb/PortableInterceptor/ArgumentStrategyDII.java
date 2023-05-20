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
package org.apache.yoko.orb.PortableInterceptor;

import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;

final class ArgumentStrategyDII extends ArgumentStrategy {
    protected org.omg.CORBA.NVList args_;

    protected org.omg.CORBA.NamedValue result_;

    protected org.omg.CORBA.ExceptionList exceptions_;

    protected org.omg.CORBA.Any resultAny_;

    // ------------------------------------------------------------------
    // ArgumentStrategyDII package member implementations
    // ------------------------------------------------------------------

    ArgumentStrategyDII(org.omg.CORBA.ORB orb, org.omg.CORBA.NVList args,
            org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList exceptions) {
        super(orb);

        args_ = args;
        result_ = result;
        exceptions_ = exceptions;
    }

    ArgumentStrategyDII(org.omg.CORBA.ORB orb, org.omg.CORBA.NVList args) {
        super(orb);

        args_ = args;
    }

    //
    // Get the arguments
    //
    org.omg.Dynamic.Parameter[] arguments() {
        if (!argsAvail_)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        org.omg.Dynamic.Parameter[] pl;

        int count;
        if (args_ != null)
            count = args_.count();
        else
            count = 0;
        pl = new org.omg.Dynamic.Parameter[count];
        for (int i = 0; i < count; i++) {
            pl[i] = new org.omg.Dynamic.Parameter();
            pl[i].argument = orb_.create_any();

            org.omg.CORBA.NamedValue v = null;
            try {
                v = args_.item(i);
            } catch (org.omg.CORBA.Bounds ex) {
                throw Assert.fail(ex);
            }

            switch (v.flags()) {
            case org.omg.CORBA.ARG_IN.value:
                pl[i].mode = org.omg.CORBA.ParameterMode.PARAM_IN;
                pl[i].argument.read_value(v.value().create_input_stream(), v
                        .value().type());
                break;

            case org.omg.CORBA.ARG_OUT.value:
                pl[i].mode = org.omg.CORBA.ParameterMode.PARAM_OUT;
                //
                // The value may only be copied if there is a successful
                // response
                //
                if (resultAvail_)
                    pl[i].argument.read_value(v.value().create_input_stream(),
                            v.value().type());
                break;

            case org.omg.CORBA.ARG_INOUT.value:
                pl[i].mode = org.omg.CORBA.ParameterMode.PARAM_INOUT;
                pl[i].argument.read_value(v.value().create_input_stream(), v
                        .value().type());
                break;

            default:
                throw Assert.fail();
            }
        }

        return pl;
    }

    //
    // Get the exceptions
    //
    org.omg.CORBA.TypeCode[] exceptions() {
        //
        // If exceptions are never available then this indicates a
        // NO_RESOURCES exception
        //
        if (exceptNeverAvail_)
            throw new org.omg.CORBA.NO_RESOURCES(
                    MinorCodes
                            .describeNoResources(MinorCodes.MinorInvalidBinding)
                            + ": exceptions unavailable",
                    MinorCodes.MinorInvalidBinding,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        if (!exceptAvail_)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall)
                            + ": exceptions unavailable",
                    MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        org.omg.CORBA.TypeCode[] e;
        int count;
        if (exceptions_ != null)
            count = exceptions_.count();
        else
            count = 0;
        e = new org.omg.CORBA.TypeCode[count];
        for (int i = 0; i < count; i++) {
            try {
                e[i] = exceptions_.item(i);
            } catch (org.omg.CORBA.Bounds ex) {
                throw Assert.fail(ex);
            }
        }

        return e;
    }

    //
    // Get the result
    //
    org.omg.CORBA.Any result() {
        if (!resultAvail_)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall)
                            + ": result unavailable",
                    MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        org.omg.CORBA.Any any = orb_.create_any();
        if (resultAny_ != null) {
            any.read_value(resultAny_.create_input_stream(), resultAny_.type());
        } else if (result_ == null) {
            any.type(orb_.get_primitive_tc(org.omg.CORBA.TCKind.tk_void));
        } else {
            any.read_value(result_.value().create_input_stream(), result_
                    .value().type());
        }

        return any;
    }

    //
    // Set the result (server side only)
    //
    void setResult(org.omg.CORBA.Any any) {
        resultAny_ = orb_.create_any();
        resultAny_.read_value(any.create_input_stream(), any.type());
    }
}

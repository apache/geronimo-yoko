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

final class ArgumentStrategyNull extends ArgumentStrategy {
    ArgumentStrategyNull(org.omg.CORBA.ORB orb) {
        super(orb);
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

        throw new org.omg.CORBA.NO_RESOURCES(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeNoResources(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidBinding)
                        + ": arguments unavailable",
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidBinding,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
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

        throw new org.omg.CORBA.NO_RESOURCES(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeNoResources(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidBinding)
                        + ": exceptions unavailable",
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidBinding,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
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

        throw new org.omg.CORBA.NO_RESOURCES(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeNoResources(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidBinding)
                        + ": result unavailable",
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidBinding,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    //
    // Set the result (server side only)
    //
    void setResult(org.omg.CORBA.Any any) {
        org.apache.yoko.orb.OB.Assert._OB_assert(false);
    }
}

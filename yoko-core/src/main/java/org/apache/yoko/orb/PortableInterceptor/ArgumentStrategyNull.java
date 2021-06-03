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

import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;

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
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall)
                            + ": arguments unavailable",
                    MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        throw new org.omg.CORBA.NO_RESOURCES(
                MinorCodes
                        .describeNoResources(MinorCodes.MinorInvalidBinding)
                        + ": arguments unavailable",
                MinorCodes.MinorInvalidBinding,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    //
    // Get the exceptions
    //
    org.omg.CORBA.TypeCode[] exceptions() {
        if (!exceptAvail_)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall)
                            + ": exceptions unavailable",
                    MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        throw new org.omg.CORBA.NO_RESOURCES(
                MinorCodes
                        .describeNoResources(MinorCodes.MinorInvalidBinding)
                        + ": exceptions unavailable",
                MinorCodes.MinorInvalidBinding,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
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

        throw new org.omg.CORBA.NO_RESOURCES(
                MinorCodes
                        .describeNoResources(MinorCodes.MinorInvalidBinding)
                        + ": result unavailable",
                MinorCodes.MinorInvalidBinding,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    //
    // Set the result (server side only)
    //
    void setResult(org.omg.CORBA.Any any) {
        throw Assert.fail();
    }
}

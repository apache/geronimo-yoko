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

package org.apache.yoko.orb.OB;

public class PIArgsDowncall extends PIDowncall {
    //
    // Argument, result and exception list description provided by the
    // static stubs
    //
    protected ParameterDesc[] argDesc_;

    protected ParameterDesc retDesc_;

    protected org.omg.CORBA.TypeCode[] exceptionTC_;

    // ----------------------------------------------------------------------
    // PIArgsDowncall private and protected member implementations
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // PIArgsDowncall public member implementations
    // ----------------------------------------------------------------------

    public PIArgsDowncall(ORBInstance orbInstance, Client client,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp,
            org.omg.IOP.IOR IOR, org.omg.IOP.IOR origIOR,
            /**/PIManager piManager, ParameterDesc[] argDesc,
            ParameterDesc retDesc, org.omg.CORBA.TypeCode[] exceptionTC) {
        super(orbInstance, client, profileInfo, policies, op, resp, IOR,
                origIOR, piManager);
        argDesc_ = argDesc;
        retDesc_ = retDesc;
        exceptionTC_ = exceptionTC;
    }

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal()
            throws LocationForward, FailureException {
        requestInfo_ = piManager_.clientSendRequest(op_, responseExpected_,
                IOR_, origIOR_, profileInfo_, policies_.value, requestSCL_,
                replySCL_, argDesc_, retDesc_, exceptionTC_);

        return preMarshalBase(); // Equivalent to Downcall::preMarshal()
    }
}

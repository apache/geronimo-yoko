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

public class PIDIIDowncall extends PIDowncall {
    //
    // Argument, result and exception list description provided by the
    // DII
    //
    protected org.omg.CORBA.NVList args_;

    protected org.omg.CORBA.NamedValue result_;

    protected org.omg.CORBA.ExceptionList exceptionList_;

    // ----------------------------------------------------------------------
    // PIDIIDowncall private and protected member implementations
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // PIDIIDowncall public member implementations
    // ----------------------------------------------------------------------

    public PIDIIDowncall(ORBInstance orbInstance, Client client,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp,
            org.omg.IOP.IOR IOR, org.omg.IOP.IOR origIOR, PIManager piManager,
            org.omg.CORBA.NVList args, org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList exceptions) {
        super(orbInstance, client, profileInfo, policies, op, resp, IOR,
                origIOR, piManager);
        args_ = args;
        result_ = result;
        exceptionList_ = exceptions;
    }

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal()
            throws LocationForward, FailureException {
        requestInfo_ = piManager_.clientSendRequest(op_, responseExpected_,
                IOR_, origIOR_, profileInfo_, policies_.value, requestSCL_,
                replySCL_, args_, result_, exceptionList_);

        return preMarshalBase(); // Equivalent to Downcall::preMarshal()
    }
}

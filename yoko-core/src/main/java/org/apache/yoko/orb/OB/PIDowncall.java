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

public class PIDowncall extends Downcall {
    //
    // The IOR and the original IOR
    //
    protected org.omg.IOP.IOR IOR_;

    protected org.omg.IOP.IOR origIOR_;

    //
    // The PortableInterceptor manager
    //
    protected PIManager piManager_;

    //
    // Holds the exception ID if state_ is DowncallStateUserException,
    // DowncallStateSystemException, or DowncallStateFailureException
    //
    // In Java, this field is in Downcall
    //
    // protected String exId_;

    //
    // The ClientRequestInfo object provided by the interceptors
    //
    protected org.omg.PortableInterceptor.ClientRequestInfo requestInfo_;

    // ----------------------------------------------------------------------
    // PIDowncall private and protected member implementations
    // ----------------------------------------------------------------------

    void checkForException() throws LocationForward, FailureException {
        //
        // If ex_ is set, but exId_ is not, then set it now
        //
        // TODO: Postpone this in Java?
        //
        if (ex_ != null && exId_ == null)
            exId_ = Util.getExceptionId(ex_);

        switch (state_) {
        case DowncallStateUserException:
            //
            // For Java portable stubs, we'll have the repository ID
            // but not the exception instance, so we pass UNKNOWN to
            // the interceptors but DO NOT modify the Downcall state.
            //
            if (ex_ == null && exId_ != null) {
                org.omg.CORBA.Any any = new org.apache.yoko.orb.CORBA.Any(
                        orbInstance_);
                org.omg.CORBA.UNKNOWN sys = new org.omg.CORBA.UNKNOWN(
                        MinorCodes
                                .describeUnknown(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException)
                                + ": " + exId_,
                        org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException,
                        org.omg.CORBA.CompletionStatus.COMPLETED_YES);
                org.omg.CORBA.UNKNOWNHelper.insert(any, sys);
                org.omg.CORBA.UnknownUserException unk = new org.omg.CORBA.UnknownUserException(
                        any);
                piManager_.clientReceiveException(requestInfo_, false, unk,
                        exId_);
            }
            //
            // Only invoke interceptor if a user exception has been
            // set
            //
            if (ex_ != null)
                piManager_.clientReceiveException(requestInfo_, false, ex_,
                        exId_);
            break;

        case DowncallStateSystemException:
            Assert._OB_assert(ex_ != null);
            piManager_.clientReceiveException(requestInfo_, true, ex_, exId_);
            break;

        case DowncallStateFailureException:
            try {
                Assert._OB_assert(ex_ != null);
                piManager_.clientReceiveException(requestInfo_, true, ex_,
                        exId_);
            } catch (org.omg.CORBA.SystemException ex) {
                //
                // Ignore any exception translations for failure
                // exceptions
                //
            }
            break;

        case DowncallStateForward:
        case DowncallStateForwardPerm:
            Assert._OB_assert(forwardIOR_ != null);
            piManager_.clientReceiveLocationForward(requestInfo_, forwardIOR_);
            break;

        default:
            break;
        }

        super.checkForException();
    }

    // ----------------------------------------------------------------------
    // PIDowncall public member implementations
    // ----------------------------------------------------------------------

    public PIDowncall(ORBInstance orbInstance, Client client,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp,
            org.omg.IOP.IOR IOR, org.omg.IOP.IOR origIOR,
            /**/PIManager piManager) {
        super(orbInstance, client, profileInfo, policies, op, resp);
        IOR_ = IOR;
        origIOR_ = origIOR;
        piManager_ = piManager;
    }

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal()
            throws LocationForward, FailureException {
        requestInfo_ = piManager_.clientSendRequest(op_, responseExpected_,
                IOR_, origIOR_, profileInfo_, policies_.value, requestSCL_,
                replySCL_);

        return super.preMarshal();
    }

    public void postUnmarshal() throws LocationForward, FailureException {
        //
        // If the result of this downcall is a user exception, but no user
        // exception could be unmarshalled, then use the system exception
        // UNKNOWN, but keep the original exception ID
        //
        // In Java, the portable stubs only provide the repository ID of
        // the user exception, not the exception instance. We want to
        // report UNKNOWN to the interceptors, but do not want to change
        // the downcall status if we have the repository ID.
        //
        if (state_ == DowncallStateUserException && ex_ == null
                && exId_ == null) {
            String id = unmarshalExceptionId();
            setSystemException(new org.omg.CORBA.UNKNOWN(org.apache.yoko.orb.OB.MinorCodes
                    .describeUnknown(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException)
                    + ": " + id, org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException,
                    org.omg.CORBA.CompletionStatus.COMPLETED_YES));
            exId_ = id;
        }

        super.postUnmarshal();

        //
        // Java only - Downcall.checkForException() does not raise
        // UserExceptions, so we return now and let the stub handle it
        //
        if (state_ == DowncallStateUserException)
            return;

        if (responseExpected_)
            Assert._OB_assert(state_ == DowncallStateNoException);
        else
            Assert._OB_assert(state_ == DowncallStateUnsent
                    || state_ == DowncallStateNoException);
        piManager_.clientReceiveReply(requestInfo_);
    }

    public void setUserException(org.omg.CORBA.UserException ex, String exId) {
        super.setUserException(ex, exId);
        exId_ = exId;
    }

    public void setUserException(org.omg.CORBA.UserException ex) {
        Assert._OB_assert(responseExpected_);
        Assert._OB_assert(ex_ == null);
        state_ = DowncallStateUserException;
        ex_ = ex;
    }
}

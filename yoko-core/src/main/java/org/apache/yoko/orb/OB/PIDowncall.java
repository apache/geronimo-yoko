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

import org.apache.yoko.orb.CORBA.Any;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.util.concurrent.AutoLock;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UNKNOWNHelper;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.UserException;
import org.omg.IOP.IOR;
import org.omg.PortableInterceptor.ClientRequestInfo;

public class PIDowncall extends Downcall {
    //
    // The IOR and the original IOR
    //
    protected IOR IOR_;

    protected IOR origIOR_;

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
    protected ClientRequestInfo requestInfo_;

    // ----------------------------------------------------------------------
    // PIDowncall private and protected member implementations
    // ----------------------------------------------------------------------

    void checkForException() throws LocationForward, FailureException {
        try (AutoLock lock = stateLock.getReadLock()) {
            //
            // If ex_ is set, but exId_ is not, then set it now
            //
            // TODO: Postpone this in Java?
            //
            if (ex_ != null && exId_ == null)
                exId_ = Util.getExceptionId(ex_);

            switch (state) {
                case USER_EXCEPTION:
                    //
                    // For Java portable stubs, we'll have the repository ID
                    // but not the exception instance, so we pass UNKNOWN to
                    // the interceptors but DO NOT modify the Downcall state.
                    //
                    if (ex_ == null && exId_ != null) {
                        org.omg.CORBA.Any any = new Any(
                                orbInstance_);
                        UNKNOWN sys = new UNKNOWN(
                                MinorCodes
                                .describeUnknown(MinorCodes.MinorUnknownUserException)
                                + ": " + exId_,
                                MinorCodes.MinorUnknownUserException,
                                CompletionStatus.COMPLETED_YES);
                        UNKNOWNHelper.insert(any, sys);
                        UnknownUserException unk = new UnknownUserException(
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

                case SYSTEM_EXCEPTION:
                    Assert._OB_assert(ex_ != null);
                    piManager_.clientReceiveException(requestInfo_, true, ex_, exId_);
                    break;

                case FAILURE_EXCEPTION:
                    try {
                        Assert._OB_assert(ex_ != null);
                        piManager_.clientReceiveException(requestInfo_, true, ex_,
                                exId_);
                    } catch (SystemException ex) {
                        //
                        // Ignore any exception translations for failure
                        // exceptions
                        //
                    }
                    break;

                case FORWARD:
                case FORWARD_PERM:
                    Assert._OB_assert(forwardIOR_ != null);
                    piManager_.clientReceiveLocationForward(requestInfo_, forwardIOR_);
                    break;

                default:
                    break;
            }

            super.checkForException();
        }
    }

    // ----------------------------------------------------------------------
    // PIDowncall public member implementations
    // ----------------------------------------------------------------------

    public PIDowncall(ORBInstance orbInstance, Client client,
            ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp,
            IOR IOR, IOR origIOR,
            /**/PIManager piManager) {
        super(orbInstance, client, profileInfo, policies, op, resp);
        IOR_ = IOR;
        origIOR_ = origIOR;
        piManager_ = piManager;
    }

    public OutputStream preMarshal()
            throws LocationForward, FailureException {
        requestInfo_ = piManager_.clientSendRequest(op_, responseExpected_,
                IOR_, origIOR_, profileInfo_, policies_.value, requestSCL_,
                replySCL_);

        return super.preMarshal();
    }

    public void postUnmarshal() throws LocationForward, FailureException {
        try (AutoLock lock = stateLock.getReadLock()) {
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
            if (state == State.USER_EXCEPTION && ex_ == null && exId_ == null) {
                String id = unmarshalExceptionId();
                setSystemException(new UNKNOWN(MinorCodes
                        .describeUnknown(MinorCodes.MinorUnknownUserException)
                        + ": " + id, MinorCodes.MinorUnknownUserException,
                        CompletionStatus.COMPLETED_YES));
                exId_ = id;
            }
    
            super.postUnmarshal();
    
            //
            // Java only - Downcall.checkForException() does not raise
            // UserExceptions, so we return now and let the stub handle it
            //
            if (state == State.USER_EXCEPTION)
                return;
    
            if (responseExpected_)
                Assert._OB_assert(state == State.NO_EXCEPTION);
            else
                Assert._OB_assert(state == State.UNSENT || state == State.NO_EXCEPTION);
            piManager_.clientReceiveReply(requestInfo_);
        }
    }

    public void setUserException(UserException ex, String exId) {
        super.setUserException(ex, exId);
        exId_ = exId;
    }
}

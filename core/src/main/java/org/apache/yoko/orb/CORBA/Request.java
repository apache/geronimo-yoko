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
import java.util.logging.Level;

import org.apache.yoko.orb.OB.Logger;

//
// This class must be public - see org.apache.yoko.orb.OB.MutliRequestSender
//
final public class Request extends org.omg.CORBA.Request {
    private org.omg.CORBA.Object target_;

    private String operation_;

    private org.omg.CORBA.NVList arguments_;

    private org.omg.CORBA.NamedValue result_;

    private org.omg.CORBA.Environment environment_;

    private org.omg.CORBA.ExceptionList exceptions_;

    private org.omg.CORBA.ContextList contexts_;

    private org.omg.CORBA.Context ctx_;

    private Delegate delegate_;

    private org.apache.yoko.orb.OB.DowncallStub downcallStub_;

    private org.apache.yoko.orb.OB.Downcall downcall_;

    private static final int RequestStateUnsent = 0;

    private static final int RequestStatePending = 1;

    private static final int RequestStateSent = 2;

    private static final int RequestStateReceiving = 3;

    private static final int RequestStateReceived = 4;

    private static final int RequestStateDone = 5;

    private int state_;

    private boolean pollable_; // Can this request be polled?

    private boolean polling_; // Is poll_response in progress?

    private java.lang.Object stateMutex_ = new java.lang.Object();

    private boolean raiseDIIExceptions_;

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void marshal() throws org.apache.yoko.orb.OB.LocationForward,
            org.apache.yoko.orb.OB.FailureException {
        org.apache.yoko.orb.OB.Assert._OB_assert(downcallStub_ != null);
        org.apache.yoko.orb.OB.Assert._OB_assert(downcall_ != null);

        OutputStream out = downcallStub_.preMarshal(downcall_);

        try {
            java.util.Vector ctxVec = new java.util.Vector();

            if (ctx_ != null) {
                for (int i = 0; i < contexts_.count(); i++) {
                    String item = contexts_.item(i);
                    ((Context) ctx_)._OB_getValues("", 0, item, ctxVec);
                }
            }

            for (int i = 0; i < arguments_.count(); i++) {
                org.omg.CORBA.NamedValue nv = arguments_.item(i);
                // Note: Don't use != ARG_OUT here, because flags can
                // also have values other than ARG_IN, ARG_OUT, or
                // ARG_INOUT
                if (nv.flags() == org.omg.CORBA.ARG_IN.value
                        || nv.flags() == org.omg.CORBA.ARG_INOUT.value)
                    nv.value().write_value(out);
            }

            if (ctx_ != null) {
                int len = ctxVec.size();
                out.write_ulong(len);
                for (int i = 0; i < len; i++)
                    out.write_string((String) ctxVec.elementAt(i));
            }
        } catch (org.omg.CORBA.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);    
        } catch (org.omg.CORBA.SystemException ex) {
            downcallStub_.marshalEx(downcall_, ex);
        }

        downcallStub_.postMarshal(downcall_);
    }

    private void unmarshal() throws org.apache.yoko.orb.OB.LocationForward,
            org.apache.yoko.orb.OB.FailureException {
        org.apache.yoko.orb.OB.Assert._OB_assert(downcallStub_ != null);
        org.apache.yoko.orb.OB.Assert._OB_assert(downcall_ != null);

        org.omg.CORBA.BooleanHolder uex = new org.omg.CORBA.BooleanHolder();
        InputStream in = downcallStub_.preUnmarshal(downcall_, uex);

        if (in == null) {
            org.apache.yoko.orb.OB.Assert._OB_assert(!uex.value);
            downcallStub_.postUnmarshal(downcall_);
            return;
        }

        if (uex.value) {
            String id = null;
            try {
                id = downcallStub_.unmarshalExceptionId(downcall_);

                for (int i = 0; i < exceptions_.count(); i++) {
                    org.omg.CORBA.TypeCode tc = exceptions_.item(i);
                    if (tc.id().equals(id)) {
                        org.omg.CORBA.Any any = new Any(delegate_
                                ._OB_ORBInstance());
                        any.read_value(in, tc);
                        org.omg.CORBA.UnknownUserException ex = new org.omg.CORBA.UnknownUserException(
                                any);
                        downcallStub_.setUserException(downcall_, ex, id);
                        //
                        // Downcall does not raise UserExceptions in Java,
                        // so we need to set this explicitly
                        //
                        environment_.exception(ex);
                        break;
                    }
                }
            } catch (org.omg.CORBA.Bounds ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);    
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);     
            } catch (org.omg.CORBA.SystemException ex) {
                downcallStub_.unmarshalEx(downcall_, ex);
            }
            downcallStub_.postUnmarshal(downcall_);
        } else {
            try {
                org.omg.CORBA.Any any = result_.value();
                any.read_value(in, any.type());

                try {
                    for (int i = 0; i < arguments_.count(); i++) {
                        org.omg.CORBA.NamedValue nv = arguments_.item(i);
                        // Note: Don't use != ARG_IN here, because flags can
                        // also have values other than ARG_IN, ARG_OUT, or
                        // ARG_INOUT
                        if (nv.flags() == org.omg.CORBA.ARG_OUT.value
                                || nv.flags() == org.omg.CORBA.ARG_INOUT.value) {
                            any = nv.value();
                            any.read_value(in, any.type());
                        }
                    }
                } catch (org.omg.CORBA.Bounds ex) {
                    org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                }
            } catch (org.omg.CORBA.SystemException ex) {
                downcallStub_.unmarshalEx(downcall_, ex);
            }
            downcallStub_.postUnmarshal(downcall_);
        }
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public org.omg.CORBA.Object target() {
        return target_;
    }

    public String operation() {
        return operation_;
    }

    public org.omg.CORBA.NVList arguments() {
        return arguments_;
    }

    public org.omg.CORBA.NamedValue result() {
        return result_;
    }

    public org.omg.CORBA.Environment env() {
        return environment_;
    }

    public org.omg.CORBA.ExceptionList exceptions() {
        return exceptions_;
    }

    public org.omg.CORBA.ContextList contexts() {
        return contexts_;
    }

    public org.omg.CORBA.Context ctx() {
        return ctx_;
    }

    public void ctx(org.omg.CORBA.Context c) {
        ctx_ = c;
    }

    public org.omg.CORBA.Any add_in_arg() {
        return (arguments_.add(org.omg.CORBA.ARG_IN.value)).value();
    }

    public org.omg.CORBA.Any add_named_in_arg(String name) {
        return (arguments_.add_item(name, org.omg.CORBA.ARG_IN.value)).value();
    }

    public org.omg.CORBA.Any add_inout_arg() {
        return (arguments_.add(org.omg.CORBA.ARG_INOUT.value)).value();
    }

    public org.omg.CORBA.Any add_named_inout_arg(String name) {
        return (arguments_.add_item(name, org.omg.CORBA.ARG_INOUT.value))
                .value();
    }

    public org.omg.CORBA.Any add_out_arg() {
        return (arguments_.add(org.omg.CORBA.ARG_OUT.value)).value();
    }

    public org.omg.CORBA.Any add_named_out_arg(String name) {
        return (arguments_.add_item(name, org.omg.CORBA.ARG_OUT.value)).value();
    }

    public void set_return_type(org.omg.CORBA.TypeCode tc) {
        result_.value().type(tc);
    }

    public org.omg.CORBA.Any return_value() {
        return result_.value();
    }

    public void invoke() {
        synchronized (stateMutex_) {
            if (state_ == RequestStateDone)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorRequestAlreadySent),
                        org.apache.yoko.orb.OB.MinorCodes.MinorRequestAlreadySent,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            else if (state_ != RequestStateUnsent)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorDuplicateSend),
                        org.apache.yoko.orb.OB.MinorCodes.MinorDuplicateSend,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            state_ = RequestStatePending;
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(downcallStub_ == null);
        org.apache.yoko.orb.OB.Assert._OB_assert(downcall_ == null);

        try {
            RetryInfo info = new RetryInfo();
            while (true) {
                try {
                    downcallStub_ = delegate_._OB_getDowncallStub(target_);

                    while (true) {
                        downcall_ = downcallStub_.createPIDIIDowncall(
                                operation_, true, arguments_, result_,
                                exceptions_);

                        try {
                            marshal();
                            downcallStub_.request(downcall_);
                            unmarshal();
                            synchronized (stateMutex_) {
                                state_ = RequestStateDone;
                            }
                            return;
                        } catch (org.apache.yoko.orb.OB.FailureException ex) {
                            downcallStub_.handleFailureException(downcall_, ex);
                        }
                    } // while(true)
                } catch (Exception ex) {
                    delegate_._OB_handleException(target_, ex, info, false);
                }
            } // while(true)
        } catch (org.omg.CORBA.SystemException ex) {
            environment_.exception(ex);
            synchronized (stateMutex_) {
                state_ = RequestStateDone;
            }
            if (raiseDIIExceptions_)
                throw ex;
            return;
        }
    }

    public void send_oneway() {
        synchronized (stateMutex_) {
            if (state_ == RequestStateDone)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorRequestAlreadySent),
                        org.apache.yoko.orb.OB.MinorCodes.MinorRequestAlreadySent,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            else if (state_ != RequestStateUnsent)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorDuplicateSend),
                        org.apache.yoko.orb.OB.MinorCodes.MinorDuplicateSend,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            state_ = RequestStatePending;
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(downcallStub_ == null);
        org.apache.yoko.orb.OB.Assert._OB_assert(downcall_ == null);

        try {
            RetryInfo info = new RetryInfo();
            while (true) {
                try {
                    downcallStub_ = delegate_._OB_getDowncallStub(target_);

                    while (true) {
                        downcall_ = downcallStub_.createPIDIIDowncall(
                                operation_, false, arguments_, result_,
                                exceptions_);

                        try {
                            marshal();
                            downcallStub_.oneway(downcall_);
                            unmarshal();
                            synchronized (stateMutex_) {
                                state_ = RequestStateDone;
                            }
                            return;
                        } catch (org.apache.yoko.orb.OB.FailureException ex) {
                            downcallStub_.handleFailureException(downcall_, ex);
                        }
                    } // while(true)
                } catch (Exception ex) {
                    delegate_._OB_handleException(target_, ex, info, false);
                }
            } // while(true)
        } catch (org.omg.CORBA.SystemException ex) {
            Logger logger = delegate_._OB_ORBInstance().getLogger(); 
            logger.log(java.util.logging.Level.FINE, "Exception sending request", ex); 
            environment_.exception(ex);
            synchronized (stateMutex_) {
                state_ = RequestStateDone;
            }
            if (raiseDIIExceptions_)
                throw ex;
            return;
        }
    }

    public void send_deferred() {
        synchronized (stateMutex_) {
            if (state_ == RequestStateDone)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorRequestAlreadySent),
                        org.apache.yoko.orb.OB.MinorCodes.MinorRequestAlreadySent,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            else if (state_ != RequestStateUnsent)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorDuplicateSend),
                        org.apache.yoko.orb.OB.MinorCodes.MinorDuplicateSend,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            state_ = RequestStatePending;
            pollable_ = true;
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(downcallStub_ == null);
        org.apache.yoko.orb.OB.Assert._OB_assert(downcall_ == null);

        org.apache.yoko.orb.OB.ORBInstance orbInstance = delegate_
                ._OB_ORBInstance();
        org.apache.yoko.orb.OB.MultiRequestSender multi = orbInstance
                .getMultiRequestSender();

        try {
            RetryInfo info = new RetryInfo();
            while (true) {
                try {
                    downcallStub_ = delegate_._OB_getDowncallStub(target_);

                    while (true) {
                        downcall_ = downcallStub_.createPIDIIDowncall(
                                operation_, true, arguments_, result_,
                                exceptions_);

                        try {
                            marshal();
                            downcallStub_.deferred(downcall_);
                            synchronized (stateMutex_) {
                                multi.addDeferredRequest(this);
                                state_ = RequestStateSent;
                            }
                            return;
                        } catch (org.apache.yoko.orb.OB.FailureException ex) {
                            downcallStub_.handleFailureException(downcall_, ex);
                        }
                    } // while(true)
                } catch (Exception ex) {
                    delegate_._OB_handleException(target_, ex, info, false);
                }
            } // while(true)
        } catch (org.omg.CORBA.SystemException ex) {
            Logger logger = delegate_._OB_ORBInstance().getLogger(); 
            logger.log(java.util.logging.Level.FINE, "Exception sending deferred request", ex); 
            environment_.exception(ex);
            synchronized (stateMutex_) {
                multi.addDeferredRequest(this);
                state_ = RequestStateReceived;
            }
            if (raiseDIIExceptions_)
                throw ex;
            return;
        }
    }

    public void get_response() {
        org.apache.yoko.orb.OB.ORBInstance orbInstance = delegate_
                ._OB_ORBInstance();
        org.apache.yoko.orb.OB.MultiRequestSender multi = orbInstance
                .getMultiRequestSender();

        synchronized (stateMutex_) {
            if (!pollable_)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorSynchronousRequest),
                        org.apache.yoko.orb.OB.MinorCodes.MinorSynchronousRequest,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            switch (state_) {
            case RequestStateUnsent:
            case RequestStatePending:
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent),
                        org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            case RequestStateSent:
                break;
            case RequestStateReceiving:
            case RequestStateDone:
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorResponseAlreadyReceived),
                        org.apache.yoko.orb.OB.MinorCodes.MinorResponseAlreadyReceived,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            case RequestStateReceived:
                multi.removeDeferredRequest(this);
                state_ = RequestStateDone;
                return;
            }

            //
            // Allow thread calling poll_response to complete
            //
            while (polling_) {
                try {
                    stateMutex_.wait();
                } catch (InterruptedException ex) {
                }
            }

            if (state_ == RequestStateReceived) {
                multi.removeDeferredRequest(this);
                state_ = RequestStateDone;
                return;
            } else {
                org.apache.yoko.orb.OB.Assert
                        ._OB_assert(state_ == RequestStateSent);
                multi.removeDeferredRequest(this);
                state_ = RequestStateReceiving;
            }
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(downcallStub_ != null);
        org.apache.yoko.orb.OB.Assert._OB_assert(downcall_ != null);

        boolean send = false;
        try {
            RetryInfo info = new RetryInfo();
            while (true) {
                try {
                    if (send)
                        downcallStub_ = delegate_._OB_getDowncallStub(target_);

                    while (true) {
                        if (send) {
                            downcall_ = downcallStub_.createPIDIIDowncall(
                                    operation_, true, arguments_, result_,
                                    exceptions_);
                        }

                        try {
                            if (send) {
                                marshal();
                                downcallStub_.deferred(downcall_);
                                send = false;
                            }

                            downcallStub_.response(downcall_);
                            unmarshal();
                            synchronized (stateMutex_) {
                                state_ = RequestStateDone;
                            }
                            return;
                        } catch (org.apache.yoko.orb.OB.FailureException ex) {
                            downcallStub_.handleFailureException(downcall_, ex);
                            send = true;
                        }
                    } // while(true)
                } catch (Exception ex) {
                    delegate_._OB_handleException(target_, ex, info, false);
                    send = true;
                }
            } // while(true)
        } catch (org.omg.CORBA.SystemException ex) {
            Logger logger = delegate_._OB_ORBInstance().getLogger(); 
            logger.log(java.util.logging.Level.FINE, "Exception getting request response", ex); 
            environment_.exception(ex);
            synchronized (stateMutex_) {
                state_ = RequestStateDone;
            }
            if (raiseDIIExceptions_)
                throw ex;
            return;
        }
    }

    public boolean poll_response() {
        synchronized (stateMutex_) {
            if (!pollable_)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorSynchronousRequest),
                        org.apache.yoko.orb.OB.MinorCodes.MinorSynchronousRequest,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            if (polling_)
                return false; // poll_response in progress

            switch (state_) {
            case RequestStateUnsent:
            case RequestStatePending:
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent),
                        org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            case RequestStateSent:
                break;
            case RequestStateReceiving: // get_response in progress
                return false;
            case RequestStateReceived:
                return true;
            case RequestStateDone:
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorResponseAlreadyReceived),
                        org.apache.yoko.orb.OB.MinorCodes.MinorResponseAlreadyReceived,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            polling_ = true;
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(downcallStub_ != null);
        org.apache.yoko.orb.OB.Assert._OB_assert(downcall_ != null);

        boolean send = false;
        try {
            RetryInfo info = new RetryInfo();
            while (true) {
                try {
                    if (send)
                        downcallStub_ = delegate_._OB_getDowncallStub(target_);

                    while (true) {
                        if (send) {
                            downcall_ = downcallStub_.createPIDIIDowncall(
                                    operation_, true, arguments_, result_,
                                    exceptions_);
                        }

                        try {
                            if (send) {
                                marshal();
                                downcallStub_.deferred(downcall_);
                                send = false;
                            }

                            if (downcallStub_.poll(downcall_)) {
                                unmarshal();
                                synchronized (stateMutex_) {
                                    state_ = RequestStateReceived;
                                    polling_ = false;
                                    stateMutex_.notify();
                                }
                                return true;
                            } else {
                                synchronized (stateMutex_) {
                                    polling_ = false;
                                    stateMutex_.notify();
                                }
                                return false;
                            }
                        } catch (org.apache.yoko.orb.OB.FailureException ex) {
                            downcallStub_.handleFailureException(downcall_, ex);
                            send = true;
                        }
                    } // while(true)
                } catch (Exception ex) {
                    delegate_._OB_handleException(target_, ex, info, false);
                    send = true;
                }
            } // while(true)
        } catch (org.omg.CORBA.SystemException ex) {
            Logger logger = delegate_._OB_ORBInstance().getLogger(); 
            logger.log(java.util.logging.Level.FINE, "Exception polling request response", ex); 
            environment_.exception(ex);
            synchronized (stateMutex_) {
                state_ = RequestStateReceived;
                polling_ = false;
                stateMutex_.notify();
            }
            if (raiseDIIExceptions_)
                throw ex;
            return true;
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    Request(org.omg.CORBA.Object target, String operation,
            org.omg.CORBA.NVList arguments, org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList exceptions,
            org.omg.CORBA.ContextList contexts) {
        delegate_ = (Delegate) (((org.omg.CORBA.portable.ObjectImpl) target)
                ._get_delegate());

        target_ = target;
        operation_ = operation;
        arguments_ = arguments;
        result_ = result;
        environment_ = new Environment();
        exceptions_ = exceptions;
        contexts_ = contexts;
        state_ = RequestStateUnsent;
        pollable_ = false;
        polling_ = false;

        ORB orb = (ORB) delegate_.orb(null);
        raiseDIIExceptions_ = orb._OB_raiseDIIExceptions();
    }

    Request(org.omg.CORBA.Object target, String operation,
            org.omg.CORBA.NVList arguments, org.omg.CORBA.NamedValue result) {
        delegate_ = (Delegate) (((org.omg.CORBA.portable.ObjectImpl) target)
                ._get_delegate());

        target_ = target;
        operation_ = operation;
        arguments_ = arguments;
        result_ = result;
        environment_ = new Environment();
        exceptions_ = new ExceptionList();
        contexts_ = new ContextList();
        state_ = RequestStateUnsent;
        pollable_ = false;
        polling_ = false;

        ORB orb = (ORB) delegate_.orb(null);
        raiseDIIExceptions_ = orb._OB_raiseDIIExceptions();
    }

    Request(org.omg.CORBA.Object target, String operation) {
        delegate_ = (Delegate) (((org.omg.CORBA.portable.ObjectImpl) target)
                ._get_delegate());

        target_ = target;
        operation_ = operation;
        arguments_ = new NVList(delegate_.orb(target_));
        result_ = new NamedValue("", new Any(delegate_._OB_ORBInstance()),
                org.omg.CORBA.ARG_OUT.value);
        environment_ = new Environment();
        exceptions_ = new ExceptionList();
        contexts_ = new ContextList();
        state_ = RequestStateUnsent;
        pollable_ = false;
        polling_ = false;

        ORB orb = (ORB) delegate_.orb(null);
        raiseDIIExceptions_ = orb._OB_raiseDIIExceptions();
    }

    protected void finalize() throws Throwable {
        if (state_ == RequestStateSent) {
            //
            // TODO: This is a memory leak, as a request has been
            // sent, but the response has never been picked
            // up. The correct thing would be to tell the Downcall
            // object to cancel the request. But we don't have
            // this ability yet.
            //
        }

        //
        // Find out whether this was a deferred request for which
        // get_response() hasn't been called yet.
        //
        org.apache.yoko.orb.OB.ORBInstance orbInstance = delegate_
                ._OB_ORBInstance();
        org.apache.yoko.orb.OB.MultiRequestSender multi = orbInstance
                .getMultiRequestSender();
        if (multi != null) // It might be possible that the
        // MultiRequestSender is already destroyed
        {
            //
            // Remove this request from the list of the outstanding
            // deferred requests
            //
            multi.removeDeferredRequest(this);
        }

        super.finalize();
    }

    public boolean _OB_completed() {
        synchronized (stateMutex_) {
            return state_ == RequestStateReceived;
        }
    }
}

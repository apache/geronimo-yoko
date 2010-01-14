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

import java.util.logging.Level;

public class Downcall {
    //
    // The ORBInstance object
    //
    protected ORBInstance orbInstance_;
    
    protected Logger logger_;   // the orbInstance_ logger object 

    //
    // The client
    //
    protected Client client_;

    //
    // The downcall emitter
    //
    protected DowncallEmitter emitter_;

    //
    // Information about the IOR profile
    //
    protected org.apache.yoko.orb.OCI.ProfileInfo profileInfo_;

    //
    // The list of policies
    //
    protected RefCountPolicyList policies_;

    //
    // The unique request ID
    //
    protected int reqId_;

    //
    // The name of the operation
    //
    protected String op_;

    //
    // Whether a response is expected
    //
    protected boolean responseExpected_;

    //
    // The marshalled headers and parameters
    //
    protected org.apache.yoko.orb.CORBA.OutputStream out_;

    //
    // Holds the results of the operation
    //
    protected org.apache.yoko.orb.CORBA.InputStream in_;

    //
    // The state of this invocation
    //
    protected static final int DowncallStateUnsent = 0;

    protected static final int DowncallStatePending = 1;

    protected static final int DowncallStateNoException = 2;

    protected static final int DowncallStateUserException = 3;

    protected static final int DowncallStateSystemException = 4;

    protected static final int DowncallStateFailureException = 5;

    protected static final int DowncallStateForward = 6;

    protected static final int DowncallStateForwardPerm = 7;

    protected int state_;

    protected java.lang.Object stateMonitor_;

    //
    // Holds the exception if state_ is DowncallStateUserException,
    // DowncallStateSystemException, or DowncallStateFailureException
    //
    protected Exception ex_;

    //
    // Holds the exception ID if state_ is DowncallStateUserException,
    // DowncallStateSystemException, or DowncallStateFailureException
    //
    // In Java, we need this member in Downcall, rather than in PIDowncall
    //
    protected String exId_;

    //
    // Holds the forward IOR if state_ is DowncallStateLocationForward
    // or DowncallLocationForwardPerm
    //
    protected org.omg.IOP.IOR forwardIOR_;

    //
    // The request and reply service contexts
    //
    protected java.util.Vector requestSCL_ = new java.util.Vector();

    protected java.util.Vector replySCL_ = new java.util.Vector();

    // ----------------------------------------------------------------------
    // Downcall private and protected member implementations
    // ----------------------------------------------------------------------

    //
    // Raise an exception if necessary
    //
    void checkForException() throws LocationForward, FailureException {
        switch (state_) {
        case DowncallStateUserException:
            //
            // Do not raise UserException in Java
            //
            // if(ex_ != null) // Only raise if a user exception has been set
            // throw ex_;
            break;

        case DowncallStateSystemException:
            Assert._OB_assert(ex_ != null);
            // update the stack trace to have the caller's stack rather than the 
            // receiver thread. 
            ex_.fillInStackTrace();    
            throw (org.omg.CORBA.SystemException) ex_;

        case DowncallStateFailureException:
            Assert._OB_assert(ex_ != null);
            throw new FailureException((org.omg.CORBA.SystemException) ex_);

        case DowncallStateForward:
            Assert._OB_assert(forwardIOR_ != null);
            throw new LocationForward(forwardIOR_, false);

        case DowncallStateForwardPerm:
            Assert._OB_assert(forwardIOR_ != null);
            throw new LocationForward(forwardIOR_, true);

        default:
            break;
        }
    }

    //
    // Java only
    //
    // Required for use by subclasses
    //
    protected final org.apache.yoko.orb.CORBA.OutputStream preMarshalBase()
            throws LocationForward, FailureException {
        org.apache.yoko.orb.CORBA.OutputStreamHolder out = new org.apache.yoko.orb.CORBA.OutputStreamHolder();
        emitter_ = client_.startDowncall(this, out);
        out_ = out.value;
        checkForException();
        return out_;
    }

    // ----------------------------------------------------------------------
    // Downcall public member implementations
    // ----------------------------------------------------------------------

    public Downcall(ORBInstance orbInstance, Client client,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp) {
        orbInstance_ = orbInstance;
        logger_ = orbInstance_.getLogger(); 
        client_ = client;
        profileInfo_ = profileInfo;
        policies_ = policies;
        op_ = op;
        responseExpected_ = resp;
        state_ = DowncallStateUnsent;
        stateMonitor_ = null;
        ex_ = null;

        //
        // Get the next request ID
        //
        reqId_ = client_.requestId();
        
        logger_.debug("Downcall created for operation " + op + " with id " + reqId_); 
    }

    public ORBInstance orbInstance() {
        return orbInstance_;
    }

    public Client client() {
        return client_;
    }

    public org.apache.yoko.orb.OCI.ProfileInfo profileInfo() {
        return profileInfo_;
    }

    public RefCountPolicyList policies() {
        return policies_;
    }

    public Exception excep() {
        return ex_;
    }

    public int requestId() {
        return reqId_;
    }

    public String operation() {
        return op_;
    }

    public boolean responseExpected() {
        return responseExpected_;
    }

    public org.apache.yoko.orb.CORBA.OutputStream output() {
        return out_;
    }

    public org.apache.yoko.orb.CORBA.InputStream input() {
        return in_;
    }

    public org.omg.IOP.ServiceContext[] getRequestSCL() {
        org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[requestSCL_
                .size()];
        requestSCL_.copyInto(scl);
        return scl;
    }

    public void addToRequestSCL(org.omg.IOP.ServiceContext sc) {
        requestSCL_.addElement(sc);
    }

    public void setReplySCL(org.omg.IOP.ServiceContext[] scl) {
        // Don't create a new Vector
        Assert._OB_assert(replySCL_.size() == 0);
        replySCL_.setSize(scl.length);
        for (int i = 0; i < scl.length; i++)
            replySCL_.setElementAt(scl[i], i);
    }

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal()
            throws LocationForward, FailureException {
        return preMarshalBase();
    }

    public void marshalEx(org.omg.CORBA.SystemException ex)
            throws LocationForward, FailureException {
        setFailureException(ex);
        checkForException();
        Assert._OB_assert(false);
    }

    public void postMarshal() throws LocationForward, FailureException {
    }

    public void locate() throws LocationForward, FailureException {
        Assert._OB_assert(responseExpected_);
        Assert._OB_assert(op_.equals("_locate"));

        //
        // We could also use send() and receive() separately. But
        // sendReceive is more efficient
        //

        /*
         * boolean finished = emitter_.send(this, true); if(finished)
         * checkForException();
         * 
         * finished = emitter_.receive(this, true); Assert._OB_assert(finished);
         * checkForException();
         */

        boolean finished = emitter_.sendReceive(this);
        Assert._OB_assert(finished);
        checkForException();
    }

    public void request() throws LocationForward, FailureException {
        Assert._OB_assert(responseExpected_);

        //
        // We could also use send() and receive() separately. But using
        // sendReceive() is more efficient.
        //
        /*
         * boolean finished = emitter_.send(this, true,
         * policies_.requestTimeout); if(finished) checkForException();
         * 
         * finished = emitter_.receive(this, true); Assert._OB_assert(finished);
         * checkForException();
         */

        boolean finished = emitter_.sendReceive(this);
        Assert._OB_assert(finished);
        checkForException();
    }

    public void oneway() throws LocationForward, FailureException {
        Assert._OB_assert(!responseExpected_);

        if (policies_.syncScope == org.omg.Messaging.SYNC_WITH_TRANSPORT.value) {
            boolean finished = emitter_.send(this, true);
            Assert._OB_assert(finished);
            checkForException();
        } else {
            boolean finished = emitter_.send(this, false);
            if (finished)
                checkForException();
        }
    }

    public void deferred() throws LocationForward, FailureException {
        Assert._OB_assert(responseExpected_);

        boolean finished = emitter_.send(this, true);
        if (finished)
            checkForException();
    }

    public void response() throws LocationForward, FailureException {
        Assert._OB_assert(responseExpected_);

        boolean finished = emitter_.receive(this, true);
        Assert._OB_assert(finished);
        checkForException();
    }

    public boolean poll() throws LocationForward, FailureException {
        Assert._OB_assert(responseExpected_);

        boolean finished = emitter_.receive(this, false);
        if (finished) {
            checkForException();
            return state_ != DowncallStatePending;
        } else
            return false;
    }

    public org.apache.yoko.orb.CORBA.InputStream preUnmarshal()
            throws LocationForward, FailureException {
        return in_;
    }

    public void unmarshalEx(org.omg.CORBA.SystemException ex)
            throws LocationForward, FailureException {
        setFailureException(ex);
        checkForException();
        Assert._OB_assert(false);
    }

    public void postUnmarshal() throws LocationForward, FailureException {
        //
        // If the result of this downcall is a user exception, but no user
        // exception could be unmarshalled, then use the system exception
        // UNKNOWN
        //
        // In Java, the portable stubs only provide the repository ID of
        // the user exception, so we only want to raise UNKNOWN if we
        // don't have the ID
        //
        if (state_ == DowncallStateUserException && ex_ == null
                && exId_ == null)
            setSystemException(new org.omg.CORBA.UNKNOWN(org.apache.yoko.orb.OB.MinorCodes
                    .describeUnknown(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException),
                    org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException,
                    org.omg.CORBA.CompletionStatus.COMPLETED_YES));

        checkForException();
    }

    public String unmarshalExceptionId() {
        Assert._OB_assert(state_ == DowncallStateUserException);
        int pos = in_._OB_pos();
        String id = in_.read_string();
        in_._OB_pos(pos);
        return id;
    }

    public boolean unsent() {
        return state_ == DowncallStateUnsent;
    }

    public boolean pending() {
        return state_ == DowncallStatePending;
    }

    public boolean noException() {
        return state_ == DowncallStateNoException;
    }

    public boolean userException() {
        return state_ == DowncallStateUserException;
    }

    public boolean failureException() {
        return state_ == DowncallStateFailureException;
    }

    public boolean systemException() {
        return state_ == DowncallStateSystemException;
    }

    private void setPendingImpl() {
        Assert._OB_assert(responseExpected_);
        state_ = DowncallStatePending;
    }

    public void setPending() {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setPendingImpl();
                stateMonitor_.notify();
            }
        } else
            setPendingImpl();
    }

    private void setNoExceptionImpl(org.apache.yoko.orb.CORBA.InputStream in) {
        state_ = DowncallStateNoException;
        if (in == null) {
            Assert._OB_assert(!responseExpected_);
        } else {
            Assert._OB_assert(responseExpected_);
            in_ = in;
            in_._OB_ORBInstance(orbInstance_);
            CodeConverters codeConverters = client_.codeConverters();
            in_._OB_codeConverters(codeConverters, (profileInfo_.major << 8)
                    | profileInfo_.minor);
        }
    }

    public void setNoException(org.apache.yoko.orb.CORBA.InputStream in) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setNoExceptionImpl(in);
                stateMonitor_.notify();
            }
        } else
            setNoExceptionImpl(in);
    }

    private void setUserExceptionImpl(org.apache.yoko.orb.CORBA.InputStream in) {
        Assert._OB_assert(in != null);
        Assert._OB_assert(responseExpected_);
        state_ = DowncallStateUserException;
        in_ = in;
        in_._OB_ORBInstance(orbInstance_);
        CodeConverters codeConverters = client_.codeConverters();
        in_._OB_codeConverters(codeConverters, (profileInfo_.major << 8)
                | profileInfo_.minor);
    }

    public void setUserException(org.apache.yoko.orb.CORBA.InputStream in) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setUserExceptionImpl(in);
                stateMonitor_.notify();
            }
        } else
            setUserExceptionImpl(in);
    }

    private void setUserExceptionImpl(org.omg.CORBA.UserException ex,
            String exId) {
        Assert._OB_assert(responseExpected_);
        Assert._OB_assert(ex_ == null);
        state_ = DowncallStateUserException;
        ex_ = ex;
    }

    public void setUserException(org.omg.CORBA.UserException ex, String exId) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setUserExceptionImpl(ex, exId);
                stateMonitor_.notify();
            }
        } else
            setUserExceptionImpl(ex, exId);
    }

    private void setUserExceptionImpl(org.omg.CORBA.UserException ex) {
        Assert._OB_assert(responseExpected_);
        Assert._OB_assert(ex_ == null);
        state_ = DowncallStateUserException;
        ex_ = ex;
    }

    public void setUserException(org.omg.CORBA.UserException ex) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setUserExceptionImpl(ex);
                stateMonitor_.notify();
            }
        } else
            setUserExceptionImpl(ex);
    }

    //
    // Java only
    //
    // Required for portable stubs, which do not make the UserException
    // instance available to the ORB
    //
    private void setUserExceptionImpl(String exId) {
        Assert._OB_assert(responseExpected_);
        Assert._OB_assert(ex_ == null);
        state_ = DowncallStateUserException;
        exId_ = exId;
        logger_.debug("Received user exception " + exId);
    }

    public void setUserException(String exId) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setUserExceptionImpl(exId);
                stateMonitor_.notify();
            }
        } else
            setUserExceptionImpl(exId);
    }

    private void setSystemExceptionImpl(org.omg.CORBA.SystemException ex) {
        Assert._OB_assert(responseExpected_);
        Assert._OB_assert(ex_ == null);
        state_ = DowncallStateSystemException;
        ex_ = ex;
        logger_.debug("Received system exception", ex);
    }

    public void setSystemException(org.omg.CORBA.SystemException ex) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setSystemExceptionImpl(ex);
                stateMonitor_.notify();
            }
        } else
            setSystemExceptionImpl(ex);
    }

    private void setFailureExceptionImpl(org.omg.CORBA.SystemException ex) {
        Assert._OB_assert(ex_ == null);
        state_ = DowncallStateFailureException;
        ex_ = ex;
        logger_.debug("Received failure exception", ex);
    }

    public void setFailureException(org.omg.CORBA.SystemException ex) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setFailureExceptionImpl(ex);
                stateMonitor_.notify();
            }
        } else
            setFailureExceptionImpl(ex);
    }

    private void setLocationForwardImpl(org.omg.IOP.IOR ior, boolean perm) {
        Assert._OB_assert(responseExpected_);
        Assert._OB_assert(forwardIOR_ == null);
        if (perm)
            state_ = DowncallStateForwardPerm;
        else
            state_ = DowncallStateForward;
        forwardIOR_ = ior;
    }

    public void setLocationForward(org.omg.IOP.IOR ior, boolean perm) {
        if (stateMonitor_ != null) {
            synchronized (stateMonitor_) {
                setLocationForwardImpl(ior, perm);
                stateMonitor_.notify();
            }
        } else
            setLocationForwardImpl(ior, perm);
    }

    //
    // Initialize the state monitor. This operation must be called in
    // order to be able to use one of the waitUntil...() operations
    // below
    //
    public void initStateMonitor() {
        Assert._OB_assert(stateMonitor_ == null);
        stateMonitor_ = new java.lang.Object();
    }

    //
    // This operation try waits for a specific state, using the
    // timeout from this downcall's policies. If the timeout expires,
    // a NO_RESPONSE exception is raised.
    //
    // - If the first parameter is set to false, the operation returns
    // immediately with false if the desired state cannot be
    // reached.
    //
    // - If the return value is true, it's safe to access or modify
    // the downcall object. If the return value if false, accessing
    // or modifying the downcall object is not allowed, for thread
    // safety reasons. (Because the downcall object is not thread
    // safe.)
    //
    public boolean waitUntilCompleted(boolean block) {
        //
        // Get the timeout
        //
        int t = policies_.requestTimeout;

        //
        // Yield if non-blocking or blocking with zero timeout
        //
        if (!block || (block && t == 0)) {
            Thread.yield();
        }

        //
        // Wait for the desired state, taking the timeout and blocking
        // flag into account
        //
        Assert._OB_assert(stateMonitor_ != null);
        synchronized (stateMonitor_) {
            while (state_ == DowncallStateUnsent
                    || state_ == DowncallStatePending) {
                if (!block) {
                    return false;
                }

                try {
                    if (t < 0) {
                        stateMonitor_.wait();
                    } else {
                        int oldState = state_;

                        stateMonitor_.wait(t);

                        if (state_ == oldState) {
                            throw new org.omg.CORBA.NO_RESPONSE(
                                    "Timeout during receive",
                                    0,
                                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
                        }
                    }
                } catch (InterruptedException ex) {
                }
            }

            //
            // The downcall has completed
            //
            return true;
        }
    }
}

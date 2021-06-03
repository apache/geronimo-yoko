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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.CORBA.OutputStreamHolder;
import org.apache.yoko.orb.IOP.MutableServiceContexts;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.apache.yoko.util.concurrent.AutoLock;
import org.apache.yoko.util.concurrent.AutoReadWriteLock;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UserException;
import org.omg.IOP.IOR;
import org.omg.IOP.ServiceContext;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;

import static org.apache.yoko.util.ObjectFormatter.format;

public class Downcall {
    /** The ORBInstance object */
    protected final ORBInstance orbInstance_;

    private final Logger logger_;   // the orbInstance_ logger object

    /** The client */
    private final Client client_;

    /** The downcall emitter */
    private DowncallEmitter emitter_;

    /** Information about the IOR profile */
    protected ProfileInfo profileInfo_;

    /** The list of policies */
    protected RefCountPolicyList policies_;

    /** The unique request ID */
    private final int reqId_;

    /** The name of the operation */
    protected final String op_;

    /** Whether a response is expected */
    protected final boolean responseExpected_;

    /** The marshalled headers and parameters */
    private OutputStream out_;

    /** Holds the results of the operation */
    private InputStream in_;

    /** The state of this invocation */
    protected enum State { UNSENT, PENDING, NO_EXCEPTION, USER_EXCEPTION, SYSTEM_EXCEPTION, FAILURE_EXCEPTION, STALE_CONNECTION, FORWARD, FORWARD_PERM }
    
    protected final AutoReadWriteLock stateLock = new AutoReadWriteLock();
    
    protected State state = State.UNSENT;

    private Condition stateWaitCondition;

    //
    // Holds the exception if state_ is DowncallStateUserException,
    // DowncallStateSystemException, or DowncallStateFailureException
    //
    protected Exception ex_;

    //
    // Holds the exception ID if state_ is DowncallStateUserException,
    // DowncallStateSystemException, or DowncallStateFailureException
    //
    protected String exId_;

    //
    // Holds the forward IOR if state_ is DowncallStateLocationForward
    // or DowncallLocationForwardPerm
    //
    protected IOR forwardIOR_;

    /** The request and reply service contexts */
    public final ServiceContexts requestContexts = new ServiceContexts();
    public final ServiceContexts replyContexts = new ServiceContexts();

    // ----------------------------------------------------------------------
    // Downcall private and protected member implementations
    // ----------------------------------------------------------------------

    /** Raise an exception if necessary */
    void checkForException() throws LocationForward, FailureException {
        try (AutoLock readLock = stateLock.getReadLock()) {
            switch (state) {
            case USER_EXCEPTION:
                //
                // Do not raise UserException in Java
                //
                // if(ex_ != null) // Only raise if a user exception has been set
                // throw ex_;
                break;

            case SYSTEM_EXCEPTION:
                Assert.ensure(ex_ != null);
                // update the stack trace to have the caller's stack rather than the
                // receiver thread.
                ex_.fillInStackTrace();
                throw (SystemException) ex_;

            case FAILURE_EXCEPTION:
                Assert.ensure(ex_ != null);
                throw new FailureException((SystemException) ex_);

            case STALE_CONNECTION:
                Assert.ensure(ex_ != null);
                throw new FailureException((SystemException) ex_, false);

            case FORWARD:
                Assert.ensure(forwardIOR_ != null);
                throw new LocationForward(forwardIOR_, false);

            case FORWARD_PERM:
                Assert.ensure(forwardIOR_ != null);
                throw new LocationForward(forwardIOR_, true);

            default:
                break;
            }
        }
    }

    /** Required for use by subclasses */
    protected final OutputStream preMarshalBase() throws LocationForward, FailureException {
        OutputStreamHolder out = new OutputStreamHolder();
        emitter_ = client_.startDowncall(this, out);
        out_ = out.value;
        checkForException();
        return out_;
    }

    // ----------------------------------------------------------------------
    // Downcall public member implementations
    // ----------------------------------------------------------------------

    public Downcall(ORBInstance orbInstance, Client client,
            ProfileInfo profileInfo,
            RefCountPolicyList policies, String op, boolean resp) {
        orbInstance_ = orbInstance;
        logger_ = orbInstance_.getLogger(); 
        client_ = client;
        profileInfo_ = profileInfo;
        policies_ = policies;
        op_ = op;
        responseExpected_ = resp;
        // since this.state is not volatile we must use a lock to guarantee consistency
        try (AutoLock writeLock = stateLock.getWriteLock()) {
            state = State.UNSENT;
        }
        ex_ = null;

        //
        // Get the next request ID
        //
        client.prepareForDowncall(policies);
        reqId_ = client_.getNewRequestID();
        
        logger_.fine("Downcall created for operation " + op + " with id " + reqId_);
    }

    public final ORBInstance orbInstance() {
        return orbInstance_;
    }

    public final Client client() {
        return client_;
    }

    public final ProfileInfo profileInfo() {
        return profileInfo_;
    }

    public final RefCountPolicyList policies() {
        return policies_;
    }

    public final Exception excep() {
        return ex_;
    }

    public final int requestId() {
        return reqId_;
    }

    public final String operation() {
        return op_;
    }

    public final boolean responseExpected() {
        return responseExpected_;
    }

    public final OutputStream output() {
        return out_;
    }

    public final InputStream input() {
        return in_;
    }

    public final ServiceContexts getRequestContexts() {
        return requestContexts;
    }

    public final void addToRequestContexts(ServiceContext sc) {
        requestContexts.mutable().add(sc);
    }

    public final void setReplyContexts(ServiceContexts contexts) {
        if (!replyContexts.isEmpty() && logger_.isDebugEnabled()) {
            logger_.fine("Expected empty reply contexts, but found " + replyContexts.size());
            for (ServiceContext sc : contexts) logger_.fine("\t" + format(sc));
        }
        final MutableServiceContexts mutable = replyContexts.mutable();
        for (ServiceContext sc: contexts) mutable.add(sc, true);
    }

    public OutputStream preMarshal() throws LocationForward, FailureException {
        return preMarshalBase();
    }

    public final void marshalEx(SystemException ex) throws LocationForward, FailureException {
        setFailureException(ex);
        checkForException();
        throw Assert.fail();
    }

    public final void postMarshal() throws LocationForward, FailureException {
    }

    public final void locate() throws LocationForward, FailureException {
        Assert.ensure(responseExpected_);
        Assert.ensure(op_.equals("_locate"));

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
        Assert.ensure(finished);
        checkForException();
    }

    public final void request() throws LocationForward, FailureException {
        Assert.ensure(responseExpected_);

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
        Assert.ensure(finished);
        checkForException();
    }

    public final void oneway() throws LocationForward, FailureException {
        Assert.ensure(!responseExpected_);

        if (policies_.syncScope == SYNC_WITH_TRANSPORT.value) {
            boolean finished = emitter_.send(this, true);
            Assert.ensure(finished);
            checkForException();
        } else {
            boolean finished = emitter_.send(this, false);
            if (finished)
                checkForException();
        }
    }

    public final void deferred() throws LocationForward, FailureException {
        Assert.ensure(responseExpected_);

        boolean finished = emitter_.send(this, true);
        if (finished)
            checkForException();
    }

    public final void response() throws LocationForward, FailureException {
        Assert.ensure(responseExpected_);

        boolean finished = emitter_.receive(this, true);
        Assert.ensure(finished);
        checkForException();
    }

    public final boolean poll() throws LocationForward, FailureException {
        Assert.ensure(responseExpected_);

        boolean finished = emitter_.receive(this, false);
        if (finished) {
            try (AutoLock lock = stateLock.getReadLock()) {
                checkForException();
                return state != State.PENDING;
            }
        } else
            return false;
    }

    public final InputStream preUnmarshal()
            throws LocationForward, FailureException {
        return in_;
    }

    public final void unmarshalEx(SystemException ex)
            throws LocationForward, FailureException {
        setFailureException(ex);
        checkForException();
        throw Assert.fail();
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
        try (AutoLock lock = stateLock.getReadLock()) {
            if (state == State.USER_EXCEPTION && ex_ == null && exId_ == null)
                setSystemException(new UNKNOWN(MinorCodes
                        .describeUnknown(MinorCodes.MinorUnknownUserException),
                        MinorCodes.MinorUnknownUserException,
                        CompletionStatus.COMPLETED_YES));
            checkForException();
        }
    }

    public final String unmarshalExceptionId() {
        try (AutoLock lock = stateLock.getReadLock()) {
            Assert.ensure(state == State.USER_EXCEPTION);
            int pos = in_.getPosition();
            String id = in_.read_string();
            in_.setPosition(pos);
            return id;
        }
    }

    public final boolean unsent() {
        try (AutoLock lock = stateLock.getReadLock()) {
            return state == State.UNSENT;
        }
    }

    public final boolean pending() {
        try (AutoLock lock = stateLock.getReadLock()) {
            return state == State.PENDING;
        }
    }

    public final boolean noException() {
        try (AutoLock lock = stateLock.getReadLock()) {
            return state == State.NO_EXCEPTION;
        }
    }

    public final boolean userException() {
        try (AutoLock lock = stateLock.getReadLock()) {
            return state == State.USER_EXCEPTION;
        }
    }

    public final void setPending() {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(responseExpected_);
            state = State.PENDING;
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    public final void setNoException(InputStream in) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            state = State.NO_EXCEPTION;
            if (in == null) {
                Assert.ensure(!responseExpected_);
            } else {
                Assert.ensure(responseExpected_);
                in_ = in;
                in_._OB_ORBInstance(orbInstance_);
                CodeConverters codeConverters = client_.codeConverters();
                in_._OB_codeConverters(codeConverters, GiopVersion.get(profileInfo_.major, profileInfo_.minor));
            }
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    public final void setUserException(InputStream in) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(in != null);
            Assert.ensure(responseExpected_);
            state = State.USER_EXCEPTION;
            in_ = in;
            in_._OB_ORBInstance(orbInstance_);
            CodeConverters codeConverters = client_.codeConverters();
            in_._OB_codeConverters(codeConverters, GiopVersion.get(profileInfo_.major, profileInfo_.minor));
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    public void setUserException(UserException ex, String exId) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(responseExpected_);
            Assert.ensure(ex_ == null);
            state = State.USER_EXCEPTION;
            ex_ = ex;
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    public final void setUserException(UserException ex) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(responseExpected_);
            Assert.ensure(ex_ == null);
            state = State.USER_EXCEPTION;
            ex_ = ex;
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    public final void setUserException(String exId) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(responseExpected_);
            Assert.ensure(ex_ == null);
            state = State.USER_EXCEPTION;
            exId_ = exId;
            logger_.fine("Received user exception " + exId);
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    public final void setSystemException(SystemException ex) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(responseExpected_);
            Assert.ensure(ex_ == null);
            state = State.SYSTEM_EXCEPTION;
            ex_ = ex;
            logger_.log(Level.FINE, "Received system exception", ex);
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    final void notifyStaleConnection() {
        try (AutoLock lock = stateLock.getWriteLock()) {
            state = State.STALE_CONNECTION;
        }
    }

    public final void setFailureException(SystemException ex) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(ex_ == null);
            if (state != State.STALE_CONNECTION) state = State.FAILURE_EXCEPTION;
            ex_ = ex;
            logger_.log(Level.FINE, "Received failure exception", ex);
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
    }

    public final void setLocationForward(IOR ior, boolean perm) {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(responseExpected_);
            Assert.ensure(forwardIOR_ == null);
            state = perm ? State.FORWARD_PERM : State.FORWARD;
            forwardIOR_ = ior;
            if (null != stateWaitCondition) stateWaitCondition.signalAll();
        }
        Assert.ensure(responseExpected_);
    }

    //
    // Initialize the wait condition. This operation must be called in
    // order to be able to use one of the waitUntil...() operations
    // below
    //
    public final void allowWaiting() {
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(stateWaitCondition == null);
            stateWaitCondition = lock.newCondition();
        }
    }

    /**
     * This operation try waits for a completed state, using the
     * timeout from this downcall's policies.
     *
     * @param block whether to wait for the call to complete
     * @return true if the call has completed
     * @throws NO_RESPONSE if a timeout was set and has elapsed
     */
    public final boolean waitUntilCompleted(boolean block) {
        //
        // Get the timeout
        //
        int t = policies_.requestTimeout;

        //
        // Wait for the desired state, taking the timeout and blocking
        // flag into account
        //
        try (AutoLock lock = stateLock.getWriteLock()) {
            Assert.ensure(stateWaitCondition != null);
            while (state == State.UNSENT || state == State.PENDING) {
                if (!block) return false;

                try {
                    if (t <= 0) {
                        stateWaitCondition.await();
                    } else {
                        State oldState = state;

                        stateWaitCondition.await(t, TimeUnit.MILLISECONDS);

                        if (state == oldState) {
                            throw new NO_RESPONSE("Timeout during receive", 0, CompletionStatus.COMPLETED_MAYBE);
                        }
                    }
                } catch (InterruptedException ex) {
                }
            }
            if (ex_ instanceof UnresolvedException) ex_ = ((UnresolvedException)ex_).resolve();
            //
            // The downcall has completed
            //
            return true;
        }
    }

    @Override
    public String toString() {
        return String.format("%s[ reqId_=%d op_=%s state=%s]", this.getClass().getSimpleName(), reqId_, op_, state);
    }
}

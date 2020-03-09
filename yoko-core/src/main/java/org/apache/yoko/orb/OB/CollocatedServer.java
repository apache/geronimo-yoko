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
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UserException;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHolder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

final public class CollocatedServer extends Server implements UpcallReturn {
    private static final Logger logger = Logger.getLogger(CollocatedServer.class.getName());
    //
    // The next request ID and the corresponding mutex
    //
    private int nextRequestId_;

    private final Object nextRequestIdMutex_ = new Object();

    //
    // The call map
    //
    private final Hashtable callMap_;

    //
    // True if destroy() was called
    //
    private boolean destroy_;

    //
    // True if holding
    //
    private boolean hold_;

    //
    // The object adapter interface
    //
    private final OAInterface oaInterface_;

    // ----------------------------------------------------------------------
    // CollocatedServer private and protected member implementations
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // CollocatedServer package member implementations
    // ----------------------------------------------------------------------

    public CollocatedServer(OAInterface oaInterface, int concModel) {
        super(concModel);
        nextRequestId_ = 0;
        callMap_ = new Hashtable(13);
        destroy_ = false;
        hold_ = true;
        oaInterface_ = oaInterface;
    }

    // ----------------------------------------------------------------------
    // CollocatedClient public member implementations
    // ----------------------------------------------------------------------

    //
    // Destroy the server
    //
    public synchronized void destroy() {
        //
        // Don't destroy twice
        //
        if (destroy_)
            return;

        //
        // Set the destroy flag
        //
        destroy_ = true;

        //
        // Set the status of all downcalls, and empty the call map
        //
        Enumeration e = callMap_.keys();
        while (e.hasMoreElements()) {
            Downcall down = (Downcall) callMap_.get(e.nextElement());
            Assert.ensure(down != null);
            Assert.ensure(down.pending());
            down.setFailureException(new INITIALIZE(
                    "ORB has been destroyed", MinorCodes.MinorORBDestroyed,
                    CompletionStatus.COMPLETED_NO));
        }
        callMap_.clear();

        //
        // Notify everyone of the state transition
        //
        notifyAll();
    }

    //
    // Hold any new requests that arrive for the Server
    //
    public synchronized void hold() {
        Assert.ensure(!destroy_);
        logger.fine("Collocated server placed in hold state"); 
        hold_ = true;

        //
        // Notify everyone of the state transition
        //
        notifyAll();
    }

    //
    // Dispatch any requests that arrive for the Server
    //
    public synchronized void activate() {
        Assert.ensure(!destroy_);
        logger.fine("Collocated server activated"); 
        hold_ = false;

        //
        // Notify everyone of the state transition
        //
        notifyAll();
    }

    //
    // Called to emit downcalls
    //
    public boolean send(Downcall down, boolean b) {
        logger.fine("Sending a request"); 
        //
        // We need a state monitor if the request is dispatched in a
        // separate thread.
        //
        // TODO: If we had a method to query for whether a separate
        // dispatch thread is used, we could avoid initializing the
        // state monitor when it is not needed.
        //
        down.allowWaiting();

        Upcall up;

        synchronized (this) {
            //
            // First check whether we're destroyed or on hold
            //
            while (hold_ && !destroy_) {
                try {
                    logger.fine("Waiting for hold to be released"); 
                    wait();
                } catch (InterruptedException ignored) {
                }
            }

            if (destroy_) {
                down.setFailureException(new TRANSIENT(
                        "Collocated server has already been destroyed", 0,
                        CompletionStatus.COMPLETED_NO));
                return true;
            }

            //
            // Collect the Upcall data
            //
            ProfileInfo profileInfo = down.profileInfo();
            int reqId = down.requestId();
            String op = down.operation();
            OutputStream out = down.output();
            ServiceContexts requestContexts = down.getRequestContexts();

            //
            // Is this a locate request?
            //
            if (op.charAt(0) == '_' && op.equals("_locate")) {
                IORHolder ior = new IORHolder();
                switch (oaInterface_.findByKey(profileInfo.key, ior)) {
                case OAInterface.UNKNOWN_OBJECT:
                    down.setSystemException(new OBJECT_NOT_EXIST());
                    break;

                case OAInterface.OBJECT_HERE:
                    InputStream in = new InputStream(out.getBufferReader());
                    down.setNoException(in);
                    break;

                case OAInterface.OBJECT_FORWARD:
                    down.setLocationForward(ior.value, false);
                    break;

                case OAInterface.OBJECT_FORWARD_PERM:
                    down.setLocationForward(ior.value, true);
                    break;

                default:
                    throw Assert.fail();
                }

                return true;
            }

            if (down.responseExpected()) {
                //
                // Put the Downcall in the call map
                //
                callMap_.put(reqId, down);

                //
                // From here on, we consider the Downcall as pending
                //
                down.setPending();

                up = oaInterface_.createUpcall(this, profileInfo, null, reqId,
                        op, new InputStream(out.getBufferReader()), requestContexts);
            } else {
                //
                // This is a oneway call, and if there was no exception so
                // far, everything is fine
                //
                down.setNoException(null);

                up = oaInterface_.createUpcall(null, profileInfo, null, reqId,
                        op, new InputStream(out.getBufferReader()), requestContexts);
            }
        }

        //
        // Invoke the upcall
        //
        // Note: This must be done *outside* the synchronization, so that
        // nested method invocations are possible.
        //
        up.invoke();

        return !down.responseExpected();
    }

    public boolean receive(Downcall down, boolean block) {
        logger.fine("Receiving a request"); 
        //
        // Try to receive the reply
        //
        try {
            //
            // TODO: If we had a method to query for whether a
            // separate dispatch thread is used, we could avoid
            // calling waitUntilCompleted() if no such separate thread
            // is used, and query the state directly instead.
            //
            return down.waitUntilCompleted(block);
        } catch (SystemException ex) {
            synchronized (this) {
                callMap_.remove(down.requestId());
                down.setFailureException(ex);
                return true;
            }
        }
    }

    //
    // Send and receive downcalls with one operation (for efficiency
    // reasons)
    //
    public boolean sendReceive(Downcall down) {
        send(down, true);
        return receive(down, true);
    }

    //
    // Get a new request ID
    //
    public int requestId() {
        synchronized (nextRequestIdMutex_) {
            return nextRequestId_++;
        }
    }

    //
    // Get the usable profiles
    //
    public ProfileInfo[] getUsableProfiles(
            IOR ior, Policy[] policies) {
        return oaInterface_.getUsableProfiles(ior, policies);
    }

    public void upcallBeginReply(Upcall upcall, ServiceContexts replyContexts) {
        upcall.createOutputStream(0);
        if (replyContexts.isEmpty()) return;
        synchronized (this) {
            Downcall down = (Downcall) callMap_.get(upcall.requestId());

            //
            // Might be null if the request timed out or destroyed
            //
            if (down != null) down.setReplyContexts(replyContexts);
        }
    }

    public synchronized void upcallEndReply(Upcall upcall) {
        Downcall down = (Downcall) callMap_.get(upcall.requestId());
        if (down == null) return ; // Might be null if the request timed out
        OutputStream out = upcall.output();
        InputStream in = new InputStream(out.getBufferReader());
        down.setNoException(in);
        callMap_.remove(down.requestId());
    }

    public void upcallBeginUserException(Upcall upcall, ServiceContexts replyContexts) {
        upcall.createOutputStream(0);
        if (replyContexts.isEmpty()) return;
        synchronized (this) {
            Downcall down = (Downcall) callMap_.get(upcall.requestId());
            // Might be null if the request timed out or destroyed
            if (down != null) down.setReplyContexts(replyContexts);
        }
    }

    public synchronized void upcallEndUserException(Upcall upcall) {
        Downcall down = (Downcall) callMap_.get(upcall.requestId());

        // Might be null if the request timed out or destroyed
        if (down == null) return;
        OutputStream out = upcall.output();
        InputStream in = new InputStream(out.getBufferReader());
        down.setUserException(in);
        callMap_.remove(down.requestId());
    }

    //
    // NOTE: Not used in Java
    //
    public void upcallUserException(Upcall upcall, UserException ex, ServiceContexts replyContexts) {
        //
        // We marshal to preserve 100% location transparency. If we would
        // set the exception in the Downcall directly, then
        // we wouldn't get an UNKNOWN exception if we're calling from the
        // DII without setting the exception TypeCode.
        //

        upcallBeginUserException(upcall, replyContexts);
        OutputStream out = upcall.output();
        try {
            throw Assert.fail("Cannot marshal the exception in Java without the helper");
        } catch (SystemException e) {
            upcall.marshalEx(e);
        }
        upcallEndUserException(upcall);
    }

    public synchronized void upcallSystemException(Upcall upcall, SystemException ex, ServiceContexts replyContexts) {
        Downcall down = (Downcall) callMap_.get(upcall.requestId());
        if (down == null) return; // Might be null if the request timed out or destroyed
        down.setReplyContexts(replyContexts);
        down.setSystemException(ex);
        callMap_.remove(down.requestId());
    }

    public synchronized void upcallForward(Upcall upcall, IOR ior, boolean perm, ServiceContexts replyContexts) {
        Downcall down = (Downcall) callMap_.get(upcall.requestId());

        //
        // Might be null if the request timed out or destroyed
        //
        if (down != null) {
            down.setReplyContexts(replyContexts);
            down.setLocationForward(ior, perm);
            callMap_.remove(down.requestId());
        }
    }

    /**
     * no need to send code set and code base service contexts to ourselves
     * @return
     */
    public boolean replySent() {
        return true;
    }
}

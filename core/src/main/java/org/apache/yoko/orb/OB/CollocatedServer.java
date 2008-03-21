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
import java.util.logging.Logger;

final public class CollocatedServer extends Server implements UpcallReturn {
    static final Logger logger = Logger.getLogger(CollocatedServer.class.getName());
    //
    // The next request ID and the corresponding mutex
    //
    private int nextRequestId_;

    private java.lang.Object nextRequestIdMutex_ = new java.lang.Object();

    //
    // The call map
    //
    private java.util.Hashtable callMap_;

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
    private OAInterface oaInterface_;

    // ----------------------------------------------------------------------
    // CollocatedServer private and protected member implementations
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // CollocatedServer package member implementations
    // ----------------------------------------------------------------------

    public CollocatedServer(OAInterface oaInterface, int concModel) {
        super(concModel);
        nextRequestId_ = 0;
        callMap_ = new java.util.Hashtable(13);
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
        java.util.Enumeration e = callMap_.keys();
        while (e.hasMoreElements()) {
            Downcall down = (Downcall) callMap_.get(e.nextElement());
            Assert._OB_assert(down != null);
            Assert._OB_assert(down.pending());
            down.setFailureException(new org.omg.CORBA.INITIALIZE(
                    "ORB has been destroyed", org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO));
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
        Assert._OB_assert(!destroy_);
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
        Assert._OB_assert(!destroy_);
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
        down.initStateMonitor();

        Upcall up;

        synchronized (this) {
            //
            // First check whether we're destroyed or on hold
            //
            while (hold_ && !destroy_) {
                try {
                    logger.fine("Waiting for hold to be released"); 
                    wait();
                } catch (InterruptedException ex) {
                }
            }

            if (destroy_) {
                down.setFailureException(new org.omg.CORBA.TRANSIENT(
                        "Collocated server has already been destroyed", 0,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO));
                return true;
            }

            //
            // Collect the Upcall data
            //
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo = down.profileInfo();
            int reqId = down.requestId();
            String op = down.operation();
            org.apache.yoko.orb.CORBA.OutputStream out = down.output();
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            buf.consume(out._OB_buffer());
            org.omg.IOP.ServiceContext[] requestSCL = down.getRequestSCL();

            //
            // Is this a locate request?
            //
            if (op.charAt(0) == '_' && op.equals("_locate")) {
                org.omg.IOP.IORHolder ior = new org.omg.IOP.IORHolder();
                switch (oaInterface_.findByKey(profileInfo.key, ior)) {
                case org.apache.yoko.orb.OB.OAInterface.UNKNOWN_OBJECT:
                    down
                            .setSystemException(new org.omg.CORBA.OBJECT_NOT_EXIST());
                    break;

                case org.apache.yoko.orb.OB.OAInterface.OBJECT_HERE:
                    org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                            buf, 0, false);
                    down.setNoException(in);
                    break;

                case org.apache.yoko.orb.OB.OAInterface.OBJECT_FORWARD:
                    down.setLocationForward(ior.value, false);
                    break;

                case org.apache.yoko.orb.OB.OAInterface.OBJECT_FORWARD_PERM:
                    down.setLocationForward(ior.value, true);
                    break;

                default:
                    Assert._OB_assert(false);
                }

                return true;
            }

            if (down.responseExpected()) {
                //
                // Put the Downcall in the call map
                //
                callMap_.put(new Integer(reqId), down);

                //
                // From here on, we consider the Downcall as pending
                //
                down.setPending();

                up = oaInterface_.createUpcall(this, profileInfo, null, reqId,
                        op, new org.apache.yoko.orb.CORBA.InputStream(buf, 0,
                                false), requestSCL);
            } else {
                //
                // This is a oneway call, and if there was no exception so
                // far, everything is fine
                //
                down.setNoException(null);

                up = oaInterface_.createUpcall(null, profileInfo, null, reqId,
                        op, new org.apache.yoko.orb.CORBA.InputStream(buf, 0,
                                false), requestSCL);
            }
        }

        //
        // Invoke the upcall
        //
        // Note: This must be done *outside* the synchronization, so that
        // nested method invocations are possible.
        //
        up.invoke();

        if (down.responseExpected())
            return false;
        else
            return true;
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
        } catch (org.omg.CORBA.SystemException ex) {
            synchronized (this) {
                callMap_.remove(new Integer(down.requestId()));
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
    public org.apache.yoko.orb.OCI.ProfileInfo[] getUsableProfiles(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies) {
        return oaInterface_.getUsableProfiles(ior, policies);
    }

    public void upcallBeginReply(Upcall upcall,
            org.omg.IOP.ServiceContext[] replySCL) {
        upcall.createOutputStream(0);

        if (replySCL.length > 0) {
            synchronized (this) {
                Downcall down = (Downcall) callMap_.get(new Integer(upcall
                        .requestId()));

                //
                // Might be null if the request timed out or destroyed
                //
                if (down != null)
                    down.setReplySCL(replySCL);
            }
        }
    }

    public synchronized void upcallEndReply(Upcall upcall) {
        Downcall down = (Downcall) callMap_
                .get(new Integer(upcall.requestId()));

        //
        // Might be null if the request timed out or destroyed
        //
        if (down != null) // Might be null if the request timed out
        {
            org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            buf.consume(out._OB_buffer());
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            down.setNoException(in);
            callMap_.remove(new Integer(down.requestId()));
        }
    }

    public void upcallBeginUserException(Upcall upcall,
            org.omg.IOP.ServiceContext[] replySCL) {
        upcall.createOutputStream(0);

        if (replySCL.length > 0) {
            synchronized (this) {
                Downcall down = (Downcall) callMap_.get(new Integer(upcall
                        .requestId()));

                //
                // Might be null if the request timed out or destroyed
                //
                if (down != null)
                    down.setReplySCL(replySCL);
            }
        }
    }

    public synchronized void upcallEndUserException(Upcall upcall) {
        Downcall down = (Downcall) callMap_
                .get(new Integer(upcall.requestId()));

        //
        // Might be null if the request timed out or destroyed
        //
        if (down != null) {
            org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            buf.consume(out._OB_buffer());
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            down.setUserException(in);
            callMap_.remove(new Integer(down.requestId()));
        }
    }

    //
    // NOTE: Not used in Java
    //
    public void upcallUserException(Upcall upcall,
            org.omg.CORBA.UserException ex,
            org.omg.IOP.ServiceContext[] replySCL) {
        //
        // We marshal to preserve 100% location transparency. If we would
        // set the exception in the Downcall directly as shown below, then
        // we wouldn't get an UNKNOWN exception if we're calling from the
        // DII without setting the exception TypeCode.
        //

        /*
         * synchronized(this) { Downcall down = (Downcall)callMap_.get(new
         * Integer(upcall.requestId()));
         *  // // Might be null if the request timed out or destroyed // if(down !=
         * null) { if(replySCL.length > 0) down.setReplySCL(replySCL);
         * down.setUserException(ex); callMap_.remove(new
         * Integer(down.requestId()); } }
         */

        upcallBeginUserException(upcall, replySCL);
        org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
        try {
            //
            // Cannot marshal the exception in Java without the helper
            //
            // ex._OB_marshal(out);
            Assert._OB_assert(false);
        } catch (org.omg.CORBA.SystemException e) {
            try {
                upcall.marshalEx(e);
            } catch (LocationForward f) {
                Assert._OB_assert(ex); // shouldn't happen
            }
        }
        upcallEndUserException(upcall);
    }

    public synchronized void upcallSystemException(Upcall upcall,
            org.omg.CORBA.SystemException ex,
            org.omg.IOP.ServiceContext[] replySCL) {
        Downcall down = (Downcall) callMap_
                .get(new Integer(upcall.requestId()));

        //
        // Might be null if the request timed out or destroyed
        //
        if (down != null) {
            if (replySCL.length > 0)
                down.setReplySCL(replySCL);
            down.setSystemException(ex);
            callMap_.remove(new Integer(down.requestId()));
        }
    }

    public synchronized void upcallForward(Upcall upcall, org.omg.IOP.IOR ior,
            boolean perm, org.omg.IOP.ServiceContext[] replySCL) {
        Downcall down = (Downcall) callMap_
                .get(new Integer(upcall.requestId()));

        //
        // Might be null if the request timed out or destroyed
        //
        if (down != null) {
            if (replySCL.length > 0)
                down.setReplySCL(replySCL);
            down.setLocationForward(ior, perm);
            callMap_.remove(new Integer(down.requestId()));
        }
    }
}

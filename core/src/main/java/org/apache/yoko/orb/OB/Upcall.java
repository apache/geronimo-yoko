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

import org.apache.yoko.orb.OB.DispatchRequest;
import org.apache.yoko.orb.OB.DispatchStrategy;

public class Upcall {
    static final Logger logger = Logger.getLogger(Upcall.class.getName());
    //
    // The ORBInstance object
    //
    protected ORBInstance orbInstance_;

    //
    // Upcall delegates to UpcallReturn upon return from the
    // upcall. If this is nil, then no response is expected (i.e.,
    // this is a oneway call).
    //
    protected UpcallReturn upcallReturn_;

    //
    // Information about the IOR profile
    //
    protected org.apache.yoko.orb.OCI.ProfileInfo profileInfo_;

    //
    // The OCI transport info object
    //
    protected org.apache.yoko.orb.OCI.TransportInfo transportInfo_;

    //
    // The unique request ID
    //
    protected int reqId_;

    //
    // The name of the operation
    //
    protected String op_;

    //
    // Holds the inout/out parameters and return value
    //
    protected org.apache.yoko.orb.CORBA.OutputStream out_;

    //
    // Holds the in/inout parameters
    //
    protected org.apache.yoko.orb.CORBA.InputStream in_;

    //
    // The request service context list
    //
    protected org.omg.IOP.ServiceContext[] requestSCL_;

    //
    // The reply service context list
    // (Must be a Vector because it can be modified by interceptors)
    //
    protected java.util.Vector replySCL_ = new java.util.Vector();

    //
    // The dispatch request
    //
    protected DispatchRequest dispatchRequest_;

    //
    // Dispatch strategy
    //
    protected DispatchStrategy dispatchStrategy_;

    //
    // The servant and POA
    //
    protected org.omg.PortableServer.Servant servant_;

    protected org.apache.yoko.orb.OBPortableServer.POA_impl poa_;

    //
    // Whether postinvoke() has been called
    //
    protected boolean postinvokeCalled_;

    //
    // Java only
    //
    // Whether beginUserException has been called. We must delay the
    // call to endUserException until we are sure nothing else will
    // be marshalled (e.g., in case a SystemException occurs after
    // marshalling). This flag is set to true in beginUserException.
    // Only a SystemException or LocationForward can occur after
    // a UserException, so the flag is reset to false in
    // setSystemException and setLocationForward.
    //
    protected boolean userEx_;

    // ----------------------------------------------------------------------
    // Upcall public member implementations
    // ----------------------------------------------------------------------

    public Upcall(ORBInstance orbInstance, UpcallReturn upcallReturn,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.apache.yoko.orb.OCI.TransportInfo transportInfo, int requestId,
            String op, org.apache.yoko.orb.CORBA.InputStream in,
            org.omg.IOP.ServiceContext[] requestSCL) {
        orbInstance_ = orbInstance;
        upcallReturn_ = upcallReturn;
        profileInfo_ = profileInfo;
        transportInfo_ = transportInfo;
        reqId_ = requestId;
        op_ = op;
        in_ = in;
        requestSCL_ = requestSCL;
        servant_ = null;
        poa_ = null;
        postinvokeCalled_ = false;

        userEx_ = false; // Java only

        logger.fine("Creating upcall request for operation " + op + " and request id " + requestId); 
        in._OB_ORBInstance(orbInstance_);
    }

    public ORBInstance orbInstance() {
        return orbInstance_;
    }

    public org.apache.yoko.orb.OCI.ProfileInfo profileInfo() {
        return profileInfo_;
    }

    public org.apache.yoko.orb.OCI.TransportInfo transportInfo() {
        return transportInfo_;
    }

    public int requestId() {
        return reqId_;
    }

    public String operation() {
        return op_;
    }

    public boolean responseExpected() {
        return upcallReturn_ != null;
    }

    public boolean postinvokeCalled() {
        return postinvokeCalled_;
    }

    public org.apache.yoko.orb.CORBA.OutputStream output() {
        return out_;
    }

    public org.apache.yoko.orb.CORBA.InputStream input() {
        return in_;
    }

    public void createOutputStream(int offset) {
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                offset);
        buf.pos(offset);
        out_ = new org.apache.yoko.orb.CORBA.OutputStream(buf, in_
                ._OB_codeConverters(), (profileInfo_.major << 8)
                | profileInfo_.minor);
    }

    public org.apache.yoko.orb.CORBA.InputStream preUnmarshal()
            throws LocationForward {
        return in_;
    }

    public void unmarshalEx(org.omg.CORBA.SystemException ex)
            throws LocationForward {
        throw ex;
    }

    public void postUnmarshal() throws LocationForward {
    }

    public void postinvoke() throws LocationForward {
        if (servant_ != null) {
            Assert._OB_assert(poa_ != null && !postinvokeCalled_);
            servant_ = null;
            postinvokeCalled_ = true;
            poa_._OB_postinvoke(); // May raise SystemException
        }
    }

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal()
            throws LocationForward {
        //
        // If we have an UpcallReturn object, then invoking upcallBeginReply
        // will eventually result in a call to createOutputStream.
        //
        // If we don't have an UpcallReturn object, then it means a oneway
        // invocation was made for a twoway operation. We return a dummy
        // OutputStream to make the skeleton happy and avoid a crash.
        //
        if (upcallReturn_ != null) {
            org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[replySCL_
                    .size()];
            replySCL_.copyInto(scl);
            upcallReturn_.upcallBeginReply(this, scl);
        } else {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            out_ = new org.apache.yoko.orb.CORBA.OutputStream(buf, in_
                    ._OB_codeConverters(), (profileInfo_.major << 8)
                    | profileInfo_.minor);
        }
        out_._OB_ORBInstance(this.orbInstance());
        return out_;
    }

    public void marshalEx(org.omg.CORBA.SystemException ex)
            throws LocationForward {
        throw ex;
    }

    public void postMarshal() throws LocationForward {
        if (upcallReturn_ != null)
            upcallReturn_.upcallEndReply(this);
    }

    //
    // NOTE: Not used in Java
    //
    public void setUserException(org.omg.CORBA.UserException ex) {
        if (upcallReturn_ != null) {
            org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[replySCL_
                    .size()];
            replySCL_.copyInto(scl);
            upcallReturn_.upcallUserException(this, ex, scl);
        }
    }

    public void setUserException(org.omg.CORBA.Any any) {
        if (upcallReturn_ != null) {
            org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[replySCL_
                    .size()];
            replySCL_.copyInto(scl);
            upcallReturn_.upcallBeginUserException(this, scl);
            try {
                any.write_value(out_);
            } catch (org.omg.CORBA.SystemException ex) {
                try {
                    marshalEx(ex);
                } catch (LocationForward f) {
                    Assert._OB_assert(ex); // shouldn't happen
                }
            }
            //
            // In Java, we must delay the call to upcallEndUserException
            //
            // upcallReturn_.upcallEndUserException(this);
            userEx_ = true;
        }
    }

    //
    // This method is needed only in Java. The skeleton marshals the
    // exception. If called by a portable skeleton, the exception will
    // be null.
    //
    public org.apache.yoko.orb.CORBA.OutputStream beginUserException(
            org.omg.CORBA.UserException ex) {
        if (upcallReturn_ != null) {
            org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[replySCL_
                    .size()];
            replySCL_.copyInto(scl);
            upcallReturn_.upcallBeginUserException(this, scl);
            userEx_ = true;
            return out_;
        }

        return null;
    }

    //
    // This method is needed only in Java
    //
    public boolean userException() {
        return userEx_;
    }

    //
    // This method is needed only in Java
    //
    public void endUserException() {
        if (upcallReturn_ != null) {
            Assert._OB_assert(userEx_);
            upcallReturn_.upcallEndUserException(this);
        }
    }

    public void setSystemException(org.omg.CORBA.SystemException ex) {
        if (upcallReturn_ != null) {
            userEx_ = false; // Java only
            org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[replySCL_
                    .size()];
            replySCL_.copyInto(scl);
            upcallReturn_.upcallSystemException(this, ex, scl);
        }
    }

    public void setLocationForward(org.omg.IOP.IOR ior, boolean perm) {
        if (upcallReturn_ != null) {
            userEx_ = false; // Java only
            org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[replySCL_
                    .size()];
            replySCL_.copyInto(scl);
            upcallReturn_.upcallForward(this, ior, perm, scl);
        }
    }

    public void contextSwitch() {
        //
        // Do nothing.
        //
    }

    public void setDispatchInfo(DispatchRequest dispatchRequest,
            DispatchStrategy dispatchStrategy) {
        dispatchRequest_ = dispatchRequest;
        dispatchStrategy_ = dispatchStrategy;
    }

    public void setServantAndPOA(org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.OBPortableServer.POA_impl poa) {
        servant_ = servant;
        poa_ = poa;
    }

    //
    // Do the invocation
    //
    public void invoke() {
        //
        // If the creation of the Upcall object resulted in an error of
        // some sort then there will be no method invocation to perform.
        // In this case do nothing.
        //
        try {
            if (dispatchStrategy_ != null) {
                logger.fine("Dispatching request " + reqId_ + " with dispatch strategy " + dispatchStrategy_.getClass().getName()); 
                dispatchStrategy_.dispatch(dispatchRequest_);
            }
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(Level.FINE, "Exception received dispatching request", ex); 
            setSystemException(ex);
        }
    }
}

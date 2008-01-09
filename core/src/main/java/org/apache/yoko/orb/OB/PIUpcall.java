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

public final class PIUpcall extends Upcall {
    //
    // The PortableInterceptor manager
    //
    protected PIManager piManager_;

    //
    // The ServerRequestInfo object provided by the interceptors
    //
    protected org.omg.PortableInterceptor.ServerRequestInfo requestInfo_;

    // ----------------------------------------------------------------------
    // PIUpcall public member implementations
    // ----------------------------------------------------------------------

    public PIUpcall(ORBInstance orbInstance, UpcallReturn upcallReturn,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.apache.yoko.orb.OCI.TransportInfo transportInfo, int requestId,
            String op, org.apache.yoko.orb.CORBA.InputStream in,
            org.omg.IOP.ServiceContext[] requestSCL, PIManager piManager) {
        super(orbInstance, upcallReturn, profileInfo, transportInfo, requestId,
                op, in, requestSCL);
        piManager_ = piManager;
    }

    public void setArgDesc(ParameterDesc[] argDesc, ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) {
        piManager_.serverParameterDesc(requestInfo_, argDesc, retDesc,
                exceptionTC);
    }

    public void setArguments(org.omg.CORBA.NVList args) {
        piManager_.serverArguments(requestInfo_, args);
    }

    public void setResult(org.omg.CORBA.Any any) {
        piManager_.serverResult(requestInfo_, any);
    }

    public void receiveRequestServiceContexts(org.omg.CORBA.Policy[] policies,
            byte[] adapterId, byte[] objectId,
            org.omg.PortableInterceptor.ObjectReferenceTemplate adapterTemplate)

    throws LocationForward {
        //
        // Copy requestSCL_ into a Vector
        //
        java.util.Vector requestSCL = new java.util.Vector(requestSCL_.length);
        for (int i = 0; i < requestSCL_.length; i++)
            requestSCL.addElement(requestSCL_[i]);

        //
        // Create the requestInfo_
        //
        requestInfo_ = piManager_.serverCreateRequestInfo(op_,
                upcallReturn_ != null, policies, adapterId, objectId,
                adapterTemplate, requestSCL, replySCL_, transportInfo_);

        //
        // Call the receive_request_service_contexts interception
        // point
        //
        piManager_.serverReceiveRequestServiceContexts(requestInfo_);
    }

    public void postUnmarshal() throws LocationForward {
        piManager_.serverReceiveRequest(requestInfo_);
        super.postUnmarshal();
    }

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal()
            throws LocationForward {
        piManager_.serverSendReply(requestInfo_);
        return super.preMarshal();
    }

    //
    // NOTE: Not used in Java
    //
    public void setUserException(org.omg.CORBA.UserException ex) {
        try {
            piManager_.serverSendException(requestInfo_, false, ex);
        } catch (org.omg.CORBA.SystemException e) {
            setSystemException(e);
            return;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return;
        }
        super.setUserException(ex);
    }

    public void setUserException(org.omg.CORBA.Any any) {
        try {
            org.omg.CORBA.UnknownUserException uex = new org.omg.CORBA.UnknownUserException(
                    any);
            piManager_.serverSendException(requestInfo_, false, uex);
        } catch (org.omg.CORBA.SystemException e) {
            setSystemException(e);
            return;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return;
        }
        super.setUserException(any);
    }

    //
    // This method is needed only in Java. Marshalling is handled by the
    // skeletons. If called by a portable skeleton, the exception will be
    // null.
    //
    public org.apache.yoko.orb.CORBA.OutputStream beginUserException(
            org.omg.CORBA.UserException ex) {
        try {
            piManager_.serverSendException(requestInfo_, false, ex);
        } catch (org.omg.CORBA.SystemException e) {
            setSystemException(e);
            return null;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return null;
        }
        return super.beginUserException(ex);
    }

    public void setSystemException(org.omg.CORBA.SystemException ex) {
        try {
            piManager_.serverSendException(requestInfo_, true, ex);
        } catch (org.omg.CORBA.SystemException e) {
            setSystemException(e);
            return;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return;
        }
        super.setSystemException(ex);
    }

    public void setLocationForward(org.omg.IOP.IOR ior, boolean perm) {
        try {
            piManager_.serverSendLocationForward(requestInfo_, ior);
        } catch (org.omg.CORBA.SystemException e) {
            setSystemException(e);
            return;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return;
        }
        super.setLocationForward(ior, perm);
    }

    public void setServantAndPOA(org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.OBPortableServer.POA_impl poa) {
        piManager_.serverSetupServant(requestInfo_, servant, poa);
        super.setServantAndPOA(servant, poa);
    }

    //
    // Notify the Upcall about a potential change in the thread context
    //
    public void contextSwitch() {
        piManager_.serverContextSwitch(requestInfo_);
    }
}

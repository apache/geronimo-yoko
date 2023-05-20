/*
 * Copyright 2019 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OBPortableServer.POA_impl;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.UserException;
import org.omg.IOP.IOR;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableServer.Servant;

public final class PIUpcall extends Upcall {
    protected PIManager piManager_;
    protected ServerRequestInfo requestInfo_;

    public PIUpcall(ORBInstance orbInstance, UpcallReturn upcallReturn,
                    ProfileInfo profileInfo,
                    TransportInfo transportInfo, int requestId,
                    String op, InputStream in,
                    ServiceContexts requestContexts, PIManager piManager) {
        super(orbInstance, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts);
        piManager_ = piManager;
    }

    public void setArgDesc(ParameterDesc[] argDesc, ParameterDesc retDesc, TypeCode[] exceptionTC) {
        piManager_.serverParameterDesc(requestInfo_, argDesc, retDesc, exceptionTC);
    }

    public void setArguments(NVList args) {
        piManager_.serverArguments(requestInfo_, args);
    }

    public void setResult(Any any) {
        piManager_.serverResult(requestInfo_, any);
    }

    public void receiveRequestServiceContexts(Policy[] policies, byte[] adapterId, byte[] objectId,
                                              ObjectReferenceTemplate adapterTemplate) throws LocationForward {
        ServiceContexts requestContextsCopy = new ServiceContexts(requestContexts);

        // create the request info
        requestInfo_ = piManager_.serverCreateRequestInfo(op_, upcallReturn_ != null, policies, adapterId, objectId,
                adapterTemplate, requestContextsCopy, replyContexts, transportInfo_);

        // Call the receive_request_service_contexts interception point
        piManager_.serverReceiveRequestServiceContexts(requestInfo_);
    }

    public void postUnmarshal() throws LocationForward {
        piManager_.serverReceiveRequest(requestInfo_);
        super.postUnmarshal();
    }

    public OutputStream preMarshal() throws LocationForward {
        piManager_.serverSendReply(requestInfo_);
        return super.preMarshal();
    }

    public void setUserException(Any any) {
        try {
            UnknownUserException uex = new UnknownUserException(any);
            piManager_.serverSendException(requestInfo_, false, uex);
        } catch (SystemException e) {
            setSystemException(e);
            return;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return;
        }
        super.setUserException(any);
    }

    // Marshalling is handled by the skeletons.
    // If called by a portable skeleton, the exception will be null.
    public OutputStream beginUserException(UserException ex) {
        try {
            piManager_.serverSendException(requestInfo_, false, ex);
        } catch (SystemException e) {
            setSystemException(e);
            return null;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return null;
        }
        return super.beginUserException(ex);
    }

    public void setSystemException(SystemException ex) {
        try {
            piManager_.serverSendException(requestInfo_, true, ex);
        } catch (SystemException e) {
            setSystemException(e);
            return;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return;
        }
        super.setSystemException(ex);
    }

    public void setLocationForward(IOR ior, boolean perm) {
        try {
            piManager_.serverSendLocationForward(requestInfo_, ior);
        } catch (SystemException e) {
            setSystemException(e);
            return;
        } catch (LocationForward e) {
            setLocationForward(e.ior, e.perm);
            return;
        }
        super.setLocationForward(ior, perm);
    }

    public void setServantAndPOA(Servant servant, POA_impl poa) {
        piManager_.serverSetupServant(requestInfo_, servant, poa);
        super.setServantAndPOA(servant, poa);
    }

    // Notify the Upcall about a potential change in the thread context
    public void contextSwitch() {
        piManager_.serverContextSwitch(requestInfo_);
    }
}

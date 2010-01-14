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

//
// The MultiRequestSender class. ORB::send_multiple_requests() and all
// related operations delegate to this class.
//
public class MultiRequestSender {
    // org.apache.yoko.orb.CORBA.Request needs access to private members
    public java.util.Vector deferredRequests_ = new java.util.Vector();

    // OBORB_impl creates MultiRequestSender
    public MultiRequestSender() {
    }

    // ----------------------------------------------------------------------
    // Convenience functions for use by org.apache.yoko.orb.CORBA.Request
    // ----------------------------------------------------------------------

    public synchronized boolean findDeferredRequest(
            org.omg.CORBA.Request request) {
        for (int index = 0; index < deferredRequests_.size(); index++)
            if (deferredRequests_.elementAt(index) == request)
                return true;

        return false;
    }

    public synchronized void addDeferredRequest(org.omg.CORBA.Request request) {
        deferredRequests_.addElement(request);
    }

    public synchronized void removeDeferredRequest(org.omg.CORBA.Request request) {
        int index;
        for (index = 0; index < deferredRequests_.size(); index++)
            if (deferredRequests_.elementAt(index) == request)
                break;

        if (index < deferredRequests_.size())
            deferredRequests_.removeElementAt(index);
    }

    // ----------------------------------------------------------------------
    // Public member implementations
    // ----------------------------------------------------------------------

    public void sendMultipleRequestsOneway(org.omg.CORBA.Request[] requests) {
        //
        // Send all requests oneway
        //
        for (int i = 0; i < requests.length; i++)
            requests[i].send_oneway();
    }

    public void sendMultipleRequestsDeferred(org.omg.CORBA.Request[] requests) {
        //
        // Send all requests deferred
        //
        for (int i = 0; i < requests.length; i++)
            requests[i].send_deferred();
    }

    public synchronized boolean pollNextResponse() {
        if (deferredRequests_.size() == 0)
            throw new org.omg.CORBA.BAD_INV_ORDER(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent),
                    org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        //
        // Poll all deferred requests
        //
        boolean polled = false;
        for (int i = 0; i < deferredRequests_.size(); i++) {
            org.omg.CORBA.Request req = (org.omg.CORBA.Request) deferredRequests_
                    .elementAt(i);
            if (req.poll_response())
                polled = true;
        }

        return polled;
    }

    public synchronized org.omg.CORBA.Request getNextResponse()
            throws org.omg.CORBA.WrongTransaction {
        org.omg.CORBA.Request request = null;

        //
        // Try to find a deferred request that has completed already
        //
        for (int i = 0; i < deferredRequests_.size(); i++) {
            request = (org.omg.CORBA.Request) deferredRequests_.elementAt(i);
            if (((org.apache.yoko.orb.CORBA.Request) request)._OB_completed()) {
                deferredRequests_.removeElementAt(i);
                return request;
            }
        }

        //
        // No completed deferred request. Let's simply get the response of
        // the first request.
        //
        if (deferredRequests_.size() > 0) {
            request = (org.omg.CORBA.Request) deferredRequests_.elementAt(0);
            request.get_response();
            return request;
        }

        throw new org.omg.CORBA.BAD_INV_ORDER(org.apache.yoko.orb.OB.MinorCodes
                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent),
                org.apache.yoko.orb.OB.MinorCodes.MinorRequestNotSent,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }
}

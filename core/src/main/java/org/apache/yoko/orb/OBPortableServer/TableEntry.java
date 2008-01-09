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

package org.apache.yoko.orb.OBPortableServer;

import org.apache.yoko.orb.OB.Assert;

class TableEntry {
    //
    // The servant
    //
    private org.omg.PortableServer.Servant servant_;

    //
    // The state of the TableEntry
    //
    final static int ACTIVATE_PENDING = 0; // Entry is pending activation

    final static int ACTIVE = 1; // Entry is active

    final static int DEACTIVATE_PENDING = 2; // Entry is pending deactivation

    final static int DEACTIVATED = 3; // Entry has been deactivated

    private int state_;

    //
    // The number of outstanding requests
    //
    private int outstandingRequests_;

    TableEntry() {
        state_ = ACTIVATE_PENDING;
        outstandingRequests_ = 0;
    }

    void setServant(org.omg.PortableServer.Servant s) {
        Assert._OB_assert(servant_ == null && s != null
                && state_ == ACTIVATE_PENDING);

        servant_ = s;
    }

    void clearServant() {
        Assert._OB_assert(state_ == DEACTIVATED && outstandingRequests_ == 0);
        servant_ = null;
    }

    org.omg.PortableServer.Servant getServant() {
        Assert._OB_assert(state_ == ACTIVE || state_ == DEACTIVATE_PENDING);
        return servant_;
    }

    //
    // Is the table entry active?
    //
    int state() {
        return state_;
    }

    //
    // Mark the table entry as activation in progress
    //
    void setActive() {
        state_ = ACTIVE;
        notifyAll();
    }

    //
    // Mark the table entry as deactivate pending
    //
    void setDeactivatePending() {
        state_ = DEACTIVATE_PENDING;
        notifyAll();
    }

    //
    // Mark the deactivation of the table entry as complete
    //
    void setDeactivated() {
        state_ = DEACTIVATED;
        notifyAll();
    }

    //
    // Wait for the deactivation of the table entry to complete
    //
    void waitForStateChange() {
        int state = state_;
        do {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        } while (state_ == state);
    }

    int getOutstandingRequests() {
        return outstandingRequests_;
    }

    void incOutstandingRequest() {
        outstandingRequests_++;
    }

    int decOutstandingRequest() {
        return --outstandingRequests_;
    }
}

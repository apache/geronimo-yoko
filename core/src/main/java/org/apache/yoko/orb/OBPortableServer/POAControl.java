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

//
// This class is used to control the POA
//
class POAControl {
    private final static int DestroyNotCalled = 0;

    private final static int DestroyPending = 1;

    private final static int DestroyInProgress = 2;

    private final static int DestroyCompleted = 3;

    //
    // The POA state
    //
    private int state_;

    //
    // Should the servants be etherealized?
    //
    private boolean etherealize_;

    //
    // How many pending requests are there?
    //
    private int requests_;

    // ----------------------------------------------------------------------
    // Package member implementations
    // ----------------------------------------------------------------------

    POAControl() {
        state_ = DestroyNotCalled;
        etherealize_ = false;
        requests_ = 0;
    }

    //
    // Mark the state as destroy pending. Return true if the destroy
    // should complete, false otherwise.
    //
    synchronized boolean markDestroyPending(boolean etherealize,
            boolean waitForCompletion) {
        //
        // If the POA is in the middle of a destroy and
        // waitForCompletion is true then wait for the destroy to
        // complete.
        //
        if (state_ != DestroyNotCalled) {
            while (waitForCompletion && state_ != DestroyCompleted) {
                //
                // wait for the destroy to complete
                //
                try {
                    wait();
                } catch (InterruptedException ex) {
                }
            }
            return false;
        }

        //
        // Save the etherealize flag. Set the state to DestroyPending.
        //
        etherealize_ = etherealize;
        state_ = DestroyPending;

        return true;
    }

    //
    // Increment the outstanding request count. Return true if the POA
    // is not destroyed, false otherwise.
    //
    synchronized boolean incrementRequestCount() {
        if (state_ != DestroyNotCalled) {
            while (state_ != DestroyCompleted) {
                //
                // wait for the destroy to complete
                //
                try {
                    wait();
                } catch (InterruptedException ex) {
                }
            }
            return false;
        }

        //
        // Reference successfully acquired, return true
        //
        requests_++;

        return true;
    }

    //
    // Decrement the outstanding request count. If the state is
    // DestroyPending then wake any waiting threads and return true.
    //
    synchronized boolean decrementRequestCount() {
        org.apache.yoko.orb.OB.Assert._OB_assert(requests_ > 0);
        requests_--;

        //
        // If we have no more outstanding requests notify anyone
        // waiting for this state
        //
        if (requests_ == 0) {
            notifyAll();
            if (state_ == DestroyPending)
                return true;
        }
        return false;
    }

    //
    // Wait for any pending requests to terminate. Return true if
    // the destroy should complete, false otherwise.
    //
    synchronized boolean waitPendingRequests() {
        while (requests_ > 0) {
            //
            // wait for the destroy to complete
            //
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }

        //
        // If the state is not DestroyPending then some other thread
        // is in the progress of completing destroying the POA
        //
        if (state_ != DestroyPending)
            return false;

        state_ = DestroyInProgress;
        return true;
    }

    //
    // Return true if there are outstanding pending requests
    //
    synchronized boolean hasPendingRequests() {
        return requests_ > 0;
    }

    //
    // Has the POA been destroyed?
    //
    synchronized boolean getDestroyed() {
        return state_ == DestroyCompleted;
    }

    //
    // Mark the state as DestroyCompleted, wake any waiting threads
    //
    synchronized void markDestroyCompleted() {
        state_ = DestroyCompleted;
        notifyAll();
    }

    //
    // Should etherealize be called?
    //
    synchronized boolean etherealize() {
        return etherealize_;
    }
}

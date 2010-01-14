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

package org.apache.yoko.orb.OBCORBA;

public class PollableSet_impl extends org.omg.CORBA.LocalObject implements
        org.omg.CORBA.PollableSet {
    //
    // List of pollable objects in this set
    //
    protected java.util.LinkedList pollableList_ = new java.util.LinkedList();

    //
    // Constructor
    //
    public PollableSet_impl() {
    }

    //
    // IDL:omg.org/CORBA/PollableSet/create_dii_pollable:1.0
    //
    public org.omg.CORBA.DIIPollable create_dii_pollable() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    //
    // IDL:omg.org/CORBA/PollableSet/add_pollable:1.0
    //
    public void add_pollable(org.omg.CORBA.Pollable potential) {
        org.apache.yoko.orb.OB.Assert._OB_assert(potential != null);
        pollableList_.addLast(potential);
    }

    //
    // IDL:omg.org/CORBA/PollableSet/get_ready_pollable:1.0
    //
    public org.omg.CORBA.Pollable get_ready_pollable(int timeout)
            throws org.omg.CORBA.PollableSetPackage.NoPossiblePollable,
            org.omg.CORBA.SystemException {
        if (pollableList_.size() == 0)
            throw new org.omg.CORBA.PollableSetPackage.NoPossiblePollable();

        //
        // try to return a pollable item in the timeout specified
        //
        while (true) {
            //
            // starting time of query
            //
            long start_time = System.currentTimeMillis();

            //
            // are there any pollables ready?
            //
            java.util.ListIterator iter = pollableList_.listIterator(0);
            while (iter.hasNext()) {
                org.omg.CORBA.Pollable pollable = (org.omg.CORBA.Pollable) iter
                        .next();

                if (pollable.is_ready(0)) {
                    iter.remove();
                    return pollable;
                }
            }

            //
            // none are ready yet so we need to block on the
            // OrbAsyncHandler until a new response is received or throw
            // a NO_RESPONSE if there is no timeout specified
            //
            if (timeout == 0)
                throw new org.omg.CORBA.NO_RESPONSE();

            //
            // Yield for now to give another thread a timeslice
            //
            Thread.yield();

            //
            // just return if timeout is INFINITE
            //
            if (timeout == -1)
                continue;

            //
            // the ending time of the query
            //
            long end_time = System.currentTimeMillis();

            //
            // subtract difference in time from the timeout value
            //
            long diff_time = end_time - start_time;
            if (diff_time > timeout)
                timeout = 0;
            else
                timeout -= diff_time;

            //
            // check if all the time has now expired
            //
            if (timeout == 0)
                throw new org.omg.CORBA.TIMEOUT();
        }
    }

    //
    // IDL:omg.org/CORBA/PollableSet/remove:1.0
    //
    public void remove(org.omg.CORBA.Pollable potential)
            throws org.omg.CORBA.PollableSetPackage.UnknownPollable {
        org.apache.yoko.orb.OB.Assert._OB_assert(potential != null);

        //
        // iterate the list, looking for a match
        //
        java.util.ListIterator iter = pollableList_.listIterator(0);
        while (iter.hasNext()) {
            if (potential == iter.next()) {
                iter.remove();
                return;
            }
        }

        //
        // never found the item
        //
        throw new org.omg.CORBA.PollableSetPackage.UnknownPollable();
    }

    //
    // IDL:omg.org/CORBA/PollableSet/number_left:1.0
    //
    public short number_left() {
        return (short) pollableList_.size();
    }
}

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

//
// Implementation Notes:
//
// The Current implementation needs to have a stack of slot
// information.  This is to deal with collocated calls that occur in
// the same thread as the client. In this case the server current
// should not overwrite the content of the client side current.
//

package org.apache.yoko.orb.PortableInterceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

final public class Current_impl extends org.omg.CORBA.LocalObject implements
        org.omg.PortableInterceptor.Current {
    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(Current_impl.class.getName());
    
    private class SlotData {
        org.omg.CORBA.Any[] slots;

        SlotData next;

        SlotData(org.omg.CORBA.Any[] s) {
            slots = s;
        }

        SlotData() {
            slots = new org.omg.CORBA.Any[0];
        }
    }

    private class SlotDataHolder {
        SlotData head;
    }

    //
    // The WeakHashMap is not thread-safe so we need to utilize one of
    // these thread-safe wrappers
    // 
    private java.util.Map stateKey_ = java.util.Collections
            .synchronizedMap(new java.util.WeakHashMap());

    private org.omg.CORBA.ORB orb_; // Java only

    private int maxSlots_;

    // ------------------------------------------------------------------
    // Private member implementations
    // ------------------------------------------------------------------

    private SlotDataHolder establishTSD(boolean partial) {
        Thread t = Thread.currentThread();
        SlotDataHolder holder_ = (SlotDataHolder) stateKey_.get(t);

        //
        // If the data isn't already allocated then allocate a new
        // SlotDataHolder and a new set of slots
        //
        if (holder_ == null) {
            holder_ = new SlotDataHolder();
            stateKey_.put(t, holder_);

            org.omg.CORBA.Any[] slots = null;

            //
            // This is an optimization. If this is a partial allocation
            // then it's not necessary to allocate data for the actual
            // slots.
            //
            if (!partial) {
                slots = new org.omg.CORBA.Any[maxSlots_];
            }

            holder_.head = new SlotData(slots);
            holder_.head.next = null;
        }

        return holder_;
    }

    // ------------------------------------------------------------------
    // Public member implementations
    // ------------------------------------------------------------------

    public org.omg.CORBA.Any get_slot(int id)
            throws org.omg.PortableInterceptor.InvalidSlot {
        if (id >= maxSlots_) {
            throw new org.omg.PortableInterceptor.InvalidSlot();
        }

        logger.fine("getting slot " + id); 
        
        SlotDataHolder holder = establishTSD(false);

        org.omg.CORBA.Any result;
        org.omg.CORBA.Any slot = holder.head.slots[id];
        if (slot == null) {
            result = orb_.create_any();
        }
        else {
            result = new org.apache.yoko.orb.CORBA.Any(slot);
        }

        return result;
    }

    public void set_slot(int id, org.omg.CORBA.Any any)
            throws org.omg.PortableInterceptor.InvalidSlot {
        if (id >= maxSlots_) {
            throw new org.omg.PortableInterceptor.InvalidSlot();
        }
        
        logger.fine("setting slot " + id); 

        SlotDataHolder holder = establishTSD(false);

        holder.head.slots[id] = new org.apache.yoko.orb.CORBA.Any(any);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Current_impl(org.omg.CORBA.ORB orb) {
        orb_ = orb;
    }

    org.omg.CORBA.Any[] _OB_currentSlotData() {
        SlotDataHolder holder = establishTSD(false);

        org.omg.CORBA.Any[] data = new org.omg.CORBA.Any[holder.head.slots.length];
        for (int i = 0; i < holder.head.slots.length; i++) {
            org.omg.CORBA.Any slot = holder.head.slots[i];
            if (slot != null) {
                data[i] = new org.apache.yoko.orb.CORBA.Any(slot);
            }
        }
        return data;
    }

    //
    // On the client side a completely new set of slots are needed
    // during the actual interceptor call, this new set is managed by
    // the client request info.
    //
    // On the server side the set of slots are shared between the
    // interceptor and the server side PICurrent
    //
    void _OB_pushSlotData(org.omg.CORBA.Any[] slots) {
        logger.fine("pushing slot data"); 
        SlotDataHolder holder = establishTSD(false);

        SlotData newSlots = new SlotData(slots);
        newSlots.next = holder.head;
        holder.head = newSlots;
    }

    void _OB_popSlotData() {
        logger.fine("popping slot data"); 
        SlotDataHolder holder = establishTSD(false);

        holder.head = holder.head.next;
        org.apache.yoko.orb.OB.Assert._OB_assert(holder.head != null);
    }

    org.omg.CORBA.Any[] _OB_newSlotTable() {
        return new org.omg.CORBA.Any[maxSlots_];
    }

    public void _OB_setMaxSlots(int max) {
        maxSlots_ = max;
    }
}

/*
 * Copyright 2022 IBM Corporation and others.
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
package org.apache.yoko.orb.PortableInterceptor;

import org.apache.yoko.util.Assert;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

final public class Current_impl extends LocalObject implements Current {
    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = getLogger(Current_impl.class.getName());
    
    private static class SlotData {
        Any[] slots;
        SlotData next;

        SlotData(Any[] s) {
            slots = s;
        }
    }

    private static class SlotDataHolder {
        SlotData head;
    }

    private final ThreadLocal<SlotDataHolder> stateKey = new ThreadLocal<>();

    private final ORB orb_; // Java only

    private int maxSlots_;

    // ------------------------------------------------------------------
    // Private member implementations
    // ------------------------------------------------------------------

    private SlotDataHolder establishTSD() {
        SlotDataHolder holder = stateKey.get();

        if (null == holder) {
            holder = new SlotDataHolder();
            stateKey.set(holder);

            Any[] slots = new Any[maxSlots_];
            holder.head = new SlotData(slots);
            holder.head.next = null;
        }

        return holder;
    }

    // ------------------------------------------------------------------
    // Public member implementations
    // ------------------------------------------------------------------

    public Any get_slot(int id) throws InvalidSlot {
        if (id >= maxSlots_ || id < 0) throw new InvalidSlot("No slot for id " + id);

        logger.fine("getting slot " + id); 
        
        SlotDataHolder holder = establishTSD();

        Any slot = holder.head.slots[id];
        if (slot == null) return orb_.create_any();
        return new org.apache.yoko.orb.CORBA.Any(slot);
    }

    public void set_slot(int id, Any any) throws InvalidSlot {
        if (id >= maxSlots_ || id < 0) throw new InvalidSlot("No slot for id " + id);

        logger.fine("setting slot " + id); 

        SlotDataHolder holder = establishTSD();

        holder.head.slots[id] = new org.apache.yoko.orb.CORBA.Any(any);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Current_impl(ORB orb) { orb_ = orb; }

    Any[] _OB_currentSlotData() {
        SlotDataHolder holder = establishTSD();

        Any[] data = new Any[holder.head.slots.length];
        for (int i = 0; i < holder.head.slots.length; i++) {
            Any slot = holder.head.slots[i];
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
    void _OB_pushSlotData(Any[] slots) {
        logger.fine("pushing slot data"); 
        SlotDataHolder holder = establishTSD();

        SlotData newSlots = new SlotData(slots);
        newSlots.next = holder.head;
        holder.head = newSlots;
    }

    void _OB_popSlotData() {
        logger.fine("popping slot data"); 
        SlotDataHolder holder = establishTSD();

        holder.head = holder.head.next;
        Assert.ensure(holder.head != null);
    }

    Any[] _OB_newSlotTable() {
        return new Any[maxSlots_];
    }

    public void _OB_setMaxSlots(int max) {
        maxSlots_ = max;
    }
}

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

package org.apache.yoko.orb.OBMessaging;

public class Poller_impl implements org.omg.CORBA.Pollable,
        org.omg.Messaging.Poller {
    //
    // ORBInstance this poller is bound to
    //
    protected org.apache.yoko.orb.OB.ORBInstance orbInstance_ = null;

    //
    // operation target
    //
    protected org.omg.CORBA.Object objectTarget_ = null;

    //
    // Operation name
    //
    protected String operationName_ = null;

    //
    // Associated ReplyHandler
    //
    protected org.omg.Messaging.ReplyHandler replyHandler_ = null;

    // ----------------------------------------------------------------
    // Standard IDL to Java mapping
    // ----------------------------------------------------------------
    public String[] _truncatable_ids() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    // ----------------------------------------------------------------
    // From org.omg.CORBA.Pollable
    // ----------------------------------------------------------------

    //
    // IDL:omg.org/CORBA/Pollable/is_ready:1.0
    //
    public boolean is_ready(int timeout) {
        org.apache.yoko.orb.OB.Assert._OB_assert(orbInstance_ != null);

        org.apache.yoko.orb.OB.OrbAsyncHandler handler = orbInstance_
                .getAsyncHandler();
        return handler.is_ready(this, timeout);
    }

    //
    // IDL:omg.org/CORBA/Pollable/create_pollable_set:1.0
    //
    public org.omg.CORBA.PollableSet create_pollable_set() {
        org.apache.yoko.orb.OB.Assert._OB_assert(orbInstance_ != null);

        return new org.apache.yoko.orb.OBCORBA.PollableSet_impl();
    }

    // ----------------------------------------------------------------
    // From org.omg.Messaging.Poller
    // ----------------------------------------------------------------

    //
    // IDL:omg.org/Messaging/Poller/operation_target:1.0
    //
    public org.omg.CORBA.Object operation_target() {
        return objectTarget_;
    }

    //
    // IDL:omg.org/Messaging/Poller/operation_name:1.0
    //
    public String operation_name() {
        return operationName_;
    }

    //
    // IDL:omg.org/Messaging/Poller/associated_handler:1.0
    //
    public org.omg.Messaging.ReplyHandler associated_handler() {
        return replyHandler_;
    }

    public void associated_handler(org.omg.Messaging.ReplyHandler handler) {
        replyHandler_ = handler;
    }

    //
    // IDL:omg.org/Messasging/Poller/is_from_poller:1.0
    //
    public boolean is_from_poller() {
        return false;
    }

    // ----------------------------------------------------------------
    // Proprietary methods used by the ORB
    // ----------------------------------------------------------------

    //
    // set the internal ORBInstance handle
    // 
    public void _OB_ORBInstance(org.apache.yoko.orb.OB.ORBInstance orb) {
        orbInstance_ = orb;
    }

    //
    // set the object target reference in this poller
    //
    public void _OB_Object(org.omg.CORBA.Object obj) {
        objectTarget_ = obj;
    }

    //
    // get the response of this request
    //
    public org.apache.yoko.orb.OB.Downcall _OB_poll_response() {
        org.apache.yoko.orb.OB.OrbAsyncHandler handler = orbInstance_
                .getAsyncHandler();
        return handler.poll_response(this);
    }
}

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
package org.apache.yoko.orb.exceptions;

import org.apache.yoko.ApacheVMCID;
import org.apache.yoko.util.Factory;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.OMGVMCID;
import org.omg.CORBA.TRANSIENT;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public enum Transients implements Factory<TRANSIENT> {
    // OMG minor codes
    REQUEST_DISCARDED
            (0x4F4D0_001, "request has been discarded"),
    NO_USABLE_PROFILE_IN_IOR
            (0x4F4D0_002, "no usable profile in IOR"),
    REQUEST_CANCELLED
            (0x4F4D0_003, "request has been cancelled"),
    POA_DESTROYED
            (0x4F4D0_004, "POA has been destroyed"),
    // Apache minor codes
    CONNECT_FAILED
            (0x41534_001, "attempt to establish connection failed"),
    CLOSE_CONNECTION
            (0x41534_002, "got a CloseConnection message"),
    ACTIVE_CONNECTION_MANAGEMENT
            (0x41534_003, "active connection management closed connection"),
    FORCED_SHUTDOWN
            (0x41534_004, "forced connection shutdown because of timeout", COMPLETED_MAYBE),
    LOCATION_FORWARD_TOO_MANY_HOPS
            (0x41534_005, "maximum forwarding count (10) exceeded"),
    ;
    private final static Map<Integer, Transients> MINOR_CODE_MAP;
    static {
        Map<Integer, Transients> map = new HashMap<>();
        for (Transients t: values()) map.put(t.minor, t);
        MINOR_CODE_MAP = Collections.unmodifiableMap(map);
    }

    final String reason;
    final int minor;
    final CompletionStatus completed;

    Transients(int minor, String reason, CompletionStatus completed) {
        final int base = minor & 0xFFFFF000;
        assert base == OMGVMCID.value || base == ApacheVMCID.value;
        this.reason = reason;
        this.minor = minor;
        this.completed = completed;
    }

    Transients(int minor, String reason) { this(minor, reason, COMPLETED_NO); }

    static String describe(int minor) {
        Transients t = MINOR_CODE_MAP.get(minor);
        return t == null ? null : t.reason;
    }

    public static TRANSIENT create(int minor, CompletionStatus completed) {
        return new TRANSIENT(describe(minor), minor, completed);
    }

    @Override
    public TRANSIENT create() { return new TRANSIENT(reason, minor, completed); }

    public boolean matches(TRANSIENT t) { return t.minor == minor; }
}

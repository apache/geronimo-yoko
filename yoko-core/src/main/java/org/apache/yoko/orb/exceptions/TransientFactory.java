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

import org.apache.yoko.util.Factory;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TRANSIENT;

import static org.apache.yoko.orb.OB.MinorCodes.MinorActiveConnectionManagement;
import static org.apache.yoko.orb.OB.MinorCodes.MinorCloseConnection;
import static org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed;
import static org.apache.yoko.orb.OB.MinorCodes.MinorForcedShutdown;
import static org.apache.yoko.orb.OB.MinorCodes.MinorLocationForwardHopCountExceeded;
import static org.apache.yoko.orb.OB.MinorCodes.MinorNoUsableProfileInIOR;
import static org.apache.yoko.orb.OB.MinorCodes.describeTransient;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public enum TransientFactory implements Factory<TRANSIENT> {
    NO_USABLE_PROFILE_IN_IOR(MinorNoUsableProfileInIOR),
    LOCATION_FORWARD_TOO_MANY_HOPS(MinorLocationForwardHopCountExceeded),
    CONNECT_FAILED(MinorConnectFailed),
    ACTIVE_CONNECTION_MANAGEMENT(MinorActiveConnectionManagement),
    CLOSE_CONNECTION(MinorCloseConnection),
    FORCED_SHUTDOWN(MinorForcedShutdown, COMPLETED_MAYBE)
    ;

    final String reason;
    final int minorCode;
    final CompletionStatus completionStatus;

    TransientFactory(String reason, int minorCode, CompletionStatus completionStatus) {
        this.reason = reason;
        this.minorCode = minorCode;
        this.completionStatus = completionStatus;
    }

    TransientFactory(int minorCode, CompletionStatus completionStatus) {
        this(describeTransient(minorCode), minorCode, completionStatus);
    }

    TransientFactory(int minorCode) {
        this(minorCode, COMPLETED_NO);
    }

    @Override
    public TRANSIENT create() {
        return new TRANSIENT(reason, minorCode, completionStatus);
    }
}

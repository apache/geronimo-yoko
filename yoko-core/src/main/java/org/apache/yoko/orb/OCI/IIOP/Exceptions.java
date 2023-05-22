/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.orb.OCI.IIOP;

import static org.apache.yoko.util.MinorCodes.*;

import org.apache.yoko.util.Wrapper;
import org.omg.CORBA.COMM_FAILURE;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.apache.yoko.util.MinorCodes.MinorSetsockopt;
import static org.apache.yoko.util.MinorCodes.describeCommFailure;
import static org.apache.yoko.util.Exceptions.as;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

enum Exceptions {;
    static COMM_FAILURE asCommFailure(SocketException e) {return asCommFailure(e, MinorSetsockopt);}
    static COMM_FAILURE asCommFailure(UnknownHostException e) {return asCommFailure(e, MinorGethostbyname);}

    static COMM_FAILURE asCommFailure(IOException e, int minor) {
        String msg = String.format("%s: %s", describeCommFailure(minor), e.getMessage());
        return as(COMM_FAILURE::new, e, msg, minor, COMPLETED_NO);
    }

    static COMM_FAILURE asCommFailure(Exception e, int minor, String message) {
        String msg = String.format("%s: %s: %s", describeCommFailure(minor), message, e.getMessage());
        return as(COMM_FAILURE::new, e, msg, minor, COMPLETED_NO);
    }
}

enum CommFailures implements Wrapper<Exception, COMM_FAILURE> {
    /*
     * TODO: complete conversion of all COMM_FAILURE creation
     * This is a work in progress. To complete it, the describeCommFailure() method
     * and all the minor codes relating to COMM_FAILURE should disappear from MinorCodes
     * and exist entirely within this mechanism.
     */
    ACCEPT(MinorAccept),
    SET_SOCK_OPT(MinorSetsockopt),
    GET_HOST_BY_NAME(MinorGethostbyname),
    SOCKET(MinorSocket),
    BIND(MinorBind)
    ;

    private final int minor;
    private final String reason;

    CommFailures(int minor, String reason) {
        this.minor = minor;
        this.reason = reason;
    }

    CommFailures(int minor) { this(minor, null); }

    @Override
    public COMM_FAILURE wrap(Exception e) {
        return as(COMM_FAILURE::new, e, reason(e), minor, COMPLETED_NO);
    }

    private String reason(Exception e) {
        return reason == null ?
                String.format("%s: %s", describeCommFailure(minor), e.getMessage()) :
                String.format("%s: %s: %s", describeCommFailure(minor), reason, e.getMessage());
    }
}

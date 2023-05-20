/*
 * Copyright 2018 IBM Corporation and others.
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
package org.apache.yoko.orb.OB;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public final class Net {
    //
    // Compare two hosts strings for equality. These don't necessarily
    // mean they are string comparable... for instance www.cnn.com and
    // 64.236.24.12 could be the same host
    //
    private static final String LOOPBACK_NAME = "127.0.0.1";

    public static boolean CompareHosts(final String host1, final String host2, final boolean matchLoopback) {
        // compare name matches
        if (host1.equals(host2) || (matchLoopback && host2.equals(LOOPBACK_NAME))) return true;
        try {
            // compare address matches
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws UnknownHostException {
                    InetAddress addr1 = InetAddress.getByName(host1);
                    InetAddress addr2 = InetAddress.getByName(host2);
                    return (addr1.equals(addr2) ||
                            (matchLoopback && addr2.equals(InetAddress.getByName(LOOPBACK_NAME))));
                }
            });
        } catch (PrivilegedActionException e) {
            try {
                throw e.getException();
            } catch (RuntimeException re) {
                throw re;
            } catch (UnknownHostException uhe) {
                return false;
            } catch (Exception e2) {
                throw new RuntimeException("Unexpected exception", e2);
            }
        }
    }

    public static boolean CompareHosts(final String host1, final String host2) {
        return CompareHosts(host1, host2, false);
    }

    //
    // Get the "canonical" hostname of the local host. If numeric is
    // true, the canonical IP address will be returned instead.
    //
    public static String getCanonicalHostname(boolean numeric) {
        String host;

        try {
            if (!numeric) {
                host = java.net.InetAddress.getLocalHost().getHostName(); 
            } else {
                host = java.net.InetAddress.getLocalHost().getHostAddress();
            }

            if (host.equals("127.0.0.1") || host.equals("localhost")) {
            }
        } catch (java.net.UnknownHostException ex) {
            // logger.warning("ORB_init: " +
            // "can't resolve hostname\n" +
            // "using `localhost' (127.0.0.1) " +
            // "instead of hostname");

            if (numeric)
                host = "127.0.0.1";
            else
                host = "localhost";
        }

        return host;
    }
}

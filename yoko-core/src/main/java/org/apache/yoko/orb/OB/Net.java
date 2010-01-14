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

package org.apache.yoko.orb.OB;

public final class Net {
    //
    // Compare two hosts strings for equality. These don't necessarily
    // mean they are string comparable... for instance www.cnn.com and
    // 64.236.24.12 could be the same host
    //
    public static boolean CompareHosts(String host1, String host2) {
        try {
            java.net.InetAddress addr1 = java.net.InetAddress.getByName(host1);
            java.net.InetAddress addr2 = java.net.InetAddress.getByName(host2);

            if (addr1.equals(addr2))
                return true;
        } catch (java.net.UnknownHostException ex) {
            return false;
        }

        return false;
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

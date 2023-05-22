/*
 * Copyright 2020 IBM Corporation and others.
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

import org.omg.IIOP.ListenPoint;

import java.util.*;

public final class ListenerMap {
    // Internal list of ListenPoints
    private final Set<EndPoint> endPoints = new LinkedHashSet<>();

    // adds a new endpoint to the internal list
    public void add(String host, short port) { endPoints.add(new EndPoint(host, port)); }

    // removes an endpoint from the internal list
    public void remove(String host, short port) { endPoints.remove(new EndPoint(host, port)); }

    // returns an array of ListenPoints from this ListenerMap
    public ListenPoint[] getListenPoints() {
        List<ListenPoint> listenPoints = new ArrayList<>();
        for (EndPoint ep: endPoints) listenPoints.add(ep.asListenPoint());
        return listenPoints.toArray(new ListenPoint[0]);
    }

    /**
     * Internal, immutable version of the ListenPoint IDL-generated class
     */
    static final class EndPoint {
        static final String _ob_id = "IDL:omg.org/IIOP/ListenPoint:1.0";
        final String host;
        final short port;

        EndPoint(String host, short port) {
            this.host = host;
            this.port = port;
        }

        ListenPoint asListenPoint() { return new ListenPoint(host, port); }

        @Override
        public boolean equals(Object theOther) {
            if (this == theOther) return true;
            if (theOther == null || getClass() != theOther.getClass()) return false;
            org.omg.IIOP.ListenPoint that = (org.omg.IIOP.ListenPoint) theOther;
            return this.port == that.port && Objects.equals(this.host, that.host);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port);
        }
    }
}

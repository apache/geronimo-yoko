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

package org.apache.yoko.orb.OCI.IIOP;

public final class ListenerMap {
    //
    // Internal list of ListenPoints
    // 
    private java.util.LinkedList list_ = new java.util.LinkedList();

    //
    // adds a new endpoint to the internal list
    // 
    public void add(String host, short port) {
        //
        // first make sure we don't already have this host/port in our
        // list
        // 
        java.util.ListIterator i = list_.listIterator(0);
        while (i.hasNext()) {
            org.omg.IIOP.ListenPoint lp = (org.omg.IIOP.ListenPoint) i.next();

            if (match(lp, host, port))
                return;
        }

        // 
        // we are now free to add this item
        //
        org.omg.IIOP.ListenPoint lp = new org.omg.IIOP.ListenPoint(host, port);
        list_.add(lp);
    }

    // 
    // removes an endpoint from the internal list
    // 
    public void remove(String host, short port) {
        java.util.ListIterator i = list_.listIterator(0);
        while (i.hasNext()) {
            org.omg.IIOP.ListenPoint lp = (org.omg.IIOP.ListenPoint) i.next();
            if (match(lp, host, port)) {
                i.remove();
                return;
            }
        }
    }

    // 
    // checks if a ListenPoint is equal to the specified host/port
    // 
    public boolean match(org.omg.IIOP.ListenPoint lp, String host, short port) {
        if (lp.port != port)
            return false;

        if (host.equals(lp.host))
            return true;

        return false;
    }

    // 
    // returns an array of ListenPoints from this ListenerMap
    //
    public org.omg.IIOP.ListenPoint[] getListenPoints() {
        org.omg.IIOP.ListenPoint[] lpl = new org.omg.IIOP.ListenPoint[list_
                .size()];

        int i = 0;
        java.util.ListIterator itor = list_.listIterator();
        while (itor.hasNext()) {
            lpl[i++] = (org.omg.IIOP.ListenPoint) itor.next();
        }

        return lpl;
    }
}

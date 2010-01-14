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

package org.apache.yoko.orb.IMR;

//
// IDL:orb.yoko.apache.org/IMR/ActiveState:1.0
//
/***/

public interface ActiveStateOperations
{
    //
    // IDL:orb.yoko.apache.org/IMR/ActiveState/set_status:1.0
    //
    /**
     *
     * Called to update the server status
     *
     * @param id The server id
     * @param status The new server status
     *
     **/

    void
    set_status(String id,
               ServerStatus status);

    //
    // IDL:orb.yoko.apache.org/IMR/ActiveState/poa_create:1.0
    //
    /**
     *
     * Called when a POA is created
     *
     * @param state The POA state
     * @param poa_tmpl The POAs ORT
     * @return The IMRs ORT
     *
     * @exception org.apache.yoko.orb.IMR._NoSuchPOA If a record for the POA does not exist
     *
     **/

    org.omg.PortableInterceptor.ObjectReferenceTemplate
    poa_create(POAStatus state,
               org.omg.PortableInterceptor.ObjectReferenceTemplate poa_tmpl)
        throws _NoSuchPOA;

    //
    // IDL:orb.yoko.apache.org/IMR/ActiveState/poa_status_update:1.0
    //
    /**
     *
     * Called when POA Manager changes state.
     *
     * @param poas List of affected POAs
     * @param state The POA Manager state
     *
     **/

    void
    poa_status_update(String[][] poas,
                      POAStatus state);
}

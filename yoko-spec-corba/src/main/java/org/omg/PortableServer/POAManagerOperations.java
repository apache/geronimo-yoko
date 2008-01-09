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

package org.omg.PortableServer;

//
// IDL:omg.org/PortableServer/POAManager:2.3
//
/***/

public interface POAManagerOperations
{
    //
    // IDL:omg.org/PortableServer/POAManager/activate:1.0
    //
    /***/

    void
    activate()
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive;

    //
    // IDL:omg.org/PortableServer/POAManager/hold_requests:1.0
    //
    /***/

    void
    hold_requests(boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive;

    //
    // IDL:omg.org/PortableServer/POAManager/discard_requests:1.0
    //
    /***/

    void
    discard_requests(boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive;

    //
    // IDL:omg.org/PortableServer/POAManager/deactivate:1.0
    //
    /***/

    void
    deactivate(boolean etherealize_objects,
               boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive;

    //
    // IDL:omg.org/PortableServer/POAManager/get_state:1.0
    //
    /***/

    org.omg.PortableServer.POAManagerPackage.State
    get_state();

    //
    // IDL:omg.org/PortableServer/POAManager/get_id:1.0
    //
    /***/

    String
    get_id();
}

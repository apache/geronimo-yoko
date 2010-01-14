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

package test.poa;

//
// IDL:POAManagerProxy:1.0
//
/***/

public interface POAManagerProxyOperations
{
    //
    // IDL:POAManagerProxy/activate:1.0
    //
    /***/

    void
    activate()
        throws test.poa.POAManagerProxyPackage.AdapterInactive;

    //
    // IDL:POAManagerProxy/hold_requests:1.0
    //
    /***/

    void
    hold_requests(boolean wait_for_completion)
        throws test.poa.POAManagerProxyPackage.AdapterInactive;

    //
    // IDL:POAManagerProxy/discard_requests:1.0
    //
    /***/

    void
    discard_requests(boolean wait_for_completion)
        throws test.poa.POAManagerProxyPackage.AdapterInactive;

    //
    // IDL:POAManagerProxy/deactivate:1.0
    //
    /***/

    void
    deactivate(boolean etherealize_objects,
               boolean wait_for_completion)
        throws test.poa.POAManagerProxyPackage.AdapterInactive;

    //
    // IDL:POAManagerProxy/get_state:1.0
    //
    /***/

    test.poa.POAManagerProxyPackage.State
    get_state();
}

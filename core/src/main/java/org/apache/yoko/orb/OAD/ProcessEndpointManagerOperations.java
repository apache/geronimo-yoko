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

package org.apache.yoko.orb.OAD;

//
// IDL:orb.yoko.apache.org/OAD/ProcessEndpointManager:1.0
//
/**
 *
 * The ProcessEndpointManager. This endpoint tracks links
 * between the ProcessManager and spawned processes
 *
 **/

public interface ProcessEndpointManagerOperations
{
    //
    // IDL:orb.yoko.apache.org/OAD/ProcessEndpointManager/establish_link:1.0
    //
    /**
     *
     * Called by the Process to establish a link between the Process
     * and the ProcessManager
     *
     * @param server The server name
     * @param instance The Server Instance ID
     * @param pid The Server Process ID
     * @param endpoint The ProcessEndpoint
     *
     * @exception org.apache.yoko.orb.OAD.AlreadyLinked Thrown if the process is already linked
     *
     **/

    void
    establish_link(String server,
                   String id,
                   int pid,
                   ProcessEndpoint endpoint)
        throws AlreadyLinked;
}

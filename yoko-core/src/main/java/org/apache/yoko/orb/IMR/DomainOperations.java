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
// IDL:orb.yoko.apache.org/IMR/Domain:1.0
//
/***/

public interface DomainOperations
{
    //
    // IDL:orb.yoko.apache.org/IMR/Domain/registerServer:1.0
    //
    /**
     *
     * Called for automatic server registration
     *
     * @param name The server name
     * @param exec The server executable path
     * @param host The server host name
     *
     * @return Whether an entry already existed
     *
     **/

    void
    registerServer(String name,
                   String exec,
                   String host)
        throws ServerAlreadyRegistered;

    //
    // IDL:orb.yoko.apache.org/IMR/Domain/startup:1.0
    //
    /**
     *
     * Called to inform the IMR that the server has been started
     *
     * @param serverId The server id
     * @param instance The server instance
     * @param root_tmpl The RootPOA ORT
     * @param endpoint The ProcessControllerRegistrar for the process
     *
     **/

    ActiveState
    startup(String serverId,
            String instance,
            org.omg.PortableInterceptor.ObjectReferenceTemplate root_tmpl,
            org.apache.yoko.orb.OAD.ProcessEndpointManagerHolder endpoint)
        throws NoSuchServer,
               NoSuchOAD,
               OADNotRunning;
}

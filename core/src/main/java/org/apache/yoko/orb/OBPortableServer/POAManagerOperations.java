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

package org.apache.yoko.orb.OBPortableServer;

//
// IDL:orb.yoko.apache.org/OBPortableServer/POAManager:1.0
//
/**
 *
 * This interface is a proprietary extension to the standard POAManager.
 *
 * @see PortableServer::POAManager
 *
 **/

public interface POAManagerOperations extends org.omg.PortableServer.POAManagerOperations
{
    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POAManager/get_acceptors:1.0
    //
    /**
     *
     * Retrieve the acceptors associated with this POAManager.
     *
     * @return The acceptors.
     *
     **/

    org.apache.yoko.orb.OCI.Acceptor[]
    get_acceptors()
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive;
}

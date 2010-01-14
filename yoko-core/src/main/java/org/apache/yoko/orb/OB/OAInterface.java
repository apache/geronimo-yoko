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

public interface OAInterface {
    //
    // Create new Upcall object
    //
    Upcall createUpcall(UpcallReturn upcallReturn,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.apache.yoko.orb.OCI.TransportInfo transportInfo, int requestId,
            String op, org.apache.yoko.orb.CORBA.InputStream in,
            org.omg.IOP.ServiceContext[] requestSCL);

    //
    // Determine if an object with the provided object key exists
    //
    // This enumeration mirrors the GIOP::LocateStatusType_1_2
    //
    int UNKNOWN_OBJECT = 0;

    int OBJECT_HERE = 1;

    int OBJECT_FORWARD = 2;

    int OBJECT_FORWARD_PERM = 3;

    int findByKey(byte[] key, org.omg.IOP.IORHolder ior);

    //
    // Get all profiles that are usable with this OAInterface
    //
    org.apache.yoko.orb.OCI.ProfileInfo[] getUsableProfiles(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies);

    //
    // Discard all incoming requests with a TRANSIENT exception
    //
    void discard();

    //
    // Allow associated POAs to receive requests
    //
    void activate();
}

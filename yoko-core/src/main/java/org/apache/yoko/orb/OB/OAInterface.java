/*
 * Copyright 2019 IBM Corporation and others.
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
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHolder;

public interface OAInterface {
    //
    // Create new Upcall object
    //
    Upcall createUpcall(UpcallReturn upcallReturn,
                        ProfileInfo profileInfo,
                        TransportInfo transportInfo, int requestId,
                        String op, InputStream in,
                        ServiceContexts requestContexts);

    //
    // Determine if an object with the provided object key exists
    //
    // This enumeration mirrors the GIOP::LocateStatusType_1_2
    //
    int UNKNOWN_OBJECT = 0;

    int OBJECT_HERE = 1;

    int OBJECT_FORWARD = 2;

    int OBJECT_FORWARD_PERM = 3;

    int findByKey(byte[] key, IORHolder ior);

    //
    // Get all profiles that are usable with this OAInterface
    //
    ProfileInfo[] getUsableProfiles(
            IOR ior, Policy[] policies);

    //
    // Discard all incoming requests with a TRANSIENT exception
    //
    void discard();

    //
    // Allow associated POAs to receive requests
    //
    void activate();
}

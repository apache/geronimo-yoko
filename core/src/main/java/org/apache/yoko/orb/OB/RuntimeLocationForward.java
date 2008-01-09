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

//
// This exception should ONLY be used when the ORB needs to bypass
// a standardized API. The checked exception LocationForward should be
// used in all other cases.
//
final public class RuntimeLocationForward extends RuntimeException {
    public org.omg.IOP.IOR ior; // Forwarded IOR

    public boolean perm; // Is this a LOCATION_FORWARD_PERM?

    public RuntimeLocationForward(org.omg.IOP.IOR i, boolean p) {
        ior = i;
        perm = p;
    }
}

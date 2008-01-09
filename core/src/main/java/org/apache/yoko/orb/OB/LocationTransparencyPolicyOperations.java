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
// IDL:orb.yoko.apache.org/OB/LocationTransparencyPolicy:1.0
//
/**
 *
 * The location transparency policy. This policy is used to control
 * how strict the ORB is in enforcing location transparency. This is
 * useful for performance reasons.
 *
 **/

public interface LocationTransparencyPolicyOperations extends org.omg.CORBA.PolicyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/LocationTransparencyPolicy/value:1.0
    //
    /**
     *
     * <code>LOCATION_TRANSPARENCY_STRICT</code> ensures strict
     * location transparency is followed.
     * <code>LOCATION_TRANSPARENCY_RELAXED</code> relaxes the location
     * transparency guarantees for performance reasons.  Specifically
     * for collocated method invocations, the dispatch concurrency
     * model will be ignored, and policy overrides are not
     * removed. The default value is
     * <code>LOCATION_TRANSPARENCY_STRICT</code>.
     *
     **/

    short
    value();
}

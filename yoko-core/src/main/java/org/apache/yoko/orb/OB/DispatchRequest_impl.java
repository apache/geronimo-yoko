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

import org.apache.yoko.orb.OB.DispatchRequest;

final public class DispatchRequest_impl extends org.omg.CORBA.LocalObject
        implements DispatchRequest {
    //
    // The POA
    //
    protected org.apache.yoko.orb.OBPortableServer.POA_impl poa_;

    //
    // The ObjectId
    //
    protected byte[] oid_;

    //
    // The Upcall
    //
    protected Upcall upcall_;

    // ------------------------------------------------------------------
    // DispatchRequest_impl public member implementation
    // ------------------------------------------------------------------

    public DispatchRequest_impl(
            org.apache.yoko.orb.OBPortableServer.POA_impl poa, byte[] oid,
            Upcall upcall) {
        poa_ = poa;
        oid_ = oid;
        upcall_ = upcall;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public void invoke() {
        poa_._OB_dispatch(oid_, upcall_);

        upcall_ = null;
    }
}

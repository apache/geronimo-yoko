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

package org.apache.yoko.orb.OBPortableInterceptor;

import org.apache.yoko.orb.OBPortableInterceptor.IMRORT;

//
// The IMR ObjectReferenceTemplate
//
final public class IMRORT_impl extends IMRORT {
    // ------------------------------------------------------------------
    // Public member implementations
    // ------------------------------------------------------------------

    public IMRORT_impl() {
    }

    public IMRORT_impl(String serverId, String[] adapterName,
            org.omg.PortableInterceptor.ObjectReferenceTemplate realTemplate) {
        the_server_id = serverId;
        the_adapter_name = adapterName;
        the_real_template = realTemplate;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java mapping
    // ------------------------------------------------------------------

    public String server_id() {
        return the_real_template.server_id();
    }

    public String orb_id() {
        return the_real_template.orb_id();
    }

    public String[] adapter_name() {
        return the_real_template.adapter_name();
    }

    public org.omg.CORBA.Object make_object(String repoid, byte[] id) {
        //
        // Create the Yoko IMR ObjectKey
        //
        String[] poaId = the_adapter_name;

        org.apache.yoko.orb.OB.ObjectKeyData obkey = new org.apache.yoko.orb.OB.ObjectKeyData();
        obkey.serverId = the_server_id;
        obkey.poaId = new String[poaId.length];
        System.arraycopy(poaId, 0, obkey.poaId, 0, poaId.length);
        obkey.oid = id;
        obkey.persistent = true;
        obkey.createTime = 0;

        //
        // CreatePersistentObjectKey/CreateTransientObjectKey instead of
        // populating this ObjectKey data to avoid the copy?
        //
        byte[] key = org.apache.yoko.orb.OB.ObjectKey.CreateObjectKey(obkey);

        //
        // Create and return reference using the real template
        //
        return the_real_template.make_object(repoid, key);
    }
}

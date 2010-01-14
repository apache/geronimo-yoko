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
// Create a new system generated object id. This is used for SYSTEM_ID
// POA. It is necessary to identify ObjectId as belonging to a
// SYSTEM_ID POA to prevent the user from calling
// active_object_with_id and create_reference_with_id with ObjectId's
// not generated from this POA.
// 
// Our system generated object-id's are 12 octet:
//
// The first 4 bytes are magic (0xcafebabe)
// The next 4 bytes are the creation timestamp of the POA
// The next 4 bytes is a running counter.
//
class SystemIdGenerationStrategy implements IdGenerationStrategy {
    int id_;

    //
    // Is the POA persistent?
    //
    boolean persistent_;

    //
    // The create time
    //
    int createTime_;

    private static final byte[] SystemGeneratedMagic = { (byte) 0xca,
            (byte) 0xfe, (byte) 0xba, (byte) 0xbe };

    SystemIdGenerationStrategy(boolean persistent) {
        id_ = 0;
        persistent_ = persistent;
        createTime_ = (int) (System.currentTimeMillis() / 1000);
    }

    public byte[] createId()
            throws org.omg.PortableServer.POAPackage.WrongPolicy {
        byte[] oid = new byte[12];
        int pos = 0;

        //
        // Copy the magic number
        //
        System.arraycopy(SystemGeneratedMagic, 0, oid, pos, 4);
        pos += 4;

        //
        // Copy the POA create time
        //
        oid[pos++] = (byte) (createTime_ >>> 24);
        oid[pos++] = (byte) (createTime_ >>> 16);
        oid[pos++] = (byte) (createTime_ >>> 8);
        oid[pos++] = (byte) createTime_;

        //
        // Copy the system id
        //
        int currId;
        synchronized (this) {
            currId = id_++;
        }
        oid[pos++] = (byte) (currId >>> 24);
        oid[pos++] = (byte) (currId >>> 16);
        oid[pos++] = (byte) (currId >>> 8);
        oid[pos++] = (byte) currId;

        org.apache.yoko.orb.OB.Assert._OB_assert(pos == oid.length);

        return oid;
    }

    public boolean isValid(byte[] oid) {
        int pos = 0;
        for (pos = 0; pos < SystemGeneratedMagic.length; pos++)
            if (oid[pos] != SystemGeneratedMagic[pos])
                return false;

        int t = (oid[pos++] << 24) | ((oid[pos++] << 16) & 0xff0000)
                | ((oid[pos++] << 8) & 0xff00) | (oid[pos++] & 0xff);
        if (persistent_) {
            if (t > createTime_)
                return false;
        } else if (createTime_ != t)
            return false;

        return true;
    }
}

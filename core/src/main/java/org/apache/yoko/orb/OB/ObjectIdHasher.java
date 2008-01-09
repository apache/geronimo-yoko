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
// All connected implementations
//
public class ObjectIdHasher {
    private byte[] oid_;

    private int hashCode_;

    public ObjectIdHasher(byte[] oid) {
        oid_ = oid;

        hashCode_ = 0;
        int offset = 0;
        int len = oid.length;

        if (len < 16) {
            for (int i = len; i > 0; i--)
                hashCode_ = (hashCode_ * 37) + (int) oid[offset++];
        } else {
            int skip = len / 8;
            for (int i = len; i > 0; i -= skip, offset += skip)
                hashCode_ = (hashCode_ * 39) + (int) oid[offset];
        }
    }

    public byte[] getObjectId() {
        return oid_;
    }

    public int hashCode() {
        return hashCode_;
    }

    public boolean equals(java.lang.Object o) {
        ObjectIdHasher h = (ObjectIdHasher) o;

        return comp(oid_, h.oid_);
    }

    public static boolean comp(byte[] id1, byte[] id2) {
        if (id1.length != id2.length)
            return false;

        for (int i = 0; i < id1.length; i++)
            if (id1[i] != id2[i])
                return false;

        return true;
    }
}

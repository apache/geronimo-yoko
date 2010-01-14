/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.impl;

/**
 * Utility for writing IORs to DataOutput
 */

class IOR {
    String type_id;

    TaggedProfile[] profiles;

    static class TaggedProfile {
        int tag;

        byte[] profile_data;

        void read(org.omg.CORBA.portable.InputStream in) {
            tag = in.read_ulong();
            int len = in.read_ulong();
            profile_data = new byte[len];
            in.read_octet_array(profile_data, 0, len);
        }

        void write(org.omg.CORBA.portable.OutputStream out) {
            out.write_ulong(tag);
            out.write_ulong(profile_data.length);
            out.write_octet_array(profile_data, 0, profile_data.length);
        }

        void read(java.io.DataInput in) throws java.io.IOException {
            tag = in.readInt();
            int len = in.readInt();
            profile_data = new byte[len];
            in.readFully(profile_data, 0, len);
        }

        void write(java.io.DataOutput out) throws java.io.IOException {
            out.writeInt(tag);
            out.writeInt(profile_data.length);
            out.write(profile_data, 0, profile_data.length);
        }

    }

    void read(org.omg.CORBA.portable.InputStream in) {
        type_id = in.read_string();
        int len = in.read_ulong();
        profiles = new TaggedProfile[len];
        for (int i = 0; i < len; i++) {
            profiles[i] = new TaggedProfile();
            profiles[i].read(in);
        }
    }

    void write(org.omg.CORBA.portable.OutputStream out) {
        out.write_string(type_id);
        out.write_ulong(profiles.length);
        for (int i = 0; i < profiles.length; i++) {
            profiles[i].write(out);
        }
    }

    void read(java.io.DataInput in) {
        try {
            // read type id
            int strlen = in.readInt();
            byte[] data = new byte[strlen];
            in.readFully(data, 0, strlen);
            type_id = new String(data, 0, strlen - 1, "ISO-8859-1");

            // read profiles
            int len = in.readInt();
            profiles = new TaggedProfile[len];
            for (int i = 0; i < len; i++) {
                profiles[i] = new TaggedProfile();
                profiles[i].read(in);
            }
        } catch (java.io.IOException ex) {
            throw new Error("failed to marshal IOR", ex);
        }
    }

    void write(java.io.DataOutput out) {
        try {

            // write type id
            byte[] string = type_id.getBytes("ISO-8859-1");
            out.writeInt(string.length + 1);
            out.write(string, 0, string.length);
            out.write(0);

            // write profile info
            out.writeInt(profiles.length);
            for (int i = 0; i < profiles.length; i++) {
                profiles[i].write(out);
            }

        } catch (java.io.IOException ex) {
            throw new Error("failed to marshal IOR", ex);
        }
    }

}

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

package org.apache.yoko.orb.OCI;

//
// IDL:orb.yoko.apache.org/OCI/ProfileInfo:1.0
//
/**
 *
 * Basic information about an IOR profile. Profiles for specific
 * protocols contain additional data. (For example, an IIOP profile
 * also contains a hostname and a port number.)
 *
 * @member key The object key.
 *
 * @member major The major version number of the ORB's protocol. (For
 * example, the major GIOP version, if the underlying ORB uses GIOP.)
 *
 * @member minor The minor version number of the ORB's protocol. (For
 * example, the minor GIOP version, if the underlying ORB uses GIOP.)
 *
 * @member id The id of the profile that contains this information.
 *
 * @member index The position index of this profile in an IOR.
 *
 * @member components A sequence of tagged components.
 *
 **/

final public class ProfileInfo implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:orb.yoko.apache.org/OCI/ProfileInfo:1.0";

    public
    ProfileInfo()
    {
    }

    public
    ProfileInfo(byte[] key,
                byte major,
                byte minor,
                int id,
                int index,
                org.omg.IOP.TaggedComponent[] components)
    {
        this.key = key;
        this.major = major;
        this.minor = minor;
        this.id = id;
        this.index = index;
        this.components = components;
    }

    public byte[] key;
    public byte major;
    public byte minor;
    public int id;
    public int index;
    public org.omg.IOP.TaggedComponent[] components;
}

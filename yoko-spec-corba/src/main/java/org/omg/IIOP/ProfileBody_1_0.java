/*
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
package org.omg.IIOP;

//
// IDL:omg.org/IIOP/ProfileBody_1_0:1.0
//
/***/

final public class ProfileBody_1_0 implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/IIOP/ProfileBody_1_0:1.0";

    public
    ProfileBody_1_0()
    {
    }

    public
    ProfileBody_1_0(Version iiop_version,
                    String host,
                    short port,
                    byte[] object_key)
    {
        this.iiop_version = iiop_version;
        this.host = host;
        this.port = port;
        this.object_key = object_key;
    }

    public Version iiop_version;
    public String host;
    public short port;
    public byte[] object_key;
}

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
package org.omg.CORBA;

//
// IDL:omg.org/CORBA/ServiceDetail:1.0
//
/***/

final public class ServiceDetail implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/CORBA/ServiceDetail:1.0";

    public
    ServiceDetail()
    {
    }

    public
    ServiceDetail(int service_detail_type,
                  byte[] service_detail)
    {
        this.service_detail_type = service_detail_type;
        this.service_detail = service_detail;
    }

    public int service_detail_type;
    public byte[] service_detail;
}

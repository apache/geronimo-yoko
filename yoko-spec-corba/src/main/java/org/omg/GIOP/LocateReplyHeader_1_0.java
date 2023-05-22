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
package org.omg.GIOP;

//
// IDL:omg.org/GIOP/LocateReplyHeader_1_0:1.0
//
/***/

final public class LocateReplyHeader_1_0 implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/GIOP/LocateReplyHeader_1_0:1.0";

    public
    LocateReplyHeader_1_0()
    {
    }

    public
    LocateReplyHeader_1_0(int request_id,
                          LocateStatusType_1_2 locate_status)
    {
        this.request_id = request_id;
        this.locate_status = locate_status;
    }

    public int request_id;
    public LocateStatusType_1_2 locate_status;
}

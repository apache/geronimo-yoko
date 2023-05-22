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
package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/RequestInfo:1.0
//
/***/

final public class RequestInfo implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/MessageRouting/RequestInfo:1.0";

    public
    RequestInfo()
    {
    }

    public
    RequestInfo(Router[] visited,
                Router[] to_visit,
                org.omg.CORBA.Object target,
                short profile_index,
                ReplyDestination reply_destination,
                org.omg.Messaging.PolicyValue[] selected_qos,
                RequestMessage payload)
    {
        this.visited = visited;
        this.to_visit = to_visit;
        this.target = target;
        this.profile_index = profile_index;
        this.reply_destination = reply_destination;
        this.selected_qos = selected_qos;
        this.payload = payload;
    }

    public Router[] visited;
    public Router[] to_visit;
    public org.omg.CORBA.Object target;
    public short profile_index;
    public ReplyDestination reply_destination;
    public org.omg.Messaging.PolicyValue[] selected_qos;
    public RequestMessage payload;
}

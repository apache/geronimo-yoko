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
// IDL:omg.org/MessageRouting/UntypedReplyHandler:1.0
//
/***/

public interface UntypedReplyHandlerOperations extends org.omg.Messaging.ReplyHandlerOperations
{
    //
    // IDL:omg.org/MessageRouting/UntypedReplyHandler/reply:1.0
    //
    /***/

    void
    reply(String operation_name,
          org.omg.GIOP.ReplyStatusType_1_2 reply_type,
          MessageBody reply_body);
}

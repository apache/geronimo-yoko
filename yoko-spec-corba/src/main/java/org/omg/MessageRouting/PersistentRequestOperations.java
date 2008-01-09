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

package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/PersistentRequest:1.0
//
/***/

public interface PersistentRequestOperations
{
    //
    // IDL:omg.org/MessageRouting/PersistentRequest/reply_available:1.0
    //
    /***/

    boolean
    reply_available();

    //
    // IDL:omg.org/MessageRouting/PersistentRequest/get_reply:1.0
    //
    /***/

    org.omg.GIOP.ReplyStatusType_1_2
    get_reply(boolean blocking,
              int timeout,
              MessageBodyHolder reply_body)
        throws ReplyNotAvailable;

    //
    // IDL:omg.org/MessageRouting/PersistentRequest/associated_handler:1.0
    //
    /***/

    org.omg.Messaging.ReplyHandler
    associated_handler();

    void
    associated_handler(org.omg.Messaging.ReplyHandler val);
}

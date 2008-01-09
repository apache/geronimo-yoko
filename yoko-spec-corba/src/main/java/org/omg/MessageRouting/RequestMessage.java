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
// IDL:omg.org/MessageRouting/RequestMessage:1.0
//
/***/

final public class RequestMessage implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/MessageRouting/RequestMessage:1.0";

    public
    RequestMessage()
    {
    }

    public
    RequestMessage(org.omg.GIOP.Version giop_version,
                   org.omg.IOP.ServiceContext[] service_contexts,
                   byte response_flags,
                   byte[] reserved,
                   byte[] object_key,
                   String operation,
                   MessageBody body)
    {
        this.giop_version = giop_version;
        this.service_contexts = service_contexts;
        this.response_flags = response_flags;
        this.reserved = reserved;
        this.object_key = object_key;
        this.operation = operation;
        this.body = body;
    }

    public org.omg.GIOP.Version giop_version;
    public org.omg.IOP.ServiceContext[] service_contexts;
    public byte response_flags;
    public byte[] reserved;
    public byte[] object_key;
    public String operation;
    public MessageBody body;
}

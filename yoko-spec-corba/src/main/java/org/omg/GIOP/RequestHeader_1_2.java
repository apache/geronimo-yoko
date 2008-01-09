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

package org.omg.GIOP;

//
// IDL:omg.org/GIOP/RequestHeader_1_2:1.0
//
/***/

final public class RequestHeader_1_2 implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/GIOP/RequestHeader_1_2:1.0";

    public
    RequestHeader_1_2()
    {
    }

    public
    RequestHeader_1_2(int request_id,
                      byte response_flags,
                      byte[] reserved,
                      TargetAddress target,
                      String operation,
                      org.omg.IOP.ServiceContext[] service_context)
    {
        this.request_id = request_id;
        this.response_flags = response_flags;
        this.reserved = reserved;
        this.target = target;
        this.operation = operation;
        this.service_context = service_context;
    }

    public int request_id;
    public byte response_flags;
    public byte[] reserved;
    public TargetAddress target;
    public String operation;
    public org.omg.IOP.ServiceContext[] service_context;
}

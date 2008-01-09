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
// IDL:omg.org/GIOP/RequestHeader_1_1:1.0
//
/***/

final public class RequestHeader_1_1 implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/GIOP/RequestHeader_1_1:1.0";

    public
    RequestHeader_1_1()
    {
    }

    public
    RequestHeader_1_1(org.omg.IOP.ServiceContext[] service_context,
                      int request_id,
                      boolean response_expected,
                      byte[] reserved,
                      byte[] object_key,
                      String operation,
                      org.omg.CORBA.Principal requesting_principal)
    {
        this.service_context = service_context;
        this.request_id = request_id;
        this.response_expected = response_expected;
        this.reserved = reserved;
        this.object_key = object_key;
        this.operation = operation;
        this.requesting_principal = requesting_principal;
    }

    public org.omg.IOP.ServiceContext[] service_context;
    public int request_id;
    public boolean response_expected;
    public byte[] reserved;
    public byte[] object_key;
    public String operation;
    public org.omg.CORBA.Principal requesting_principal;
}

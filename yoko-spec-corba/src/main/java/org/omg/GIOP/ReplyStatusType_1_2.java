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
// IDL:omg.org/GIOP/ReplyStatusType_1_2:1.0
//
/***/

public class ReplyStatusType_1_2 implements org.omg.CORBA.portable.IDLEntity
{
    private static ReplyStatusType_1_2 [] values_ = new ReplyStatusType_1_2[6];
    private int value_;

    public final static int _NO_EXCEPTION = 0;
    public final static ReplyStatusType_1_2 NO_EXCEPTION = new ReplyStatusType_1_2(_NO_EXCEPTION);
    public final static int _USER_EXCEPTION = 1;
    public final static ReplyStatusType_1_2 USER_EXCEPTION = new ReplyStatusType_1_2(_USER_EXCEPTION);
    public final static int _SYSTEM_EXCEPTION = 2;
    public final static ReplyStatusType_1_2 SYSTEM_EXCEPTION = new ReplyStatusType_1_2(_SYSTEM_EXCEPTION);
    public final static int _LOCATION_FORWARD = 3;
    public final static ReplyStatusType_1_2 LOCATION_FORWARD = new ReplyStatusType_1_2(_LOCATION_FORWARD);
    public final static int _LOCATION_FORWARD_PERM = 4;
    public final static ReplyStatusType_1_2 LOCATION_FORWARD_PERM = new ReplyStatusType_1_2(_LOCATION_FORWARD_PERM);
    public final static int _NEEDS_ADDRESSING_MODE = 5;
    public final static ReplyStatusType_1_2 NEEDS_ADDRESSING_MODE = new ReplyStatusType_1_2(_NEEDS_ADDRESSING_MODE);

    protected
    ReplyStatusType_1_2(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static ReplyStatusType_1_2
    from_int(int value)
    {
        if(value < values_.length)
            return values_[value];
        else
            throw new org.omg.CORBA.BAD_PARAM("Value (" + value  + ") out of range", 25, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private java.lang.Object
    readResolve()
        throws java.io.ObjectStreamException
    {
        return from_int(value());
    }
}

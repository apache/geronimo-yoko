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
// IDL:omg.org/GIOP/LocateStatusType_1_2:1.0
//
/***/

public class LocateStatusType_1_2 implements org.omg.CORBA.portable.IDLEntity
{
    private static LocateStatusType_1_2 [] values_ = new LocateStatusType_1_2[6];
    private int value_;

    public final static int _UNKNOWN_OBJECT = 0;
    public final static LocateStatusType_1_2 UNKNOWN_OBJECT = new LocateStatusType_1_2(_UNKNOWN_OBJECT);
    public final static int _OBJECT_HERE = 1;
    public final static LocateStatusType_1_2 OBJECT_HERE = new LocateStatusType_1_2(_OBJECT_HERE);
    public final static int _OBJECT_FORWARD = 2;
    public final static LocateStatusType_1_2 OBJECT_FORWARD = new LocateStatusType_1_2(_OBJECT_FORWARD);
    public final static int _OBJECT_FORWARD_PERM = 3;
    public final static LocateStatusType_1_2 OBJECT_FORWARD_PERM = new LocateStatusType_1_2(_OBJECT_FORWARD_PERM);
    public final static int _LOC_SYSTEM_EXCEPTION = 4;
    public final static LocateStatusType_1_2 LOC_SYSTEM_EXCEPTION = new LocateStatusType_1_2(_LOC_SYSTEM_EXCEPTION);
    public final static int _LOC_NEEDS_ADDRESSING_MODE = 5;
    public final static LocateStatusType_1_2 LOC_NEEDS_ADDRESSING_MODE = new LocateStatusType_1_2(_LOC_NEEDS_ADDRESSING_MODE);

    protected
    LocateStatusType_1_2(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static LocateStatusType_1_2
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

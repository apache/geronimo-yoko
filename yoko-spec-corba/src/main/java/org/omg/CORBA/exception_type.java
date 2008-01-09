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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/exception_type:1.0
//
/***/

public class exception_type implements org.omg.CORBA.portable.IDLEntity
{
    private static exception_type [] values_ = new exception_type[3];
    private int value_;

    public final static int _NO_EXCEPTION = 0;
    public final static exception_type NO_EXCEPTION = new exception_type(_NO_EXCEPTION);
    public final static int _USER_EXCEPTION = 1;
    public final static exception_type USER_EXCEPTION = new exception_type(_USER_EXCEPTION);
    public final static int _SYSTEM_EXCEPTION = 2;
    public final static exception_type SYSTEM_EXCEPTION = new exception_type(_SYSTEM_EXCEPTION);

    protected
    exception_type(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static exception_type
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

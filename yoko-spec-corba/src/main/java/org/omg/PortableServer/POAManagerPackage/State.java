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

package org.omg.PortableServer.POAManagerPackage;

//
// IDL:omg.org/PortableServer/POAManager/State:1.0
//
/***/

public class State implements org.omg.CORBA.portable.IDLEntity
{
    private static State [] values_ = new State[4];
    private int value_;

    public final static int _HOLDING = 0;
    public final static State HOLDING = new State(_HOLDING);
    public final static int _ACTIVE = 1;
    public final static State ACTIVE = new State(_ACTIVE);
    public final static int _DISCARDING = 2;
    public final static State DISCARDING = new State(_DISCARDING);
    public final static int _INACTIVE = 3;
    public final static State INACTIVE = new State(_INACTIVE);

    protected
    State(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static State
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

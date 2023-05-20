/*
 * Copyright 2010 IBM Corporation and others.
 *
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
package ORBTest;

//
// IDL:ORBTest/ORBType:1.0
//
/***/

public class ORBType implements org.omg.CORBA.portable.IDLEntity
{
    private static ORBType [] values_ = new ORBType[5];
    private int value_;

    public final static int _ORBacus3 = 0;
    public final static ORBType ORBacus3 = new ORBType(_ORBacus3);
    public final static int _ORBacus4 = 1;
    public final static ORBType ORBacus4 = new ORBType(_ORBacus4);
    public final static int _OrbixE = 2;
    public final static ORBType OrbixE = new ORBType(_OrbixE);
    public final static int _Orbix3 = 3;
    public final static ORBType Orbix3 = new ORBType(_Orbix3);
    public final static int _Orbix2000 = 4;
    public final static ORBType Orbix2000 = new ORBType(_Orbix2000);

    protected
    ORBType(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static ORBType
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

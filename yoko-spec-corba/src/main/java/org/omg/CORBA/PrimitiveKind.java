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
// IDL:omg.org/CORBA/PrimitiveKind:1.0
//
/***/

public class PrimitiveKind implements org.omg.CORBA.portable.IDLEntity
{
    private static PrimitiveKind [] values_ = new PrimitiveKind[22];
    private int value_;

    public final static int _pk_null = 0;
    public final static PrimitiveKind pk_null = new PrimitiveKind(_pk_null);
    public final static int _pk_void = 1;
    public final static PrimitiveKind pk_void = new PrimitiveKind(_pk_void);
    public final static int _pk_short = 2;
    public final static PrimitiveKind pk_short = new PrimitiveKind(_pk_short);
    public final static int _pk_long = 3;
    public final static PrimitiveKind pk_long = new PrimitiveKind(_pk_long);
    public final static int _pk_ushort = 4;
    public final static PrimitiveKind pk_ushort = new PrimitiveKind(_pk_ushort);
    public final static int _pk_ulong = 5;
    public final static PrimitiveKind pk_ulong = new PrimitiveKind(_pk_ulong);
    public final static int _pk_float = 6;
    public final static PrimitiveKind pk_float = new PrimitiveKind(_pk_float);
    public final static int _pk_double = 7;
    public final static PrimitiveKind pk_double = new PrimitiveKind(_pk_double);
    public final static int _pk_boolean = 8;
    public final static PrimitiveKind pk_boolean = new PrimitiveKind(_pk_boolean);
    public final static int _pk_char = 9;
    public final static PrimitiveKind pk_char = new PrimitiveKind(_pk_char);
    public final static int _pk_octet = 10;
    public final static PrimitiveKind pk_octet = new PrimitiveKind(_pk_octet);
    public final static int _pk_any = 11;
    public final static PrimitiveKind pk_any = new PrimitiveKind(_pk_any);
    public final static int _pk_TypeCode = 12;
    public final static PrimitiveKind pk_TypeCode = new PrimitiveKind(_pk_TypeCode);
    public final static int _pk_Principal = 13;
    public final static PrimitiveKind pk_Principal = new PrimitiveKind(_pk_Principal);
    public final static int _pk_string = 14;
    public final static PrimitiveKind pk_string = new PrimitiveKind(_pk_string);
    public final static int _pk_objref = 15;
    public final static PrimitiveKind pk_objref = new PrimitiveKind(_pk_objref);
    public final static int _pk_longlong = 16;
    public final static PrimitiveKind pk_longlong = new PrimitiveKind(_pk_longlong);
    public final static int _pk_ulonglong = 17;
    public final static PrimitiveKind pk_ulonglong = new PrimitiveKind(_pk_ulonglong);
    public final static int _pk_longdouble = 18;
    public final static PrimitiveKind pk_longdouble = new PrimitiveKind(_pk_longdouble);
    public final static int _pk_wchar = 19;
    public final static PrimitiveKind pk_wchar = new PrimitiveKind(_pk_wchar);
    public final static int _pk_wstring = 20;
    public final static PrimitiveKind pk_wstring = new PrimitiveKind(_pk_wstring);
    public final static int _pk_value_base = 21;
    public final static PrimitiveKind pk_value_base = new PrimitiveKind(_pk_value_base);

    protected
    PrimitiveKind(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static PrimitiveKind
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

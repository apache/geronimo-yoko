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

package org.omg.Messaging;

//
// IDL:omg.org/Messaging/ExceptionHolder:1.0
//
/***/

public abstract class _ExceptionHolder implements org.omg.CORBA.portable.StreamableValue
{
    //
    // IDL:omg.org/Messaging/ExceptionHolder/raise_exception:1.0
    //
    /***/

    public abstract void
    raise_exception()
        throws java.lang.Exception;

    //
    // IDL:omg.org/Messaging/ExceptionHolder/raise_exception_with_list:1.0
    //
    /***/

    public abstract void
    raise_exception_with_list(org.omg.CORBA.TypeCode[] exc_list)
        throws java.lang.Exception;

    //
    // IDL:omg.org/Messaging/ExceptionHolder/is_system_exception:1.0
    //
    /***/

    protected boolean is_system_exception;

    //
    // IDL:omg.org/Messaging/ExceptionHolder/byte_order:1.0
    //
    /***/

    protected boolean byte_order;

    //
    // IDL:omg.org/Messaging/ExceptionHolder/marshaled_exception:1.0
    //
    /***/

    protected byte[] marshaled_exception;

    private static String[] _OB_truncatableIds_ =
    {
        _ExceptionHolderHelper.id()
    };

    public String[]
    _truncatable_ids()
    {
        return _OB_truncatableIds_;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        is_system_exception = in.read_boolean();
        byte_order = in.read_boolean();
        int len0 = in.read_ulong();
        marshaled_exception = new byte[len0];
        in.read_octet_array(marshaled_exception, 0, len0);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        out.write_boolean(is_system_exception);
        out.write_boolean(byte_order);
        int len0 = marshaled_exception.length;
        out.write_ulong(len0);
        out.write_octet_array(marshaled_exception, 0, len0);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return _ExceptionHolderHelper.type();
    }
}

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
// **********************************************************************

// Version: 4.3.1

package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/UnlimitedPing:1.0
//
/***/

public abstract class UnlimitedPing extends RetryPolicy
{
    //
    // IDL:omg.org/MessageRouting/UnlimitedPing/max_backoffs:1.0
    //
    /***/

    public short max_backoffs;

    //
    // IDL:omg.org/MessageRouting/UnlimitedPing/backoff_factor:1.0
    //
    /***/

    public float backoff_factor;

    //
    // IDL:omg.org/MessageRouting/UnlimitedPing/base_interval_seconds:1.0
    //
    /***/

    public int base_interval_seconds;

    private static String[] _OB_truncatableIds_ =
    {
        UnlimitedPingHelper.id()
    };

    public String[]
    _truncatable_ids()
    {
        return _OB_truncatableIds_;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        super._read(in);
        max_backoffs = in.read_short();
        backoff_factor = in.read_float();
        base_interval_seconds = in.read_ulong();
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        super._write(out);
        out.write_short(max_backoffs);
        out.write_float(backoff_factor);
        out.write_ulong(base_interval_seconds);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return UnlimitedPingHelper.type();
    }
}

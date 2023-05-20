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
package test.types.DynAnyTypes;

//
// IDL:test/types/DynAnyTypes/TestValue2:1.0
//
/***/

public abstract class TestValue2 implements org.omg.CORBA.portable.StreamableValue
{
    //
    // IDL:test/types/DynAnyTypes/TestValue2/shortVal:1.0
    //
    /***/

    public short shortVal;

    //
    // IDL:test/types/DynAnyTypes/TestValue2/longVal:1.0
    //
    /***/

    public int longVal;

    //
    // IDL:test/types/DynAnyTypes/TestValue2/stringVal:1.0
    //
    /***/

    public String stringVal;

    private static String[] _OB_truncatableIds_ =
    {
        TestValue2Helper.id()
    };

    public String[]
    _truncatable_ids()
    {
        return _OB_truncatableIds_;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        shortVal = in.read_short();
        longVal = in.read_long();
        stringVal = in.read_string();
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        out.write_short(shortVal);
        out.write_long(longVal);
        out.write_string(stringVal);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TestValue2Helper.type();
    }
}

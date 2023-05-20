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
// IDL:test/types/DynAnyTypes/TestValue4:1.0
//
/***/

public abstract class TestValue4 extends TestValue2
{
    //
    // IDL:test/types/DynAnyTypes/TestValue4/charVal:1.0
    //
    /***/

    public char charVal;

    //
    // IDL:test/types/DynAnyTypes/TestValue4/longlongVal:1.0
    //
    /***/

    public long longlongVal;

    private static String[] _OB_truncatableIds_ =
    {
        TestValue4Helper.id(),
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
        super._read(in);
        charVal = in.read_char();
        longlongVal = in.read_longlong();
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        super._write(out);
        out.write_char(charVal);
        out.write_longlong(longlongVal);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TestValue4Helper.type();
    }
}

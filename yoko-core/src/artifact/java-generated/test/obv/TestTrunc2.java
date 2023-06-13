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
package test.obv;

//
// IDL:TestTrunc2:1.0
//
/***/

public abstract class TestTrunc2 extends TestTruncBase
{
    //
    // IDL:TestTrunc2/t:1.0
    //
    /***/

    public TestTruncBase t;

    //
    // IDL:TestTrunc2/a:1.0
    //
    /***/

    public TestAbsValue1 a;

    //
    // IDL:TestTrunc2/v:1.0
    //
    /***/

    public TestValue v;

    //
    // IDL:TestTrunc2/b:1.0
    //
    /***/

    public TestTruncBase b;

    private static String[] _OB_truncatableIds_ =
    {
        TestTrunc2Helper.id(),
        TestTruncBaseHelper.id()
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
        t = TestTruncBaseHelper.read(in);
        a = TestAbsValue1Helper.read(in);
        v = TestValueHelper.read(in);
        b = TestTruncBaseHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        super._write(out);
        TestTruncBaseHelper.write(out, t);
        TestAbsValue1Helper.write(out, a);
        TestValueHelper.write(out, v);
        TestTruncBaseHelper.write(out, b);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TestTrunc2Helper.type();
    }
}

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
// IDL:TestTrunc1:1.0
//
/***/

public abstract class TestTrunc1 extends TestTruncBase
{
    //
    // IDL:TestTrunc1/boolVal:1.0
    //
    /***/

    public boolean boolVal;

    //
    // IDL:TestTrunc1/v:1.0
    //
    /***/

    public TestAbsValue1 v;

    //
    // IDL:TestTrunc1/shortVal:1.0
    //
    /***/

    public short shortVal;

    private static String[] _OB_truncatableIds_ =
    {
        TestTrunc1Helper.id(),
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
        boolVal = in.read_boolean();
        v = TestAbsValue1Helper.read(in);
        shortVal = in.read_short();
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        super._write(out);
        out.write_boolean(boolVal);
        TestAbsValue1Helper.write(out, v);
        out.write_short(shortVal);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TestTrunc1Helper.type();
    }
}

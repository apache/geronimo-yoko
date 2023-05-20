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
// IDL:TestNode:1.0
//
/***/

public abstract class TestNode implements org.omg.CORBA.portable.StreamableValue
{
    //
    // IDL:TestNode/left:1.0
    //
    /***/

    public TestNode left;

    //
    // IDL:TestNode/right:1.0
    //
    /***/

    public TestNode right;

    //
    // IDL:TestNode/count:1.0
    //
    /***/

    protected int count;

    //
    // IDL:TestNode/compute_count:1.0
    //
    /***/

    public abstract int
    compute_count();

    private static String[] _OB_truncatableIds_ =
    {
        TestNodeHelper.id()
    };

    public String[]
    _truncatable_ids()
    {
        return _OB_truncatableIds_;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        left = TestNodeHelper.read(in);
        right = TestNodeHelper.read(in);
        count = in.read_ulong();
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        TestNodeHelper.write(out, left);
        TestNodeHelper.write(out, right);
        out.write_ulong(count);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TestNodeHelper.type();
    }
}

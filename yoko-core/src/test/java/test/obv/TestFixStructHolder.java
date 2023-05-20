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
// IDL:TestFixStruct:1.0
//
final public class TestFixStructHolder implements org.omg.CORBA.portable.Streamable
{
    public TestFixStruct value;

    public
    TestFixStructHolder()
    {
    }

    public
    TestFixStructHolder(TestFixStruct initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = TestFixStructHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        TestFixStructHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TestFixStructHelper.type();
    }
}

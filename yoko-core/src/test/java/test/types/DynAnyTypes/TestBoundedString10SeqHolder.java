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

package test.types.DynAnyTypes;

//
// IDL:test/types/DynAnyTypes/TestBoundedString10Seq:1.0
//
final public class TestBoundedString10SeqHolder implements org.omg.CORBA.portable.Streamable
{
    public String[] value;

    public
    TestBoundedString10SeqHolder()
    {
    }

    public
    TestBoundedString10SeqHolder(String[] initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = TestBoundedString10SeqHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        TestBoundedString10SeqHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TestBoundedString10SeqHelper.type();
    }
}

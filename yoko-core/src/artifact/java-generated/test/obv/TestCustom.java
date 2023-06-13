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
// IDL:TestCustom:1.0
//
/***/

public abstract class TestCustom implements org.omg.CORBA.portable.CustomValue,
                                            TestAbsValue1
{
    //
    // IDL:TestCustom/shortVal:1.0
    //
    /***/

    public short shortVal;

    //
    // IDL:TestCustom/longVal:1.0
    //
    /***/

    public int longVal;

    //
    // IDL:TestCustom/stringVal:1.0
    //
    /***/

    public String stringVal;

    //
    // IDL:TestCustom/doubleVal:1.0
    //
    /***/

    public double doubleVal;

    private static String[] _OB_truncatableIds_ =
    {
        TestCustomHelper.id()
    };

    public String[]
    _truncatable_ids()
    {
        return _OB_truncatableIds_;
    }
}

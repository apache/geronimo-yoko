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

package org.omg.DynamicAny;

//
// IDL:omg.org/DynamicAny/DynEnum:1.0
//
/***/

public interface DynEnumOperations extends DynAnyOperations
{
    //
    // IDL:omg.org/DynamicAny/DynEnum/get_as_string:1.0
    //
    /***/

    String
    get_as_string();

    //
    // IDL:omg.org/DynamicAny/DynEnum/set_as_string:1.0
    //
    /***/

    void
    set_as_string(String value)
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynEnum/get_as_ulong:1.0
    //
    /***/

    int
    get_as_ulong();

    //
    // IDL:omg.org/DynamicAny/DynEnum/set_as_ulong:1.0
    //
    /***/

    void
    set_as_ulong(int value)
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
}

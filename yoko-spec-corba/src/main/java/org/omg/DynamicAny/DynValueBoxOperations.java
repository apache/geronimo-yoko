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
// IDL:omg.org/DynamicAny/DynValueBox:1.0
//
/***/

public interface DynValueBoxOperations extends DynValueCommonOperations
{
    //
    // IDL:omg.org/DynamicAny/DynValueBox/get_boxed_value:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_boxed_value()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValueBox/set_boxed_value:1.0
    //
    /***/

    void
    set_boxed_value(org.omg.CORBA.Any boxed)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValueBox/get_boxed_value_as_dyn_any:1.0
    //
    /***/

    DynAny
    get_boxed_value_as_dyn_any()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValueBox/set_boxed_value_as_dyn_any:1.0
    //
    /***/

    void
    set_boxed_value_as_dyn_any(DynAny boxed)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
}

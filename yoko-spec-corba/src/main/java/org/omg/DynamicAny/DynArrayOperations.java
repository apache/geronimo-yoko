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
// IDL:omg.org/DynamicAny/DynArray:1.0
//
/***/

public interface DynArrayOperations extends DynAnyOperations
{
    //
    // IDL:omg.org/DynamicAny/DynArray/get_elements:1.0
    //
    /***/

    org.omg.CORBA.Any[]
    get_elements();

    //
    // IDL:omg.org/DynamicAny/DynArray/set_elements:1.0
    //
    /***/

    void
    set_elements(org.omg.CORBA.Any[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynArray/get_elements_as_dyn_any:1.0
    //
    /***/

    DynAny[]
    get_elements_as_dyn_any();

    //
    // IDL:omg.org/DynamicAny/DynArray/set_elements_as_dyn_any:1.0
    //
    /***/

    void
    set_elements_as_dyn_any(DynAny[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;
}

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
// IDL:omg.org/DynamicAny/DynValue:1.0
//
/***/

public interface DynValueOperations extends DynValueCommonOperations
{
    //
    // IDL:omg.org/DynamicAny/DynValue/current_member_name:1.0
    //
    /***/

    String
    current_member_name()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValue/current_member_kind:1.0
    //
    /***/

    org.omg.CORBA.TCKind
    current_member_kind()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValue/get_members:1.0
    //
    /***/

    NameValuePair[]
    get_members()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValue/set_members:1.0
    //
    /***/

    void
    set_members(NameValuePair[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValue/get_members_as_dyn_any:1.0
    //
    /***/

    NameDynAnyPair[]
    get_members_as_dyn_any()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynValue/set_members_as_dyn_any:1.0
    //
    /***/

    void
    set_members_as_dyn_any(NameDynAnyPair[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;
}

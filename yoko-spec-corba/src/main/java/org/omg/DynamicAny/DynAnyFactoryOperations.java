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
// IDL:omg.org/DynamicAny/DynAnyFactory:1.0
//
/***/

public interface DynAnyFactoryOperations
{
    //
    // IDL:omg.org/DynamicAny/DynAnyFactory/create_dyn_any:1.0
    //
    /***/

    DynAny
    create_dyn_any(org.omg.CORBA.Any value)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

    //
    // IDL:omg.org/DynamicAny/DynAnyFactory/create_dyn_any_from_type_code:1.0
    //
    /***/

    DynAny
    create_dyn_any_from_type_code(org.omg.CORBA.TypeCode type)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

    //
    // IDL:omg.org/DynamicAny/DynAnyFactory/create_dyn_any_without_truncation:1.0
    //
    /***/

    DynAny
    create_dyn_any_without_truncation(org.omg.CORBA.Any value)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode,
               MustTruncate;

    //
    // IDL:omg.org/DynamicAny/DynAnyFactory/create_multiple_dyn_anys:1.0
    //
    /***/

    DynAny[]
    create_multiple_dyn_anys(org.omg.CORBA.Any[] values,
                             boolean allow_truncate)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode,
               MustTruncate;

    //
    // IDL:omg.org/DynamicAny/DynAnyFactory/create_multiple_anys:1.0
    //
    /***/

    org.omg.CORBA.Any[]
    create_multiple_anys(DynAny[] values);
}

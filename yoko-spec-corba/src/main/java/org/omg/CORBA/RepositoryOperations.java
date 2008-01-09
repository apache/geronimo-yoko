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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/Repository:1.0
//
/***/

public interface RepositoryOperations extends ContainerOperations
{
    //
    // IDL:omg.org/CORBA/Repository/lookup_id:1.0
    //
    /***/

    Contained
    lookup_id(String search_id);

    //
    // IDL:omg.org/CORBA/Repository/get_canonical_typecode:1.0
    //
    /***/

    org.omg.CORBA.TypeCode
    get_canonical_typecode(org.omg.CORBA.TypeCode tc);

    //
    // IDL:omg.org/CORBA/Repository/get_primitive:1.0
    //
    /***/

    PrimitiveDef
    get_primitive(PrimitiveKind kind);

    //
    // IDL:omg.org/CORBA/Repository/create_string:1.0
    //
    /***/

    StringDef
    create_string(int bound);

    //
    // IDL:omg.org/CORBA/Repository/create_wstring:1.0
    //
    /***/

    WstringDef
    create_wstring(int bound);

    //
    // IDL:omg.org/CORBA/Repository/create_sequence:1.0
    //
    /***/

    SequenceDef
    create_sequence(int bound,
                    IDLType element_type);

    //
    // IDL:omg.org/CORBA/Repository/create_array:1.0
    //
    /***/

    ArrayDef
    create_array(int length,
                 IDLType element_type);

    //
    // IDL:omg.org/CORBA/Repository/create_fixed:1.0
    //
    /***/

    FixedDef
    create_fixed(short digits,
                 short scale);
}

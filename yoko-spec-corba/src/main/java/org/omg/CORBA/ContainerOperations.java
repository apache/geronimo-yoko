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
// IDL:omg.org/CORBA/Container:1.0
//
/***/

public interface ContainerOperations extends IRObjectOperations
{
    //
    // IDL:omg.org/CORBA/Container/lookup:1.0
    //
    /***/

    Contained
    lookup(String search_name);

    //
    // IDL:omg.org/CORBA/Container/contents:1.0
    //
    /***/

    Contained[]
    contents(DefinitionKind limit_type,
             boolean exclude_inherited);

    //
    // IDL:omg.org/CORBA/Container/lookup_name:1.0
    //
    /***/

    Contained[]
    lookup_name(String search_name,
                int levels_to_search,
                DefinitionKind limit_type,
                boolean exclude_inherited);

    //
    // IDL:omg.org/CORBA/Container/describe_contents:1.0
    //
    /***/

    org.omg.CORBA.ContainerPackage.Description[]
    describe_contents(DefinitionKind limit_type,
                      boolean exclude_inherited,
                      int max_returned_objs);

    //
    // IDL:omg.org/CORBA/Container/create_module:1.0
    //
    /***/

    ModuleDef
    create_module(String id,
                  String name,
                  String version);

    //
    // IDL:omg.org/CORBA/Container/create_constant:1.0
    //
    /***/

    ConstantDef
    create_constant(String id,
                    String name,
                    String version,
                    IDLType type,
                    org.omg.CORBA.Any value);

    //
    // IDL:omg.org/CORBA/Container/create_struct:1.0
    //
    /***/

    StructDef
    create_struct(String id,
                  String name,
                  String version,
                  StructMember[] members);

    //
    // IDL:omg.org/CORBA/Container/create_union:1.0
    //
    /***/

    UnionDef
    create_union(String id,
                 String name,
                 String version,
                 IDLType discriminator_type,
                 UnionMember[] members);

    //
    // IDL:omg.org/CORBA/Container/create_enum:1.0
    //
    /***/

    EnumDef
    create_enum(String id,
                String name,
                String version,
                String[] members);

    //
    // IDL:omg.org/CORBA/Container/create_alias:1.0
    //
    /***/

    AliasDef
    create_alias(String id,
                 String name,
                 String version,
                 IDLType original_type);

    //
    // IDL:omg.org/CORBA/Container/create_interface:1.0
    //
    /***/

    InterfaceDef
    create_interface(String id,
                     String name,
                     String version,
                     InterfaceDef[] base_interfaces);

    //
    // IDL:omg.org/CORBA/Container/create_abstract_interface:1.0
    //
    /***/

    AbstractInterfaceDef
    create_abstract_interface(String id,
                              String name,
                              String version,
                              AbstractInterfaceDef[] base_interfaces);

    //
    // IDL:omg.org/CORBA/Container/create_local_interface:1.0
    //
    /***/

    LocalInterfaceDef
    create_local_interface(String id,
                           String name,
                           String version,
                           InterfaceDef[] base_interfaces);

    //
    // IDL:omg.org/CORBA/Container/create_exception:1.0
    //
    /***/

    ExceptionDef
    create_exception(String id,
                     String name,
                     String version,
                     StructMember[] members);

    //
    // IDL:omg.org/CORBA/Container/create_value:1.0
    //
    /***/

    ValueDef
    create_value(String id,
                 String name,
                 String version,
                 boolean is_custom,
                 boolean is_abstract,
                 ValueDef base_value,
                 boolean is_truncatable,
                 ValueDef[] abstract_base_values,
                 InterfaceDef[] supported_interfaces,
                 Initializer[] initializers);

    //
    // IDL:omg.org/CORBA/Container/create_value_box:1.0
    //
    /***/

    ValueBoxDef
    create_value_box(String id,
                     String name,
                     String version,
                     IDLType original_type_def);

    //
    // IDL:omg.org/CORBA/Container/create_native:1.0
    //
    /***/

    NativeDef
    create_native(String id,
                  String name,
                  String version);
}

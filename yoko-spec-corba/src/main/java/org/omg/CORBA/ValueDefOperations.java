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
// IDL:omg.org/CORBA/ValueDef:1.0
//
/***/

public interface ValueDefOperations extends ContainerOperations,
                                            ContainedOperations,
                                            IDLTypeOperations
{
    //
    // IDL:omg.org/CORBA/ValueDef/supported_interfaces:1.0
    //
    /***/

    InterfaceDef[]
    supported_interfaces();

    void
    supported_interfaces(InterfaceDef[] val);

    //
    // IDL:omg.org/CORBA/ValueDef/initializers:1.0
    //
    /***/

    Initializer[]
    initializers();

    void
    initializers(Initializer[] val);

    //
    // IDL:omg.org/CORBA/ValueDef/base_value:1.0
    //
    /***/

    ValueDef
    base_value();

    void
    base_value(ValueDef val);

    //
    // IDL:omg.org/CORBA/ValueDef/abstract_base_values:1.0
    //
    /***/

    ValueDef[]
    abstract_base_values();

    void
    abstract_base_values(ValueDef[] val);

    //
    // IDL:omg.org/CORBA/ValueDef/is_abstract:1.0
    //
    /***/

    boolean
    is_abstract();

    void
    is_abstract(boolean val);

    //
    // IDL:omg.org/CORBA/ValueDef/is_custom:1.0
    //
    /***/

    boolean
    is_custom();

    void
    is_custom(boolean val);

    //
    // IDL:omg.org/CORBA/ValueDef/is_truncatable:1.0
    //
    /***/

    boolean
    is_truncatable();

    void
    is_truncatable(boolean val);

    //
    // IDL:omg.org/CORBA/ValueDef/is_a:1.0
    //
    /***/

    boolean
    is_a(String value_id);

    //
    // IDL:omg.org/CORBA/ValueDef/describe_value:1.0
    //
    /***/

    org.omg.CORBA.ValueDefPackage.FullValueDescription
    describe_value();

    //
    // IDL:omg.org/CORBA/ValueDef/create_value_member:1.0
    //
    /***/

    ValueMemberDef
    create_value_member(String id,
                        String name,
                        String version,
                        IDLType type,
                        short access);

    //
    // IDL:omg.org/CORBA/ValueDef/create_attribute:1.0
    //
    /***/

    AttributeDef
    create_attribute(String id,
                     String name,
                     String version,
                     IDLType type,
                     AttributeMode mode);

    //
    // IDL:omg.org/CORBA/ValueDef/create_operation:1.0
    //
    /***/

    OperationDef
    create_operation(String id,
                     String name,
                     String version,
                     IDLType result,
                     OperationMode mode,
                     ParameterDescription[] params,
                     ExceptionDef[] exceptions,
                     String[] contexts);

    //
    // IDL:omg.org/CORBA/ValueDef/_OB_create_operation:1.0
    //
    /***/

    OperationDef
    _OB_create_operation(String id,
                         String name,
                         String version,
                         IDLType result,
                         OperationMode mode,
                         ParameterDescription[] params,
                         ExceptionDef[] exceptions,
                         NativeDef[] native_exceptions,
                         String[] contexts);
}

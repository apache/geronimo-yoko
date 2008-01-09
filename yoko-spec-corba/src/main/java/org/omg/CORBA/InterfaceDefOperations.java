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
// IDL:omg.org/CORBA/InterfaceDef:1.0
//
/***/

public interface InterfaceDefOperations extends ContainerOperations,
                                                ContainedOperations,
                                                IDLTypeOperations
{
    //
    // IDL:omg.org/CORBA/InterfaceDef/base_interfaces:1.0
    //
    /***/

    InterfaceDef[]
    base_interfaces();

    void
    base_interfaces(InterfaceDef[] val);

    //
    // IDL:omg.org/CORBA/InterfaceDef/is_a:1.0
    //
    /***/

    boolean
    is_a(String interface_id);

    //
    // IDL:omg.org/CORBA/InterfaceDef/describe_interface:1.0
    //
    /***/

    org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescription
    describe_interface();

    //
    // IDL:omg.org/CORBA/InterfaceDef/create_attribute:1.0
    //
    /***/

    AttributeDef
    create_attribute(String id,
                     String name,
                     String version,
                     IDLType type,
                     AttributeMode mode);

    //
    // IDL:omg.org/CORBA/InterfaceDef/create_operation:1.0
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
}

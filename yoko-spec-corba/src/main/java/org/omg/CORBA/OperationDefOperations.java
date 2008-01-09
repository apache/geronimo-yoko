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
// IDL:omg.org/CORBA/OperationDef:1.0
//
/***/

public interface OperationDefOperations extends ContainedOperations
{
    //
    // IDL:omg.org/CORBA/OperationDef/result:1.0
    //
    /***/

    org.omg.CORBA.TypeCode
    result();

    //
    // IDL:omg.org/CORBA/OperationDef/result_def:1.0
    //
    /***/

    IDLType
    result_def();

    void
    result_def(IDLType val);

    //
    // IDL:omg.org/CORBA/OperationDef/params:1.0
    //
    /***/

    ParameterDescription[]
    params();

    void
    params(ParameterDescription[] val);

    //
    // IDL:omg.org/CORBA/OperationDef/mode:1.0
    //
    /***/

    OperationMode
    mode();

    void
    mode(OperationMode val);

    //
    // IDL:omg.org/CORBA/OperationDef/contexts:1.0
    //
    /***/

    String[]
    contexts();

    void
    contexts(String[] val);

    //
    // IDL:omg.org/CORBA/OperationDef/exceptions:1.0
    //
    /***/

    ExceptionDef[]
    exceptions();

    void
    exceptions(ExceptionDef[] val);

    //
    // IDL:omg.org/CORBA/OperationDef/native_exceptions:1.0
    //
    /***/

    NativeDef[]
    native_exceptions();

    void
    native_exceptions(NativeDef[] val);
}

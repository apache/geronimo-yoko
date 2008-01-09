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
// IDL:omg.org/CORBA/AttributeDef:1.0
//
/***/

public interface AttributeDefOperations extends ContainedOperations
{
    //
    // IDL:omg.org/CORBA/AttributeDef/type:1.0
    //
    /***/

    org.omg.CORBA.TypeCode
    type();

    //
    // IDL:omg.org/CORBA/AttributeDef/type_def:1.0
    //
    /***/

    IDLType
    type_def();

    void
    type_def(IDLType val);

    //
    // IDL:omg.org/CORBA/AttributeDef/mode:1.0
    //
    /***/

    AttributeMode
    mode();

    void
    mode(AttributeMode val);
}

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
// IDL:omg.org/CORBA/Contained:1.0
//
/***/

public interface ContainedOperations extends IRObjectOperations
{
    //
    // IDL:omg.org/CORBA/Contained/id:1.0
    //
    /***/

    String
    id();

    void
    id(String val);

    //
    // IDL:omg.org/CORBA/Contained/name:1.0
    //
    /***/

    String
    name();

    void
    name(String val);

    //
    // IDL:omg.org/CORBA/Contained/version:1.0
    //
    /***/

    String
    version();

    void
    version(String val);

    //
    // IDL:omg.org/CORBA/Contained/defined_in:1.0
    //
    /***/

    Container
    defined_in();

    //
    // IDL:omg.org/CORBA/Contained/absolute_name:1.0
    //
    /***/

    String
    absolute_name();

    //
    // IDL:omg.org/CORBA/Contained/containing_repository:1.0
    //
    /***/

    Repository
    containing_repository();

    //
    // IDL:omg.org/CORBA/Contained/describe:1.0
    //
    /***/

    org.omg.CORBA.ContainedPackage.Description
    describe();

    //
    // IDL:omg.org/CORBA/Contained/move:1.0
    //
    /***/

    void
    move(Container new_container,
         String new_name,
         String new_version);
}

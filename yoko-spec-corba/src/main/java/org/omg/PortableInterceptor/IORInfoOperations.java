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

package org.omg.PortableInterceptor;

//
// IDL:omg.org/PortableInterceptor/IORInfo:1.0
//
/***/

public interface IORInfoOperations
{
    //
    // IDL:omg.org/PortableInterceptor/IORInfo/get_effective_policy:1.0
    //
    /***/

    org.omg.CORBA.Policy
    get_effective_policy(int type);

    //
    // IDL:omg.org/PortableInterceptor/IORInfo/add_ior_component:1.0
    //
    /***/

    void
    add_ior_component(org.omg.IOP.TaggedComponent a_component);

    //
    // IDL:omg.org/PortableInterceptor/IORInfo/add_ior_component_to_profile:1.0
    //
    /***/

    void
    add_ior_component_to_profile(org.omg.IOP.TaggedComponent a_component,
                                 int profile_id);

    //
    // IDL:omg.org/PortableInterceptor/IORInfo/adapter_template:1.0
    //
    /***/

    ObjectReferenceTemplate
    adapter_template();

    //
    // IDL:omg.org/PortableInterceptor/IORInfo/current_factory:1.0
    //
    /***/

    ObjectReferenceFactory
    current_factory();

    void
    current_factory(ObjectReferenceFactory val);

    //
    // IDL:omg.org/PortableInterceptor/IORInfo/manager_id:1.0
    //
    /***/

    String
    manager_id();

    //
    // IDL:omg.org/PortableInterceptor/IORInfo/state:1.0
    //
    /***/

    short
    state();
}

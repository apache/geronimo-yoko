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

package org.omg.PortableServer;

//
// IDL:omg.org/PortableServer/POA:2.3
//
/***/

public interface POAOperations
{
    //
    // IDL:omg.org/PortableServer/POA/create_POA:1.0
    //
    /***/

    POA
    create_POA(String adapter_name,
               POAManager a_POAManager,
               org.omg.CORBA.Policy[] policies)
        throws org.omg.PortableServer.POAPackage.AdapterAlreadyExists,
               org.omg.PortableServer.POAPackage.InvalidPolicy;

    //
    // IDL:omg.org/PortableServer/POA/find_POA:1.0
    //
    /***/

    POA
    find_POA(String adapter_name,
             boolean activate_it)
        throws org.omg.PortableServer.POAPackage.AdapterNonExistent;

    //
    // IDL:omg.org/PortableServer/POA/destroy:1.0
    //
    /***/

    void
    destroy(boolean etherealize_objects,
            boolean wait_for_completion);

    //
    // IDL:omg.org/PortableServer/POA/create_thread_policy:1.0
    //
    /***/

    ThreadPolicy
    create_thread_policy(ThreadPolicyValue value);

    //
    // IDL:omg.org/PortableServer/POA/create_lifespan_policy:1.0
    //
    /***/

    LifespanPolicy
    create_lifespan_policy(LifespanPolicyValue value);

    //
    // IDL:omg.org/PortableServer/POA/create_id_uniqueness_policy:1.0
    //
    /***/

    IdUniquenessPolicy
    create_id_uniqueness_policy(IdUniquenessPolicyValue value);

    //
    // IDL:omg.org/PortableServer/POA/create_id_assignment_policy:1.0
    //
    /***/

    IdAssignmentPolicy
    create_id_assignment_policy(IdAssignmentPolicyValue value);

    //
    // IDL:omg.org/PortableServer/POA/create_implicit_activation_policy:1.0
    //
    /***/

    ImplicitActivationPolicy
    create_implicit_activation_policy(ImplicitActivationPolicyValue value);

    //
    // IDL:omg.org/PortableServer/POA/create_servant_retention_policy:1.0
    //
    /***/

    ServantRetentionPolicy
    create_servant_retention_policy(ServantRetentionPolicyValue value);

    //
    // IDL:omg.org/PortableServer/POA/create_request_processing_policy:1.0
    //
    /***/

    RequestProcessingPolicy
    create_request_processing_policy(RequestProcessingPolicyValue value);

    //
    // IDL:omg.org/PortableServer/POA/the_name:1.0
    //
    /***/

    String
    the_name();

    //
    // IDL:omg.org/PortableServer/POA/the_parent:1.0
    //
    /***/

    POA
    the_parent();

    //
    // IDL:omg.org/PortableServer/POA/the_children:1.0
    //
    /***/

    POA[]
    the_children();

    //
    // IDL:omg.org/PortableServer/POA/the_POAManager:1.0
    //
    /***/

    POAManager
    the_POAManager();

    //
    // IDL:omg.org/PortableServer/POA/the_POAManagerFactory:1.0
    //
    /***/

    POAManagerFactory
    the_POAManagerFactory();

    //
    // IDL:omg.org/PortableServer/POA/id:1.0
    //
    /***/

    byte[]
    id();

    //
    // IDL:omg.org/PortableServer/POA/the_activator:1.0
    //
    /***/

    AdapterActivator
    the_activator();

    void
    the_activator(AdapterActivator val);

    //
    // IDL:omg.org/PortableServer/POA/get_servant_manager:1.0
    //
    /***/

    ServantManager
    get_servant_manager()
        throws org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/set_servant_manager:1.0
    //
    /***/

    void
    set_servant_manager(ServantManager imgr)
        throws org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/get_servant:1.0
    //
    /***/

    Servant
    get_servant()
        throws org.omg.PortableServer.POAPackage.NoServant,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/set_servant:1.0
    //
    /***/

    void
    set_servant(Servant p_servant)
        throws org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/activate_object:1.0
    //
    /***/

    byte[]
    activate_object(Servant p_servant)
        throws org.omg.PortableServer.POAPackage.ServantAlreadyActive,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/activate_object_with_id:1.0
    //
    /***/

    void
    activate_object_with_id(byte[] id,
                            Servant p_servant)
        throws org.omg.PortableServer.POAPackage.ServantAlreadyActive,
               org.omg.PortableServer.POAPackage.ObjectAlreadyActive,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/deactivate_object:1.0
    //
    /***/

    void
    deactivate_object(byte[] oid)
        throws org.omg.PortableServer.POAPackage.ObjectNotActive,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/create_reference:1.0
    //
    /***/

    org.omg.CORBA.Object
    create_reference(String intf)
        throws org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/create_reference_with_id:1.0
    //
    /***/

    org.omg.CORBA.Object
    create_reference_with_id(byte[] oid,
                             String intf);

    //
    // IDL:omg.org/PortableServer/POA/servant_to_id:1.0
    //
    /***/

    byte[]
    servant_to_id(Servant p_servant)
        throws org.omg.PortableServer.POAPackage.ServantNotActive,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/servant_to_reference:1.0
    //
    /***/

    org.omg.CORBA.Object
    servant_to_reference(Servant p_servant)
        throws org.omg.PortableServer.POAPackage.ServantNotActive,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/reference_to_servant:1.0
    //
    /***/

    Servant
    reference_to_servant(org.omg.CORBA.Object reference)
        throws org.omg.PortableServer.POAPackage.ObjectNotActive,
               org.omg.PortableServer.POAPackage.WrongAdapter,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/reference_to_id:1.0
    //
    /***/

    byte[]
    reference_to_id(org.omg.CORBA.Object reference)
        throws org.omg.PortableServer.POAPackage.WrongAdapter,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/id_to_servant:1.0
    //
    /***/

    Servant
    id_to_servant(byte[] oid)
        throws org.omg.PortableServer.POAPackage.ObjectNotActive,
               org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // IDL:omg.org/PortableServer/POA/id_to_reference:1.0
    //
    /***/

    org.omg.CORBA.Object
    id_to_reference(byte[] oid)
        throws org.omg.PortableServer.POAPackage.ObjectNotActive,
               org.omg.PortableServer.POAPackage.WrongPolicy;
}

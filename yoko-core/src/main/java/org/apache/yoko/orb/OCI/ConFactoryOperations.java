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

package org.apache.yoko.orb.OCI;

//
// IDL:orb.yoko.apache.org/OCI/ConFactory:1.0
//
/**
 *
 * A factory for Connector objects.
 *
 * @see Connector
 * @see ConFactoryRegistry
 *
 **/

public interface ConFactoryOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/ConFactory/id:1.0
    //
    /** The plugin id. */

    String
    id();

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactory/tag:1.0
    //
    /** The profile id tag. */

    int
    tag();

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactory/describe_profile:1.0
    //
    /**
     *
     * Returns a description of the given tagged profile.
     *
     * @param prof The tagged profile.
     *
     * @return The profile description.
     *
     **/

    String
    describe_profile(org.omg.IOP.TaggedProfile prof);

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactory/create_connectors:1.0
    //
    /**
     *
     * Returns a sequence of Connectors for a given IOR and a list of
     * policies. The sequence includes one or more Connectors for each
     * IOR profile that matches this Connector factory and satisfies
     * the list of policies.
     *
     * @param ref The IOR for which Connectors are returned.
     *
     * @param policies The policies that must be satisfied.
     *
     * @return The sequence of Connectors.
     *
     **/

    Connector[]
    create_connectors(org.omg.IOP.IOR ref,
                      org.omg.CORBA.Policy[] policies);

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactory/equivalent:1.0
    //
    /**
     *
     * Checks whether two IORs are equivalent, taking only profiles
     * into account matching this Connector factory.
     *
     * @param ior1 The first IOR to check for equivalence.
     *
     * @param ior2 The second IOR to check for equivalence.
     *
     * @return <code>TRUE</code> if the IORs are equivalent,
     * <code>FALSE</code> otherwise.
     *
     **/

    boolean
    equivalent(org.omg.IOP.IOR ior1,
               org.omg.IOP.IOR ior2);

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactory/hash:1.0
    //
    /**
     *
     * Calculates a hash value for an IOR.
     *
     * @param ref The IOR to calculate a hash value for.
     *
     * @param maximum The maximum value of the hash value.
     *
     * @return The hash value.
     *
     **/

    int
    hash(org.omg.IOP.IOR ref,
         int maximum);

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactory/get_info:1.0
    //
    /**
     *
     * Returns the information object associated with the Connector
     * factory.
     *
     * @return The Connector factory information object.
     *
     **/

    ConFactoryInfo
    get_info();
}

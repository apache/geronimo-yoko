/*
 * Copyright 2010 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package test.ins.URLTest;

//
// IDL:URLTest/IIOPAddress:1.0
//
/**
 * interface for testing CORBA URLs using iiop protocol
 **/

public interface IIOPAddressOperations
{
    //
    // IDL:URLTest/IIOPAddress/getKey:1.0
    //
    /**
     * Get the corbaloc key the object is advertised with.
     * More than one object may return the same corbaloc key
     **/

    String
    getKey();

    //
    // IDL:URLTest/IIOPAddress/getPort:1.0
    //
    /**
     * Get the port this object is listening on
     **/

    short
    getPort();

    //
    // IDL:URLTest/IIOPAddress/getHost:1.0
    //
    /**
     * Get the hostname / address this object is listening on
     **/

    String
    getHost();

    //
    // IDL:URLTest/IIOPAddress/getIIOPAddress:1.0
    //
    /**
     * Get the object's corabloc iiop address
     * such as ":555objs.com:34"
     **/

    String
    getIIOPAddress();

    //
    // IDL:URLTest/IIOPAddress/getCorbalocURL:1.0
    //
    /**
     * Get the object's full URL string.
     * "corbaloc::555objs.com:34/Key"
     **/

    String
    getCorbalocURL();

    //
    // IDL:URLTest/IIOPAddress/destroy:1.0
    //
    /**
     * Destroy this object. This allows fault tolerance testing
     * if this object is referenced in a multi-profile IOR.
     **/

    void
    destroy();

    //
    // IDL:URLTest/IIOPAddress/setString:1.0
    //
    /**
     * Set arbitrary text string 
     * @param textStr The text string to set
     * @see getString
     **/

    void
    setString(String textStr);

    //
    // IDL:URLTest/IIOPAddress/getString:1.0
    //
    /**
     * Retrieve any text set for this object
     * returns a zero-length string if no string
     * has been set
     **/

    String
    getString();

    //
    // IDL:URLTest/IIOPAddress/deactivate:1.0
    //
    /**
     * Deactivate this server (via orb shutdown)
     **/

    void
    deactivate();
}

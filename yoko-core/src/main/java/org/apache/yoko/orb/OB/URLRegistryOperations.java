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

package org.apache.yoko.orb.OB;

//
// IDL:orb.yoko.apache.org/OB/URLRegistry:1.0
//
/**
 *
 * The URLRegistry holds all of the supported URL schemes.
 *
 * @see URLScheme
 *
 **/

public interface URLRegistryOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/URLRegistry/add_scheme:1.0
    //
    /**
     *
     * Register a new URL scheme.
     *
     * @param scheme The new scheme.
     *
     * @exception org.apache.yoko.orb.OB.URLRegistryPackage.SchemeAlreadyExists Another scheme already exists with
     * the same name.
     *
     **/

    void
    add_scheme(URLScheme scheme)
        throws org.apache.yoko.orb.OB.URLRegistryPackage.SchemeAlreadyExists;

    //
    // IDL:orb.yoko.apache.org/OB/URLRegistry/find_scheme:1.0
    //
    /**
     *
     * Find a scheme with the given name.
     *
     * @param name The scheme name, in lower case.
     *
     * @return The URLScheme, or nil if no match was found.
     *
     **/

    URLScheme
    find_scheme(String name);

    //
    // IDL:orb.yoko.apache.org/OB/URLRegistry/parse_url:1.0
    //
    /**
     *
     * Convert a URL into an object reference by delegating to a
     * registered URLScheme object.
     *
     * @param url The complete URL, including the scheme.
     *
     * @return An object reference.
     *
     * @exception BAD_PARAM In case the URL is invalid.
     *
     **/

    org.omg.CORBA.Object
    parse_url(String url);

    //
    // IDL:orb.yoko.apache.org/OB/URLRegistry/destroy:1.0
    //
    /**
     *
     * Invoke <code>destroy()</code> on all registered schemes, and
     * release any resources held by the object.
     *
     **/

    void
    destroy();
}

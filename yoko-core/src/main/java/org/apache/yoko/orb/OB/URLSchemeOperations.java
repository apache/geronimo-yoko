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
// IDL:orb.yoko.apache.org/OB/URLScheme:1.0
//
/**
 *
 * A URLScheme (e.g., <code>file:</code>, <code>corbaloc:</code>, etc.)
 * is responsible for converting a URL into an object reference.
 * All schemes must be installed in the <code>URLRegistry</code>.
 *
 * @see URLRegistry
 *
 **/

public interface URLSchemeOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/URLScheme/name:1.0
    //
    /**
     *
     * Each scheme must have a unique name. All scheme names must
     * be in lower case, and do not include the trailing colon.
     *
     **/

    String
    name();

    //
    // IDL:orb.yoko.apache.org/OB/URLScheme/parse_url:1.0
    //
    /**
     *
     * Convert a URL into an object reference.
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
    // IDL:orb.yoko.apache.org/OB/URLScheme/destroy:1.0
    //
    /**
     *
     * Release any resources held by the object.
     *
     **/

    void
    destroy();
}

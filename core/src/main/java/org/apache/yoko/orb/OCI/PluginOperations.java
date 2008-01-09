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
// IDL:orb.yoko.apache.org/OCI/Plugin:1.0
//
/**
 *
 * The interface for a Plugin object, which is used to initialize
 * an OCI plug-in.
 *
 **/

public interface PluginOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/Plugin/id:1.0
    //
    /** The plugin id. */

    String
    id();

    //
    // IDL:orb.yoko.apache.org/OCI/Plugin/tag:1.0
    //
    /** The profile id tag. */

    int
    tag();

    //
    // IDL:orb.yoko.apache.org/OCI/Plugin/init_client:1.0
    //
    /**
     *
     * Initialize the client-side of the plug-in.
     *
     * @param params Plug-in specific parameters.
     *
     **/

    void
    init_client(String[] params);

    //
    // IDL:orb.yoko.apache.org/OCI/Plugin/init_server:1.0
    //
    /**
     *
     * Initialize the server-side of the plug-in.
     *
     * @param params Plug-in specific parameters.
     *
     **/

    void
    init_server(String[] params);
}

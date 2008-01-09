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
// IDL:orb.yoko.apache.org/OCI/ConFactoryInfo:1.0
//
/**
 *
 * Information on an OCI ConFactory object.
 *
 * @see ConFactory
 *
 **/

public interface ConFactoryInfoOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/ConFactoryInfo/id:1.0
    //
    /** The plugin id. */

    String
    id();

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactoryInfo/tag:1.0
    //
    /** The profile id tag. */

    int
    tag();

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactoryInfo/describe:1.0
    //
    /**
     *
     * Returns a human readable description of the transport.
     *
     * @return The description.
     *
     **/

    String
    describe();

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactoryInfo/add_connect_cb:1.0
    //
    /**
     *
     * Add a callback that is called whenever a new connection is
     * established. If the callback has already been registered, this
     * method has no effect.
     *
     * @param cb The callback to add.
     *
     **/

    void
    add_connect_cb(ConnectCB cb);

    //
    // IDL:orb.yoko.apache.org/OCI/ConFactoryInfo/remove_connect_cb:1.0
    //
    /**
     *
     * Remove a connect callback. If the callback was not registered,
     * this method has no effect.
     *
     * @param cb The callback to remove.
     *
     **/

    void
    remove_connect_cb(ConnectCB cb);
}

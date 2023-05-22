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
package org.apache.yoko.orb.OAD;

//
// IDL:orb.yoko.apache.org/OAD/ProcessEndpoint:1.0
//
/**
 *
 * The ProcessEndpoint. This is used to reestablish a link between
 * the ProcessManager and the Process
 *
 **/

public interface ProcessEndpointOperations
{
    //
    // IDL:orb.yoko.apache.org/OAD/ProcessEndpoint/reestablish_link:1.0
    //
    /**
     *
     * Called by the ProcessManager to establish a link between the
     * Process and the ProcessManager
     *
     * @param endpoint The ProcessEndpointManager with which to establish
     *                  a link
     **/

    void
    reestablish_link(ProcessEndpointManager endpoint);

    //
    // IDL:orb.yoko.apache.org/OAD/ProcessEndpoint/stop:1.0
    //
    /**
     *
     * Called by the ProcessManager to inform the Process to shutdown.
     *
     **/

    void
    stop();
}

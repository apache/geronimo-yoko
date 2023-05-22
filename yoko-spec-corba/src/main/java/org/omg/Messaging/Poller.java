/*
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
package org.omg.Messaging;

//
// IDL:omg.org/Messaging/Poller:1.0
//
/***/

public interface Poller extends org.omg.CORBA.portable.ValueBase,
                                org.omg.CORBA.Pollable
{
    //
    // IDL:omg.org/Messaging/Poller/operation_target:1.0
    //
    /***/

    org.omg.CORBA.Object
    operation_target();

    //
    // IDL:omg.org/Messaging/Poller/operation_name:1.0
    //
    /***/

    String
    operation_name();

    //
    // IDL:omg.org/Messaging/Poller/associated_handler:1.0
    //
    /***/

    ReplyHandler
    associated_handler();

    void
    associated_handler(ReplyHandler val);

    //
    // IDL:omg.org/Messaging/Poller/is_from_poller:1.0
    //
    /***/

    boolean
    is_from_poller();
}

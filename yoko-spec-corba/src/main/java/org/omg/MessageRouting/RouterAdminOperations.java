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

package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/RouterAdmin:1.0
//
/***/

public interface RouterAdminOperations
{
    //
    // IDL:omg.org/MessageRouting/RouterAdmin/register_destination:1.0
    //
    /***/

    void
    register_destination(org.omg.CORBA.Object dest,
                         boolean is_router,
                         RetryPolicy retry,
                         DecayPolicy decay);

    //
    // IDL:omg.org/MessageRouting/RouterAdmin/suspend_destination:1.0
    //
    /***/

    void
    suspend_destination(org.omg.CORBA.Object dest,
                        ResumePolicy resumption)
        throws InvalidState;

    //
    // IDL:omg.org/MessageRouting/RouterAdmin/resume_destination:1.0
    //
    /***/

    void
    resume_destination(org.omg.CORBA.Object dest)
        throws InvalidState;

    //
    // IDL:omg.org/MessageRouting/RouterAdmin/unregister_destination:1.0
    //
    /***/

    void
    unregister_destination(org.omg.CORBA.Object dest)
        throws InvalidState;
}

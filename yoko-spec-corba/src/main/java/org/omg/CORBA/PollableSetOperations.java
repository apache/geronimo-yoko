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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/PollableSet:1.0
//
/***/

public interface PollableSetOperations
{
    //
    // IDL:omg.org/CORBA/PollableSet/create_dii_pollable:1.0
    //
    /***/

    DIIPollable
    create_dii_pollable();

    //
    // IDL:omg.org/CORBA/PollableSet/add_pollable:1.0
    //
    /***/

    void
    add_pollable(Pollable potential);

    //
    // IDL:omg.org/CORBA/PollableSet/get_ready_pollable:1.0
    //
    /***/

    Pollable
    get_ready_pollable(int timeout)
        throws org.omg.CORBA.PollableSetPackage.NoPossiblePollable;

    //
    // IDL:omg.org/CORBA/PollableSet/remove:1.0
    //
    /***/

    void
    remove(Pollable potential)
        throws org.omg.CORBA.PollableSetPackage.UnknownPollable;

    //
    // IDL:omg.org/CORBA/PollableSet/number_left:1.0
    //
    /***/

    short
    number_left();
}

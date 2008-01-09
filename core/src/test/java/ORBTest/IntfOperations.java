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

package ORBTest;

//
// IDL:ORBTest/Intf:1.0
//
/***/

public interface IntfOperations
{
    //
    // IDL:ORBTest/Intf/get_ORB_type:1.0
    //
    /***/

    ORBType
    get_ORB_type();

    //
    // IDL:ORBTest/Intf/deactivate:1.0
    //
    /***/

    void
    deactivate();

    //
    // IDL:ORBTest/Intf/concurrent_request_execution:1.0
    //
    /***/

    boolean
    concurrent_request_execution();

    //
    // IDL:ORBTest/Intf/get_test_case_list:1.0
    //
    /***/

    TestCase[]
    get_test_case_list();
}

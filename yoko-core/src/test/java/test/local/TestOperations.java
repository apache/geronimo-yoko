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

package test.local;

//
// IDL:Test:1.0
//
/***/

public interface TestOperations
{
    //
    // IDL:Test/say:1.0
    //
    /***/

    void
    say(String s);

    //
    // IDL:Test/intest:1.0
    //
    /***/

    void
    intest(Test t);

    //
    // IDL:Test/inany:1.0
    //
    /***/

    void
    inany(org.omg.CORBA.Any a);

    //
    // IDL:Test/outany:1.0
    //
    /***/

    void
    outany(org.omg.CORBA.AnyHolder a);

    //
    // IDL:Test/returntest:1.0
    //
    /***/

    Test
    returntest();

    //
    // IDL:Test/shutdown:1.0
    //
    /***/

    void
    shutdown();
}

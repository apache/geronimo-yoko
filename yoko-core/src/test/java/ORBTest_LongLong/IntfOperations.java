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
package ORBTest_LongLong;

//
// IDL:ORBTest_LongLong/Intf:1.0
//
/***/

public interface IntfOperations
{
    //
    // IDL:ORBTest_LongLong/Intf/attrLongLong:1.0
    //
    /***/

    long
    attrLongLong();

    void
    attrLongLong(long val);

    //
    // IDL:ORBTest_LongLong/Intf/opLongLong:1.0
    //
    /***/

    long
    opLongLong(long a0,
               org.omg.CORBA.LongHolder a1,
               org.omg.CORBA.LongHolder a2);

    //
    // IDL:ORBTest_LongLong/Intf/opLongLongEx:1.0
    //
    /***/

    long
    opLongLongEx(long a0,
                 org.omg.CORBA.LongHolder a1,
                 org.omg.CORBA.LongHolder a2)
        throws ExLongLong;

    //
    // IDL:ORBTest_LongLong/Intf/attrULongLong:1.0
    //
    /***/

    long
    attrULongLong();

    void
    attrULongLong(long val);

    //
    // IDL:ORBTest_LongLong/Intf/opULongLong:1.0
    //
    /***/

    long
    opULongLong(long a0,
                org.omg.CORBA.LongHolder a1,
                org.omg.CORBA.LongHolder a2);

    //
    // IDL:ORBTest_LongLong/Intf/opULongLongEx:1.0
    //
    /***/

    long
    opULongLongEx(long a0,
                  org.omg.CORBA.LongHolder a1,
                  org.omg.CORBA.LongHolder a2)
        throws ExULongLong;
}

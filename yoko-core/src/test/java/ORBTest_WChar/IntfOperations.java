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
package ORBTest_WChar;

//
// IDL:ORBTest_WChar/Intf:1.0
//
/***/

public interface IntfOperations
{
    //
    // IDL:ORBTest_WChar/Intf/attrWChar:1.0
    //
    /***/

    char
    attrWChar();

    void
    attrWChar(char val);

    //
    // IDL:ORBTest_WChar/Intf/opWChar:1.0
    //
    /***/

    char
    opWChar(char a0,
            org.omg.CORBA.CharHolder a1,
            org.omg.CORBA.CharHolder a2);

    //
    // IDL:ORBTest_WChar/Intf/opWCharEx:1.0
    //
    /***/

    char
    opWCharEx(char a0,
              org.omg.CORBA.CharHolder a1,
              org.omg.CORBA.CharHolder a2)
        throws ExWChar;

    //
    // IDL:ORBTest_WChar/Intf/attrWString:1.0
    //
    /***/

    String
    attrWString();

    void
    attrWString(String val);

    //
    // IDL:ORBTest_WChar/Intf/opWString:1.0
    //
    /***/

    String
    opWString(String a0,
              org.omg.CORBA.StringHolder a1,
              org.omg.CORBA.StringHolder a2);

    //
    // IDL:ORBTest_WChar/Intf/opWStringEx:1.0
    //
    /***/

    String
    opWStringEx(String a0,
                org.omg.CORBA.StringHolder a1,
                org.omg.CORBA.StringHolder a2)
        throws ExWString;
}

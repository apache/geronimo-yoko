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
package test.types;

//
// IDL:TestConstInterface:1.0
//
/***/

public interface TestConstInterfaceOperations
{
    //
    // IDL:TestConstInterface/ConstLong:1.0
    //
    /***/

    int ConstLong = (int)(61454L);

    //
    // IDL:TestConstInterface/ConstULong:1.0
    //
    /***/

    int ConstULong = (int)(4294967295L);

    //
    // IDL:TestConstInterface/ConstChar0:1.0
    //
    /***/

    char ConstChar0 = '\0';

    //
    // IDL:TestConstInterface/ConstChar1:1.0
    //
    /***/

    char ConstChar1 = 'c';

    //
    // IDL:TestConstInterface/ConstChar2:1.0
    //
    /***/

    char ConstChar2 = '\n';

    //
    // IDL:TestConstInterface/ConstChar3:1.0
    //
    /***/

    char ConstChar3 = '\377';

    //
    // IDL:TestConstInterface/ConstChar4:1.0
    //
    /***/

    char ConstChar4 = '\210';

    //
    // IDL:TestConstInterface/ConstChar5:1.0
    //
    /***/

    char ConstChar5 = '\'';

    //
    // IDL:TestConstInterface/ConstWChar:1.0
    //
    /***/

    char ConstWChar = 'Z';

    //
    // IDL:TestConstInterface/ConstString:1.0
    //
    /***/

    String ConstString = "\n\t\013\b\r\f\007\\?\'\"\377\377\007";

    //
    // IDL:TestConstInterface/ConstWString:1.0
    //
    /***/

    String ConstWString = "\n\t\013\b\r\f\007\\?\'\"\377\377\007";

    //
    // IDL:TestConstInterface/ConstEnum:1.0
    //
    /***/

    Measurement ConstEnum = Measurement.FEET;
}

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
package ORBTest_ExceptionsExt_2_3;

//
// IDL:ORBTest_ExceptionsExt_2_3/Intf:1.0
//
/***/

public interface IntfOperations
{
    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_CODESET_INCOMPATIBLE_Ex:1.0
    //
    /***/

    void
    op_CODESET_INCOMPATIBLE_Ex();

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_REBIND_Ex:1.0
    //
    /***/

    void
    op_REBIND_Ex();

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_TIMEOUT_Ex:1.0
    //
    /***/

    void
    op_TIMEOUT_Ex();

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_TRANSACTION_UNAVAILABLE_Ex:1.0
    //
    /***/

    void
    op_TRANSACTION_UNAVAILABLE_Ex();

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_TRANSACTION_MODE_Ex:1.0
    //
    /***/

    void
    op_TRANSACTION_MODE_Ex();

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_BAD_QOS_Ex:1.0
    //
    /***/

    void
    op_BAD_QOS_Ex();
}

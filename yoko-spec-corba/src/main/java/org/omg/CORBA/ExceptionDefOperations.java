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
package org.omg.CORBA;

//
// IDL:omg.org/CORBA/ExceptionDef:1.0
//
/***/

public interface ExceptionDefOperations extends ContainedOperations,
                                                ContainerOperations
{
    //
    // IDL:omg.org/CORBA/ExceptionDef/type:1.0
    //
    /***/

    org.omg.CORBA.TypeCode
    type();

    //
    // IDL:omg.org/CORBA/ExceptionDef/members:1.0
    //
    /***/

    StructMember[]
    members();

    void
    members(StructMember[] val);
}

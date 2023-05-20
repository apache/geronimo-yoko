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
package org.omg.DynamicAny;

//
// IDL:omg.org/DynamicAny/DynUnion:1.0
//
/***/

public interface DynUnionOperations extends DynAnyOperations
{
    //
    // IDL:omg.org/DynamicAny/DynUnion/get_discriminator:1.0
    //
    /***/

    DynAny
    get_discriminator();

    //
    // IDL:omg.org/DynamicAny/DynUnion/set_discriminator:1.0
    //
    /***/

    void
    set_discriminator(DynAny d)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

    //
    // IDL:omg.org/DynamicAny/DynUnion/set_to_default_member:1.0
    //
    /***/

    void
    set_to_default_member()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

    //
    // IDL:omg.org/DynamicAny/DynUnion/set_to_no_active_member:1.0
    //
    /***/

    void
    set_to_no_active_member()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

    //
    // IDL:omg.org/DynamicAny/DynUnion/has_no_active_member:1.0
    //
    /***/

    boolean
    has_no_active_member();

    //
    // IDL:omg.org/DynamicAny/DynUnion/discriminator_kind:1.0
    //
    /***/

    org.omg.CORBA.TCKind
    discriminator_kind();

    //
    // IDL:omg.org/DynamicAny/DynUnion/member:1.0
    //
    /***/

    DynAny
    member()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynUnion/member_name:1.0
    //
    /***/

    String
    member_name()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynUnion/member_kind:1.0
    //
    /***/

    org.omg.CORBA.TCKind
    member_kind()
        throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynUnion/is_set_to_default_member:1.0
    //
    /***/

    boolean
    is_set_to_default_member();
}

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
package test.pi;

//
// IDL:TestInterface:1.0
//
/***/

public interface TestInterfaceOperations
{
    //
    // IDL:TestInterface/noargs:1.0
    //
    /***/

    void
    noargs();

    //
    // IDL:TestInterface/noargs_oneway:1.0
    //
    /***/

    void
    noargs_oneway();

    //
    // IDL:TestInterface/systemexception:1.0
    //
    /***/

    void
    systemexception();

    //
    // IDL:TestInterface/userexception:1.0
    //
    /***/

    void
    userexception()
        throws test.pi.TestInterfacePackage.user;

    //
    // IDL:TestInterface/location_forward:1.0
    //
    /***/

    void
    location_forward();

    //
    // IDL:TestInterface/test_service_context:1.0
    //
    /***/

    void
    test_service_context();

    //
    // IDL:TestInterface/string_attrib:1.0
    //
    /***/

    String
    string_attrib();

    void
    string_attrib(String val);

    //
    // IDL:TestInterface/one_string_in:1.0
    //
    /***/

    void
    one_string_in(String param);

    //
    // IDL:TestInterface/one_string_inout:1.0
    //
    /***/

    void
    one_string_inout(org.omg.CORBA.StringHolder param);

    //
    // IDL:TestInterface/one_string_out:1.0
    //
    /***/

    void
    one_string_out(org.omg.CORBA.StringHolder param);

    //
    // IDL:TestInterface/one_string_return:1.0
    //
    /***/

    String
    one_string_return();

    //
    // IDL:TestInterface/struct_attrib:1.0
    //
    /***/

    test.pi.TestInterfacePackage.s
    struct_attrib();

    void
    struct_attrib(test.pi.TestInterfacePackage.s val);

    //
    // IDL:TestInterface/one_struct_in:1.0
    //
    /***/

    void
    one_struct_in(test.pi.TestInterfacePackage.s param);

    //
    // IDL:TestInterface/one_struct_inout:1.0
    //
    /***/

    void
    one_struct_inout(test.pi.TestInterfacePackage.sHolder param);

    //
    // IDL:TestInterface/one_struct_out:1.0
    //
    /***/

    void
    one_struct_out(test.pi.TestInterfacePackage.sHolder param);

    //
    // IDL:TestInterface/one_struct_return:1.0
    //
    /***/

    test.pi.TestInterfacePackage.s
    one_struct_return();

    //
    // IDL:TestInterface/deactivate:1.0
    //
    /***/

    void
    deactivate();
}

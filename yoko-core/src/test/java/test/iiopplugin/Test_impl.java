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
package test.iiopplugin;

public class Test_impl extends TestPOA {
    private org.omg.PortableServer.POA poa_;

    private Test localTest_;

    private static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    public Test_impl(org.omg.PortableServer.POA poa) {
        poa_ = poa;
        localTest_ = new LocalTest_impl();
    }

    public org.omg.PortableServer.POA _default_POA() {
        if (poa_ != null)
            return poa_;
        else
            return super._default_POA();
    }

    public void say(String s) {
        // System.out.println(s);
    }

    public void intest(Test t) {
        t.say("hi");
    }

    public void inany(org.omg.CORBA.Any a) {
        Test t = TestHelper.extract(a);
    }

    public void outany(org.omg.CORBA.AnyHolder a) {
        a.value = _orb().create_any();
        TestHelper.insert(a.value, localTest_);
    }

    public Test returntest() {
        return localTest_;
    }

    public void shutdown() {
        _orb().shutdown(false);
        if (!ServerPlugin.testPassed())
            throw new test.common.TestException();
    }
}

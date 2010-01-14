/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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


/**
 * @version $Rev: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
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

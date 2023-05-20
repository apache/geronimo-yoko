/*
 * Copyright 2015 IBM Corporation and others.
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
package test.tnaming;

public class Test_impl extends TestPOA {
    private String name_;

    private org.omg.PortableServer.POA poa_;

    private static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    public Test_impl(org.omg.PortableServer.POA poa, String name) {
        poa_ = poa;
        name_ = name;
    }

    public org.omg.PortableServer.POA _default_POA() {
        if (poa_ != null)
            return poa_;
        else
            return super._default_POA();
    }

    public String get_id() {
        return name_;
    }

    public void shutdown() {
        _orb().shutdown(false);
    }
}

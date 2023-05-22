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
package test.codesets;

class TestCodeSets_impl extends TestCodeSetsPOA {
    private org.omg.CORBA.ORB orb_;

    TestCodeSets_impl(org.omg.CORBA.ORB orb) {
        orb_ = orb;
    }

    public char testChar(char c) {
        return c;
    }

    public String testString(String s) {
        return s;
    }

    public char testWChar(char wc) {
        return wc;
    }

    public String testWString(String ws) {
        return ws;
    }

    public void deactivate() {
        orb_.shutdown(false);
    }
}

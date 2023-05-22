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
package test.local;

public class LocalTest_impl extends org.omg.CORBA.LocalObject implements
        LocalTest {
    public void say(String s) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void intest(Test t) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void inany(org.omg.CORBA.Any a) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void outany(org.omg.CORBA.AnyHolder a) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public Test returntest() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void shutdown() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}

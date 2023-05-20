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
package ORBTest;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

final class TestIntfStubTimeout_impl extends ORBTest_StubTimeout.IntfPOA {
    private POA m_poa;

    public TestIntfStubTimeout_impl(POA poa) {
        m_poa = poa;
    }

    public synchronized void sleep_oneway(int sec) {
        try {
            Thread.currentThread().sleep(sec * 1000);
        } catch (java.lang.InterruptedException ex) {
        }
    }

    public synchronized void sleep_twoway(int sec) {
        try {
            Thread.currentThread().sleep(sec * 1000);
        } catch (java.lang.InterruptedException ex) {
        }
    }

    public org.omg.PortableServer.POA _default_POA() {
        return m_poa;
    }
}

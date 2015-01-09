/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
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

package ORBTest;

import static org.junit.Assert.assertTrue;

import org.omg.CORBA.*;

public class TestObjectStubTimeout extends test.common.TestBase implements
        TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    private boolean m_local;

    public TestObjectStubTimeout(ORB orb, ORBTest.Intf test_intf, boolean local) {
        m_orb = orb;
        m_test_intf = test_intf;
        m_local = local;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        // Timeout test are not for collocated client and servers
        //
        if (m_local) {
            return false;
        }

        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_StubTimeout.Intf ti = (ORBTest_StubTimeout.IntfHelper
                        .narrow(obj));
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_StubTimeout.Intf tiOriginal = (ORBTest_StubTimeout.IntfHelper
                .narrow(obj));

        //
        // Create policy list containing new timeout policy
        //
        Policy[] pl = new Policy[1];
        Any any = m_orb.create_any();
        any.insert_ulong(2000);
        try {
            pl[0] = (m_orb.create_policy(
                    org.apache.yoko.orb.OB.TIMEOUT_POLICY_ID.value, any));
        } catch (PolicyError e) {
            assertTrue(false);
        }

        //
        // _set_policy_override returns new proxy with the policies applied
        //
        org.omg.CORBA.Object new_obj = tiOriginal._set_policy_override(pl,
                SetOverrideType.ADD_OVERRIDE);

        //
        // _narrow to correct type
        //
        ORBTest_StubTimeout.Intf ti = (ORBTest_StubTimeout.IntfHelper
                .narrow(new_obj));

        try {
            ti.sleep_twoway(3);
            assertTrue(false);
        } catch (NO_RESPONSE ex) {
        }

        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException ex) {
        }

        //
        // The following test doesn't work, because there is a race
        // between the sleep_oneway and the sleep_twoway if the server
        // is running in thread pool or thread-per-request mode. (This
        // isn't such an exiting test anyway, so perhaps we should
        // permanently remove it in a future version.)
        //
        /*
         * try { ti.sleep_oneway(3); } catch(NO_RESPONSE ex) { TEST(false); }
         * 
         * try { ti.sleep_twoway(0); TEST(false); } catch(NO_RESPONSE ex) { }
         * 
         * try { Thread.currentThread().sleep(2000); }
         * catch(InterruptedException ex) { }
         */

        try {
            ti.sleep_twoway(0);
        } catch (NO_RESPONSE ex) {
            assertTrue(false);
        }
    }

    public void test_DII(org.omg.CORBA.Object obj) {
    }
}

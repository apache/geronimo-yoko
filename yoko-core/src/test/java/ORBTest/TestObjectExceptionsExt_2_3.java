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
package ORBTest;

import static org.junit.Assert.assertTrue;

import org.omg.CORBA.*;

public class TestObjectExceptionsExt_2_3 extends test.common.TestBase implements
        TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    public TestObjectExceptionsExt_2_3(ORB orb, ORBTest.Intf test_intf) {
        m_orb = orb;
        m_test_intf = test_intf;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_ExceptionsExt_2_3.Intf ti = (ORBTest_ExceptionsExt_2_3.IntfHelper
                        .narrow(obj));
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_ExceptionsExt_2_3.Intf ti = (ORBTest_ExceptionsExt_2_3.IntfHelper
                .narrow(obj));

        try {
            ti.op_CODESET_INCOMPATIBLE_Ex();
            assertTrue(false);
        } catch (CODESET_INCOMPATIBLE ex) {
            assertTrue(ex.minor == 31);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_REBIND_Ex();
            assertTrue(false);
        } catch (REBIND ex) {
            assertTrue(ex.minor == 32);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_TIMEOUT_Ex();
            assertTrue(false);
        } catch (TIMEOUT ex) {
            assertTrue(ex.minor == 33);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_TRANSACTION_UNAVAILABLE_Ex();
            assertTrue(false);
        } catch (TRANSACTION_UNAVAILABLE ex) {
            assertTrue(ex.minor == 34);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_TRANSACTION_MODE_Ex();
            assertTrue(false);
        } catch (TRANSACTION_MODE ex) {
            assertTrue(ex.minor == 35);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_BAD_QOS_Ex();
            assertTrue(false);
        } catch (BAD_QOS ex) {
            assertTrue(ex.minor == 36);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }
    }

    public void test_DII(org.omg.CORBA.Object obj) {
        // REVISIT
    }
}

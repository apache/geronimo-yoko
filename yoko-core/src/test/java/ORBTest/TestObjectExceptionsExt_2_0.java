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

import org.omg.CORBA.*;

public class TestObjectExceptionsExt_2_0 extends test.common.TestBase implements
        TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    public TestObjectExceptionsExt_2_0(ORB orb, ORBTest.Intf test_intf) {
        m_orb = orb;
        m_test_intf = test_intf;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_ExceptionsExt_2_0.Intf ti = (ORBTest_ExceptionsExt_2_0.IntfHelper
                        .narrow(obj));
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_ExceptionsExt_2_0.Intf ti = (ORBTest_ExceptionsExt_2_0.IntfHelper
                .narrow(obj));

        try {
            ti.op_PERSIST_STORE_Ex();
            TEST(false);
        } catch (PERSIST_STORE ex) {
            TEST(ex.minor == 16);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_FREE_MEM_Ex();
            TEST(false);
        } catch (FREE_MEM ex) {
            TEST(ex.minor == 19);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_INV_IDENT_Ex();
            TEST(false);
        } catch (INV_IDENT ex) {
            TEST(ex.minor == 20);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_INV_FLAG_Ex();
            TEST(false);
        } catch (INV_FLAG ex) {
            TEST(ex.minor == 21);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_INTF_REPOS_Ex();
            TEST(false);
        } catch (INTF_REPOS ex) {
            TEST(ex.minor == 22);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_BAD_CONTEXT_Ex();
            TEST(false);
        } catch (BAD_CONTEXT ex) {
            TEST(ex.minor == 23);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_TRANSACTION_REQUIRED_Ex();
            TEST(false);
        } catch (TRANSACTION_REQUIRED ex) {
            TEST(ex.minor == 27);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_TRANSACTION_ROLLEDBACK_Ex();
            TEST(false);
        } catch (TRANSACTION_ROLLEDBACK ex) {
            TEST(ex.minor == 28);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_INVALID_TRANSACTION_Ex();
            TEST(false);
        } catch (INVALID_TRANSACTION ex) {
            TEST(ex.minor == 29);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }
    }

    public void test_DII(org.omg.CORBA.Object obj) {
        // REVISIT
    }
}

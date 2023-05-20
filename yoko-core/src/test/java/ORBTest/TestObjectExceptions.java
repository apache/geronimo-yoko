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

public class TestObjectExceptions extends test.common.TestBase implements
        TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    public TestObjectExceptions(ORB orb, ORBTest.Intf test_intf) {
        m_orb = orb;
        m_test_intf = test_intf;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_Exceptions.Intf ti = (ORBTest_Exceptions.IntfHelper
                        .narrow(obj));
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_Exceptions.Intf ti = (ORBTest_Exceptions.IntfHelper.narrow(obj));

        try {
            ti.op_UNKNOWN_Ex();
            assertTrue(false);
        } catch (UNKNOWN ex) {
            assertTrue(ex.minor == 1);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_BAD_PARAM_Ex();
            assertTrue(false);
        } catch (BAD_PARAM ex) {
            assertTrue(ex.minor == 2);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_NO_MEMORY_Ex();
            assertTrue(false);
        } catch (NO_MEMORY ex) {
            assertTrue(ex.minor == 3);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_IMP_LIMIT_Ex();
            assertTrue(false);
        } catch (IMP_LIMIT ex) {
            assertTrue(ex.minor == 4);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_COMM_FAILURE_Ex();
            assertTrue(false);
        } catch (COMM_FAILURE ex) {
            assertTrue(ex.minor == 5);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_INV_OBJREF_Ex();
            assertTrue(false);
        } catch (INV_OBJREF ex) {
            assertTrue(ex.minor == 6);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_NO_PERMISSION_Ex();
            assertTrue(false);
        } catch (NO_PERMISSION ex) {
            assertTrue(ex.minor == 7);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_INTERNAL_Ex();
            assertTrue(false);
        } catch (INTERNAL ex) {
            assertTrue(ex.minor == 8);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_MARSHAL_Ex();
            assertTrue(false);
        } catch (MARSHAL ex) {
            assertTrue(ex.minor == 9);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_INITIALIZE_Ex();
            assertTrue(false);
        } catch (INITIALIZE ex) {
            assertTrue(ex.minor == 10);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_NO_IMPLEMENT_Ex();
            assertTrue(false);
        } catch (NO_IMPLEMENT ex) {
            assertTrue(ex.minor == 11);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_BAD_TYPECODE_Ex();
            assertTrue(false);
        } catch (BAD_TYPECODE ex) {
            assertTrue(ex.minor == 12);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_BAD_OPERATION_Ex();
            assertTrue(false);
        } catch (BAD_OPERATION ex) {
            assertTrue(ex.minor == 13);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_NO_RESOURCES_Ex();
            assertTrue(false);
        } catch (NO_RESOURCES ex) {
            assertTrue(ex.minor == 14);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_NO_RESPONSE_Ex();
            assertTrue(false);
        } catch (NO_RESPONSE ex) {
            assertTrue(ex.minor == 15);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_BAD_INV_ORDER_Ex();
            assertTrue(false);
        } catch (BAD_INV_ORDER ex) {
            assertTrue(ex.minor == 17);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_TRANSIENT_Ex();
            assertTrue(false);
        } catch (TRANSIENT ex) {
            assertTrue(ex.minor == 18);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_OBJ_ADAPTER_Ex();
            assertTrue(false);
        } catch (OBJ_ADAPTER ex) {
            assertTrue(ex.minor == 24);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_DATA_CONVERSION_Ex();
            assertTrue(false);
        } catch (DATA_CONVERSION ex) {
            assertTrue(ex.minor == 25);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_OBJECT_NOT_EXIST_Ex();
            assertTrue(false);
        } catch (OBJECT_NOT_EXIST ex) {
            assertTrue(ex.minor == 26);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_INV_POLICY_Ex();
            assertTrue(false);
        } catch (INV_POLICY ex) {
            assertTrue(ex.minor == 30);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }
    }

    public void test_DII(org.omg.CORBA.Object obj) {
        ORBTest_Exceptions.Intf ti = (ORBTest_Exceptions.IntfHelper.narrow(obj));

        Request request;

        try {
            request = ti._request("op_BAD_PARAM_Ex");
            request.invoke();
            Exception ex = request.env().exception();
            assertTrue(ex != null);
            BAD_PARAM bp = (BAD_PARAM) ex;
            throw bp;
        } catch (BAD_PARAM ex) {
            assertTrue(ex.minor == 2);
            assertTrue(ex.completed == CompletionStatus.COMPLETED_NO);
        }
    }
}

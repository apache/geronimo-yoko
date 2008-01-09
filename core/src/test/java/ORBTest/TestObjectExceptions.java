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
            TEST(false);
        } catch (UNKNOWN ex) {
            TEST(ex.minor == 1);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_BAD_PARAM_Ex();
            TEST(false);
        } catch (BAD_PARAM ex) {
            TEST(ex.minor == 2);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_NO_MEMORY_Ex();
            TEST(false);
        } catch (NO_MEMORY ex) {
            TEST(ex.minor == 3);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_IMP_LIMIT_Ex();
            TEST(false);
        } catch (IMP_LIMIT ex) {
            TEST(ex.minor == 4);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_COMM_FAILURE_Ex();
            TEST(false);
        } catch (COMM_FAILURE ex) {
            TEST(ex.minor == 5);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_INV_OBJREF_Ex();
            TEST(false);
        } catch (INV_OBJREF ex) {
            TEST(ex.minor == 6);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_NO_PERMISSION_Ex();
            TEST(false);
        } catch (NO_PERMISSION ex) {
            TEST(ex.minor == 7);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_INTERNAL_Ex();
            TEST(false);
        } catch (INTERNAL ex) {
            TEST(ex.minor == 8);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_MARSHAL_Ex();
            TEST(false);
        } catch (MARSHAL ex) {
            TEST(ex.minor == 9);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_INITIALIZE_Ex();
            TEST(false);
        } catch (INITIALIZE ex) {
            TEST(ex.minor == 10);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_NO_IMPLEMENT_Ex();
            TEST(false);
        } catch (NO_IMPLEMENT ex) {
            TEST(ex.minor == 11);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_BAD_TYPECODE_Ex();
            TEST(false);
        } catch (BAD_TYPECODE ex) {
            TEST(ex.minor == 12);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_BAD_OPERATION_Ex();
            TEST(false);
        } catch (BAD_OPERATION ex) {
            TEST(ex.minor == 13);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_NO_RESOURCES_Ex();
            TEST(false);
        } catch (NO_RESOURCES ex) {
            TEST(ex.minor == 14);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_NO_RESPONSE_Ex();
            TEST(false);
        } catch (NO_RESPONSE ex) {
            TEST(ex.minor == 15);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_BAD_INV_ORDER_Ex();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            TEST(ex.minor == 17);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_TRANSIENT_Ex();
            TEST(false);
        } catch (TRANSIENT ex) {
            TEST(ex.minor == 18);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_OBJ_ADAPTER_Ex();
            TEST(false);
        } catch (OBJ_ADAPTER ex) {
            TEST(ex.minor == 24);
            TEST(ex.completed == CompletionStatus.COMPLETED_MAYBE);
        }

        try {
            ti.op_DATA_CONVERSION_Ex();
            TEST(false);
        } catch (DATA_CONVERSION ex) {
            TEST(ex.minor == 25);
            TEST(ex.completed == CompletionStatus.COMPLETED_YES);
        }

        try {
            ti.op_OBJECT_NOT_EXIST_Ex();
            TEST(false);
        } catch (OBJECT_NOT_EXIST ex) {
            TEST(ex.minor == 26);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }

        try {
            ti.op_INV_POLICY_Ex();
            TEST(false);
        } catch (INV_POLICY ex) {
            TEST(ex.minor == 30);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }
    }

    public void test_DII(org.omg.CORBA.Object obj) {
        ORBTest_Exceptions.Intf ti = (ORBTest_Exceptions.IntfHelper.narrow(obj));

        Request request;

        try {
            request = ti._request("op_BAD_PARAM_Ex");
            request.invoke();
            Exception ex = request.env().exception();
            TEST(ex != null);
            BAD_PARAM bp = (BAD_PARAM) ex;
            throw bp;
        } catch (BAD_PARAM ex) {
            TEST(ex.minor == 2);
            TEST(ex.completed == CompletionStatus.COMPLETED_NO);
        }
    }
}

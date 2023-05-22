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

class TestIntfExceptionsExt_2_0DSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB m_orb;

    TestIntfExceptionsExt_2_0DSI_impl(ORB orb) {
        m_orb = orb;
    }

    static final String[] m_ids = { "IDL:ORBTest_ExceptionsExt_2_0/Intf:1.0" };

    public String[] _all_interfaces(org.omg.PortableServer.POA poa,
            byte[] object_id) {
        return m_ids;
    }

    public void invoke(ServerRequest request) {
        String name = request.operation();

        boolean ex;
        if (name.length() > 2 && name.endsWith("Ex")) {
            name = name.substring(0, name.length() - 2);
            ex = true;
        } else {
            ex = false;
        }

        if (name.equals("op_PERSIST_STORE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            PERSIST_STOREHelper.insert(any, new PERSIST_STORE(16,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_FREE_MEM_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            FREE_MEMHelper.insert(any, new FREE_MEM(19,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INV_IDENT_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INV_IDENTHelper.insert(any, new INV_IDENT(20,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INV_FLAG_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INV_FLAGHelper.insert(any, new INV_FLAG(21,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INTF_REPOS_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INTF_REPOSHelper.insert(any, new INTF_REPOS(22,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_BAD_CONTEXT_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            BAD_CONTEXTHelper.insert(any, new BAD_CONTEXT(23,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_TRANSACTION_REQUIRED_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            TRANSACTION_REQUIREDHelper.insert(any, new TRANSACTION_REQUIRED(27,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_TRANSACTION_ROLLEDBACK_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            TRANSACTION_ROLLEDBACKHelper.insert(any,
                    new TRANSACTION_ROLLEDBACK(28,
                            CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INVALID_TRANSACTION_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INVALID_TRANSACTIONHelper.insert(any, new INVALID_TRANSACTION(29,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        System.err.println("DSI implementation: unknown operation: " + name);

        NVList list = m_orb.create_list(0);
        request.arguments(list);

        Any exAny = m_orb.create_any();
        BAD_OPERATIONHelper.insert(exAny, new BAD_OPERATION());
        request.set_exception(exAny);
    }
}

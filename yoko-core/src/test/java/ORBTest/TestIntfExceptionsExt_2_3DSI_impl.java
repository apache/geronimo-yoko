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

class TestIntfExceptionsExt_2_3DSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB m_orb;

    TestIntfExceptionsExt_2_3DSI_impl(ORB orb) {
        m_orb = orb;
    }

    static final String[] m_ids = { "IDL:ORBTest_ExceptionsExt_2_3/Intf:1.0" };

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

        if (name.equals("op_CODESET_INCOMPATIBLE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            CODESET_INCOMPATIBLEHelper.insert(any, new CODESET_INCOMPATIBLE(31,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_REBIND_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            REBINDHelper.insert(any, new REBIND(32,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_TIMEOUT_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            TIMEOUTHelper.insert(any, new TIMEOUT(33,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_TRANSACTION_UNAVAILABLE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            TRANSACTION_UNAVAILABLEHelper.insert(any,
                    new TRANSACTION_UNAVAILABLE(34,
                            CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_TRANSACTION_MODE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            TRANSACTION_MODEHelper.insert(any, new TRANSACTION_MODE(35,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_BAD_QOS_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            BAD_QOSHelper.insert(any, new BAD_QOS(36,
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

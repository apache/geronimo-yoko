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

class TestIntfExceptionsDSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB m_orb;

    TestIntfExceptionsDSI_impl(ORB orb) {
        m_orb = orb;
    }

    static final String[] m_ids = { "IDL:ORBTest_Exceptions/Intf:1.0" };

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

        if (name.equals("op_UNKNOWN_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            UNKNOWNHelper.insert(any, new UNKNOWN(1,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_BAD_PARAM_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            BAD_PARAMHelper.insert(any, new BAD_PARAM(2,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_NO_MEMORY_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            NO_MEMORYHelper.insert(any, new NO_MEMORY(3,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_IMP_LIMIT_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            IMP_LIMITHelper.insert(any, new IMP_LIMIT(4,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_COMM_FAILURE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            COMM_FAILUREHelper.insert(any, new COMM_FAILURE(5,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INV_OBJREF_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INV_OBJREFHelper.insert(any, new INV_OBJREF(6,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_NO_PERMISSION_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            NO_PERMISSIONHelper.insert(any, new NO_PERMISSION(7,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INTERNAL_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INTERNALHelper.insert(any, new INTERNAL(8,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_MARSHAL_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            MARSHALHelper.insert(any, new MARSHAL(9,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INITIALIZE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INITIALIZEHelper.insert(any, new INITIALIZE(10,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_NO_IMPLEMENT_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            NO_IMPLEMENTHelper.insert(any, new NO_IMPLEMENT(11,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_BAD_TYPECODE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            BAD_TYPECODEHelper.insert(any, new BAD_TYPECODE(12,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_BAD_OPERATION_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            BAD_OPERATIONHelper.insert(any, new BAD_OPERATION(13,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_NO_RESOURCES_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            NO_RESOURCESHelper.insert(any, new NO_RESOURCES(14,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_NO_RESPONSE_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            NO_RESPONSEHelper.insert(any, new NO_RESPONSE(15,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_BAD_INV_ORDER_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            BAD_INV_ORDERHelper.insert(any, new BAD_INV_ORDER(17,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_TRANSIENT_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            TRANSIENTHelper.insert(any, new TRANSIENT(18,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_OBJ_ADAPTER_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            OBJ_ADAPTERHelper.insert(any, new OBJ_ADAPTER(24,
                    CompletionStatus.COMPLETED_MAYBE));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_DATA_CONVERSION_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            DATA_CONVERSIONHelper.insert(any, new DATA_CONVERSION(25,
                    CompletionStatus.COMPLETED_YES));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_OBJECT_NOT_EXIST_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            OBJECT_NOT_EXISTHelper.insert(any, new OBJECT_NOT_EXIST(26,
                    CompletionStatus.COMPLETED_NO));
            request.set_exception(any);

            return;
        }

        if (name.equals("op_INV_POLICY_")) {
            if (!ex)
                throw new RuntimeException();

            NVList list = m_orb.create_list(0);
            request.arguments(list);

            Any any = m_orb.create_any();
            INV_POLICYHelper.insert(any, new INV_POLICY(30,
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

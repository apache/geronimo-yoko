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

class TestIntfStubTimeoutDSI_impl extends
        org.omg.PortableServer.DynamicImplementation {
    private ORB m_orb;

    private ORBTest_StubTimeout.Intf m_ti;

    TestIntfStubTimeoutDSI_impl(ORB orb, ORBTest_StubTimeout.Intf ti) {
        m_orb = orb;
        m_ti = ti;
    }

    static final String[] m_ids = { "IDL:ORBTest_StubTimeout/Intf:1.0" };

    public String[] _all_interfaces(org.omg.PortableServer.POA poa,
            byte[] object_id) {
        return m_ids;
    }

    public void invoke(ServerRequest request) {
        String name = request.operation();

        if (name.equals("sleep_oneway")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_ulong));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            int arg = any.extract_ulong();

            m_ti.sleep_oneway(arg);

            return;
        }

        if (name.equals("sleep_twoway")) {
            NVList list = m_orb.create_list(0);
            Any any = m_orb.create_any();
            any.type(m_orb.get_primitive_tc(TCKind.tk_ulong));
            list.add_value("", any, org.omg.CORBA.ARG_IN.value);
            request.arguments(list);
            int arg = any.extract_ulong();

            m_ti.sleep_twoway(arg);

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

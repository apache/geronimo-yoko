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
import ORBTest_Context.*;

final class TestIntfContext_impl extends ORBTest_Context.IntfPOA {
    private POA m_poa;

    public TestIntfContext_impl(POA poa) {
        m_poa = poa;
    }

    public synchronized String[] opContext(String pattern, Context ctx) {
        NVList values = ctx.get_values("", 0, pattern);

        int len = values.count();
        String[] result = new String[len * 2];
        for (int i = 0; i < len; i++) {
            try {
                String s = values.item(i).value().extract_string();
                result[i * 2] = values.item(i).name();
                result[i * 2 + 1] = s;
            } catch (org.omg.CORBA.Bounds ex) {
            }
        }

        return result;
    }

    public org.omg.PortableServer.POA _default_POA() {
        return m_poa;
    }
}

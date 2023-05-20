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

final class TestIntfExceptionsExt_2_3_impl extends
        ORBTest_ExceptionsExt_2_3.IntfPOA {
    private POA m_poa;

    public TestIntfExceptionsExt_2_3_impl(POA poa) {
        m_poa = poa;
    }

    public synchronized void op_CODESET_INCOMPATIBLE_Ex() {
        throw new CODESET_INCOMPATIBLE(31, CompletionStatus.COMPLETED_NO);
    }

    public synchronized void op_REBIND_Ex() {
        throw new REBIND(32, CompletionStatus.COMPLETED_NO);
    }

    public synchronized void op_TIMEOUT_Ex() {
        throw new TIMEOUT(33, CompletionStatus.COMPLETED_NO);
    }

    public synchronized void op_TRANSACTION_UNAVAILABLE_Ex() {
        throw new TRANSACTION_UNAVAILABLE(34, CompletionStatus.COMPLETED_NO);
    }

    public synchronized void op_TRANSACTION_MODE_Ex() {
        throw new TRANSACTION_MODE(35, CompletionStatus.COMPLETED_NO);
    }

    public synchronized void op_BAD_QOS_Ex() {
        throw new BAD_QOS(36, CompletionStatus.COMPLETED_NO);
    }

    public org.omg.PortableServer.POA _default_POA() {
        return m_poa;
    }
}

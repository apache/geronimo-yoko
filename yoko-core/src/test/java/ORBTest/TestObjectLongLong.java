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

public class TestObjectLongLong extends test.common.TestBase implements
        TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    public TestObjectLongLong(ORB orb, ORBTest.Intf test_intf) {
        m_orb = orb;
        m_test_intf = test_intf;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_LongLong.Intf ti = (ORBTest_LongLong.IntfHelper
                        .narrow(obj));
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_LongLong.Intf ti = ORBTest_LongLong.IntfHelper.narrow(obj);

        {
            long ret;
            ti.attrLongLong(-9223372036854775807L - 1);
            ret = ti.attrLongLong();
            assertTrue(ret == -9223372036854775807L - 1);

            ti.attrLongLong(9223372036854775807L);
            ret = ti.attrLongLong();
            assertTrue(ret == 9223372036854775807L);

            LongHolder inOut = new LongHolder(20);
            LongHolder out = new LongHolder();
            ret = ti.opLongLong(10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            long ret;
            ti.attrULongLong(9223372036854775807L);
            ret = ti.attrULongLong();
            assertTrue(ret == 9223372036854775807L);

            LongHolder inOut = new LongHolder(20);
            LongHolder out = new LongHolder();
            ret = ti.opULongLong(10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            LongHolder inOut = new LongHolder(20);
            LongHolder out = new LongHolder();

            try {
                ti.opLongLongEx(10, inOut, out);
                assertTrue(false);
            } catch (ORBTest_LongLong.ExLongLong ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            LongHolder inOut = new LongHolder(20);
            LongHolder out = new LongHolder();

            try {
                ti.opULongLongEx(10, inOut, out);
                assertTrue(false);
            } catch (ORBTest_LongLong.ExULongLong ex) {
                assertTrue(ex.value == 30);
            }
        }

    }

    public void test_DII(org.omg.CORBA.Object obj) {
        // REVISIT
    }
}

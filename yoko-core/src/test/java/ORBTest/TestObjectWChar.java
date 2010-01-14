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

public class TestObjectWChar extends test.common.TestBase implements TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    public TestObjectWChar(ORB orb, ORBTest.Intf test_intf) {
        m_orb = orb;
        m_test_intf = test_intf;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_WChar.Intf ti = ORBTest_WChar.IntfHelper.narrow(obj);
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_WChar.Intf ti = ORBTest_WChar.IntfHelper.narrow(obj);

        {
            char ret;
            ti.attrWChar('a');
            ret = ti.attrWChar();
            TEST(ret == 'a');
            ti.attrWChar((char) 224);
            ret = ti.attrWChar();
            TEST(ret == (char) 224);
            ti.attrWChar((char) 0x20ac);
            ret = ti.attrWChar();
            TEST(ret == (char) 0x20ac);

            CharHolder inOut = new CharHolder((char) 1);
            CharHolder out = new CharHolder();
            ret = ti.opWChar('a', inOut, out);
            TEST(ret == 'b');
            TEST(inOut.value == 'b');
            TEST(out.value == 'b');
        }

        {
            String ret;

            ti.attrWString("");
            ret = ti.attrWString();
            TEST(ret.equals(""));

            ti.attrWString("Hello");
            ret = ti.attrWString();
            TEST(ret.equals("Hello"));

            StringHolder inOut = new StringHolder("world!");
            StringHolder out = new StringHolder();
            ret = ti.opWString("Hello, ", inOut, out);
            TEST(ret.equals("Hello, world!"));
            TEST(out.value.equals("Hello, world!"));
        }

        {
            CharHolder inOut = new CharHolder((char) 1);
            CharHolder out = new CharHolder();

            try {
                ti.opWCharEx('a', inOut, out);
                TEST(false);
            } catch (ORBTest_WChar.ExWChar ex) {
                TEST(ex.value == 'b');
            }
        }

        {
            StringHolder inOut = new StringHolder("world!");
            StringHolder out = new StringHolder();

            try {
                ti.opWStringEx("Hello, ", inOut, out);
                TEST(false);
            } catch (ORBTest_WChar.ExWString ex) {
                TEST(ex.value.equals("Hello, world!"));
            }
        }

    }

    public void test_DII(org.omg.CORBA.Object obj) {
        // REVISIT
    }
}

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

import static org.junit.Assert.assertTrue;

import org.omg.CORBA.*;

public class TestObjectContext extends test.common.TestBase implements
        TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    public TestObjectContext(ORB orb, ORBTest.Intf test_intf) {
        m_orb = orb;
        m_test_intf = test_intf;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_Context.Intf ti = ORBTest_Context.IntfHelper
                        .narrow(obj);
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_Context.Intf ti = ORBTest_Context.IntfHelper.narrow(obj);

        Context ctx;
        Context ctx2;

        {
            ctx = m_orb.get_default_context();

            String[] seq;

            try {
                seq = ti.opContext("*", ctx);
                assertTrue(false);
            } catch (BAD_CONTEXT ex) {
                // Expected
            }
        }

        {
            Any any = m_orb.create_any();
            int i;
            String[] seq;

            ctx = m_orb.get_default_context();

            any.insert_string("A1");
            ctx.set_one_value("A", any);

            any.insert_string("A2");
            ctx.set_one_value("AAA", any);

            any.insert_string("B1");
            ctx.set_one_value("B", any);

            any.insert_string("B2");
            ctx.set_one_value("BBB", any);

            any.insert_string("X1");
            ctx.set_one_value("X", any);

            any.insert_string("X2");
            ctx.set_one_value("XXX", any);

            any.insert_string("Y1");
            ctx.set_one_value("Y", any);

            any.insert_string("Y2");
            ctx.set_one_value("YYY", any);

            seq = ti.opContext("*", ctx);
            assertTrue(seq.length == 3 * 2);
            for (i = 0; i < seq.length; i += 2) {
                if (seq[i].equals("A"))
					assertTrue(seq[i + 1].equals("A1"));

                if (seq[i].equals("AAA"))
					assertTrue(seq[i + 1].equals("A2"));

                if (seq[i].equals("X"))
					assertTrue(seq[i + 1].equals("X1"));
            }

            seq = ti.opContext("A*", ctx);
            assertTrue(seq.length == 2 * 2);
            for (i = 0; i < seq.length; i += 2) {
                if (seq[i].equals("A"))
					assertTrue(seq[i + 1].equals("A1"));

                if (seq[i].equals("AAA"))
					assertTrue(seq[i + 1].equals("A2"));
            }

            seq = ti.opContext("AA*", ctx);
            assertTrue(seq.length == 1 * 2);
            assertTrue(seq[0].equals("AAA") && seq[1].equals("A2"));

            seq = ti.opContext("A", ctx);
            assertTrue(seq.length == 1 * 2);
            assertTrue(seq[0].equals("A") && seq[1].equals("A1"));

            ctx2 = ctx.create_child("child");

            any.insert_string("C1");
            ctx2.set_one_value("C", any);

            any.insert_string("C2");
            ctx2.set_one_value("CCC", any);

            any.insert_string("X1-1");
            ctx2.set_one_value("X", any);

            seq = ti.opContext("*", ctx2);
            assertTrue(seq.length == 5 * 2);
            for (i = 0; i < seq.length; i += 2) {
                if (seq[i].equals("A"))
					assertTrue(seq[i + 1].equals("A1"));

                if (seq[i].equals("AAA"))
					assertTrue(seq[i + 1].equals("A2"));

                if (seq[i].equals("C"))
					assertTrue(seq[i + 1].equals("C1"));

                if (seq[i].equals("CCC"))
					assertTrue(seq[i + 1].equals("C2"));

                if (seq[i].equals("X"))
					assertTrue(seq[i + 1].equals("X1-1"));
            }
        }
    }

    public void test_DII(org.omg.CORBA.Object obj) {
        ORBTest_Context.Intf ti = ORBTest_Context.IntfHelper.narrow(obj);

        Context ctx;

        {
            ctx = m_orb.get_default_context();

            Request request;

            request = ti._request("opContext");
            request.contexts().add("A*");
            request.contexts().add("C*");
            request.contexts().add("X");
            request.contexts().add("Z");
            request.ctx(ctx);
            request.add_in_arg().insert_string("*");
            request
                    .set_return_type(ORBTest_Context.StringSequenceHelper
                            .type());
            try {
                request.invoke();
                Exception ex = request.env().exception();
                assertTrue(ex != null);
                BAD_CONTEXT bex = (BAD_CONTEXT) ex;
            } catch (BAD_CONTEXT ex) {
                // Expected (if yoko.m_orb.raise_dii_exceptions = true)
            }
        }

        {
            ctx = m_orb.get_default_context();

            Any any = m_orb.create_any();
            int i;

            any.insert_string("A1");
            ctx.set_one_value("A", any);

            any.insert_string("A2");
            ctx.set_one_value("AAA", any);

            any.insert_string("B1");
            ctx.set_one_value("B", any);

            any.insert_string("B2");
            ctx.set_one_value("BBB", any);

            any.insert_string("X1");
            ctx.set_one_value("X", any);

            any.insert_string("X2");
            ctx.set_one_value("XXX", any);

            any.insert_string("Y1");
            ctx.set_one_value("Y", any);

            any.insert_string("Y2");
            ctx.set_one_value("YYY", any);

            Request request;
            String[] seq;

            request = ti._request("opContext");
            request.contexts().add("A*");
            request.contexts().add("C*");
            request.contexts().add("X");
            request.contexts().add("Z");
            request.ctx(ctx);
            request
                    .set_return_type(ORBTest_Context.StringSequenceHelper
                            .type());
            request.add_in_arg().insert_string("*");
            request.invoke();
            seq = (ORBTest_Context.StringSequenceHelper.extract(request
                    .return_value()));

            assertTrue(seq.length == 3 * 2);
            for (i = 0; i < seq.length; i += 2) {
                if (seq[i].equals("A"))
					assertTrue(seq[i + 1].equals("A1"));

                if (seq[i].equals("AAA"))
					assertTrue(seq[i + 1].equals("A2"));

                if (seq[i].equals("X"))
					assertTrue(seq[i + 1].equals("X1"));
            }

            request = ti._request("opContext");
            request.contexts().add("A*");
            request.contexts().add("C*");
            request.contexts().add("X");
            request.contexts().add("Z*");
            request.ctx(ctx);
            request
                    .set_return_type(ORBTest_Context.StringSequenceHelper
                            .type());
            request.add_in_arg().insert_string("A*");
            request.invoke();
            seq = (ORBTest_Context.StringSequenceHelper.extract(request
                    .return_value()));

            assertTrue(seq.length == 2 * 2);
            for (i = 0; i < seq.length; i += 2) {
                if (seq[i].equals("A"))
					assertTrue(seq[i + 1].equals("A1"));

                if (seq[i].equals("AAA"))
					assertTrue(seq[i + 1].equals("A2"));
            }

            request = ti._request("opContext");
            request.contexts().add("A*");
            request.contexts().add("C*");
            request.contexts().add("X");
            request.contexts().add("Z");
            request.ctx(ctx);
            request
                    .set_return_type(ORBTest_Context.StringSequenceHelper
                            .type());
            request.add_in_arg().insert_string("AA*");
            request.invoke();
            seq = (ORBTest_Context.StringSequenceHelper.extract(request
                    .return_value()));

            assertTrue(seq.length == 1 * 2);
            assertTrue(seq[0].equals("AAA") && seq[1].equals("A2"));

            request = ti._request("opContext");
            request.contexts().add("A*");
            request.contexts().add("C*");
            request.contexts().add("X");
            request.contexts().add("Z");
            request.ctx(ctx);
            request
                    .set_return_type(ORBTest_Context.StringSequenceHelper
                            .type());
            request.add_in_arg().insert_string("A");
            request.invoke();
            seq = (ORBTest_Context.StringSequenceHelper.extract(request
                    .return_value()));

            assertTrue(seq.length == 1 * 2);
            assertTrue(seq[0].equals("A") && seq[1].equals("A1"));
        }
    }
}

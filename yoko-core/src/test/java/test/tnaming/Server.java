/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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

/**
 * @version $Rev: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */

package test.tnaming;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

final class Server extends test.common.TestBase implements AutoCloseable {
    private static final NameComponent LEVEL1 = new NameComponent("level1", "test");
    private static final NameComponent LEVEL2 = new NameComponent("level2", "");

    private static final NameComponent TEST1 = new NameComponent("Test1", "");
    private static final NameComponent TEST2 = new NameComponent("Test2", "");
    private static final NameComponent TEST3 = new NameComponent("Test3", "");

    final ORB orb;
    final POA rootPoa;
    final NamingContextExt rootNamingContext;
    final Test test1, test2, test3;

    public Server(Properties props, String... args) throws Exception {
        try {
            System.out.println("About to init ORB");
            this.orb = ORB.init(args, props);
            System.out.println("create ORB");
            this.rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            System.out.println("got root poa");
            this.rootPoa.the_POAManager().activate();
            System.out.println("activated root poa");
            this.rootNamingContext = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
            System.out.println("got root context");

            //
            // Create implementation objects
            //
            test1 = TestHelper.narrow(new Test_impl(rootPoa, "Test1")._this_object(orb));
            test2 = TestHelper.narrow(new Test_impl(rootPoa, "Test2")._this_object(orb));
            test3 = TestHelper.narrow(new Test_impl(rootPoa, "Test3")._this_object(orb));
            System.out.println("created references");
        } catch (Throwable t) {
            System.err.println("Caught throwable: " + t);
            t.printStackTrace();
            throw t;
        }
    }

    void run(String refFile) throws Exception {
        System.out.println("server starting to run");
        try (PrintWriter out = new PrintWriter(new FileWriter(refFile))) {
            System.out.println("server opened file for writing");
            try {
                NamingContext nc1 = rootNamingContext.new_context();
                System.out.println("server created new naming context");

                System.out.println("Binding context level1");
                rootNamingContext.bind_context(new NameComponent[]{LEVEL1}, nc1);
                System.out.println("server binding context");

                NamingContext nc2 = rootNamingContext.bind_new_context(new NameComponent[]{LEVEL1, LEVEL2});

                Util.assertNameNotBound(rootNamingContext, TEST1);

                Util.assertNameNotBound(rootNamingContext, TEST1);

                rootNamingContext.bind(new NameComponent[]{TEST1}, test1);
                Util.assertTestIsBound("Test1", rootNamingContext, TEST1);

                nc1.bind(new NameComponent[]{TEST2}, test2);
                Util.assertTestIsBound("Test2", rootNamingContext, LEVEL1, TEST2);

                rootNamingContext.bind(new NameComponent[]{LEVEL1, LEVEL2, TEST3}, test3);
                Util.assertTestIsBound("Test3", rootNamingContext, LEVEL1, LEVEL2, TEST3);

                Test test3a = TestHelper.narrow(new Test_impl(rootPoa, "Test3a")._this_object(orb));
                nc2.rebind(new NameComponent[]{TEST3}, test3a);
                Util.assertTestIsBound("Test3a", rootNamingContext, LEVEL1, LEVEL2, TEST3);

                rootNamingContext.unbind(new NameComponent[]{LEVEL1, LEVEL2, TEST3});
                Util.assertNameNotBound(nc2, TEST3);

                nc2.bind(new NameComponent[]{TEST3}, test3);
                Util.assertTestIsBound("Test3", rootNamingContext, LEVEL1, LEVEL2, TEST3);

                nc1.unbind(new NameComponent[]{LEVEL2});
                Util.assertNameNotBound(rootNamingContext, LEVEL1, LEVEL2, TEST3);

                nc1.rebind_context(new NameComponent[]{LEVEL2}, nc2);
            } catch (Exception e) {
                e.printStackTrace(out);
                throw e;
            }
            //
            // Save reference. This must be done after POA manager
            // activation, otherwise there is a potential for a race
            // condition between the client sending a request and the
            // server not being ready yet.
            //
            writeRef(orb, out, test1, rootNamingContext, new NameComponent[]{TEST1});
            writeRef(orb, out, test2, rootNamingContext, new NameComponent[]{LEVEL1, TEST2});
            writeRef(orb, out, test3, rootNamingContext, new NameComponent[]{LEVEL1, LEVEL2, TEST3});
            out.flush();
        } catch (java.io.IOException ex) {
            System.err.println("Can't write to `" + ex.getMessage() + "'");
            throw ex;
        }

        orb.run();
    }

    @Override
    public void close() throws Exception {
        try {
            Util.unbindEverything(rootNamingContext);
        } finally {
            orb.destroy();
        }
    }
}

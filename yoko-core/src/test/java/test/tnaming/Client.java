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
 *  Unless required by applicable law or agreed to in writing, softwares
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @version $Rev: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */

package test.tnaming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static test.tnaming.Client.NameServiceType.READ_ONLY;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

final class Client extends test.common.TestBase implements AutoCloseable {
    enum NameServiceType {
        READ_ONLY, INTEGRAL, STANDALONE
    };
    final NameServiceType accessibility;
    final ORB orb;
    final POA rootPoa;
    final NamingContextExt rootNamingContext;
    final String name1, name2, name3;
    final Test test1, test2, test3;

    Client(NameServiceType accessibility, final String refFile, Properties props, String... args) throws Exception {
        assertNotNull(accessibility);
        this.accessibility = accessibility;
        this.orb = ORB.init(args, props);
        this.rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
        this.rootPoa.the_POAManager().activate();
        this.rootNamingContext = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
        //
        // Get "test" objects
        //
        System.out.println("Started ORB, getting test object IORs from file");
        try (BufferedReader file = openFileReader(refFile)) {
            String[] refStrings = new String[2];
            readRef(file, refStrings);
            test1 = getTestObjectFromReference(refStrings[0]);
            name1 = refStrings[1];

            readRef(file, refStrings);
            test2 = getTestObjectFromReference(refStrings[0]);
            name2 = refStrings[1];

            readRef(file, refStrings);
            test3 = getTestObjectFromReference(refStrings[0]);
            name3 = refStrings[1];
        } catch (java.io.IOException ex) {
            System.err.println("Can't read from '" + ex.getMessage() + "'");
            throw ex;
        }
    }

    private BufferedReader openFileReader(final String refFile) throws FileNotFoundException {
        return new BufferedReader(new FileReader(refFile)) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    Files.delete(Paths.get(refFile));
                }
            }
        };
    }

    private Test getTestObjectFromReference(String ref) {
        assertNotNull("Reference should not have been null", ref);
        org.omg.CORBA.Object obj = orb.string_to_object(ref);
        assertNotNull("Orb should have created a non-null stub", obj);
        Test result = TestHelper.narrow(obj);
        assertNotNull("Should have been able to narrow stub to a Test", result);
        return result;
    }

    void run() throws Exception {
        System.out.println("Running naming client tests");
        switch (accessibility) {
            case READ_ONLY :
                testReadOnly();
                testBoundReferences();
                testIterators();
                testObjectFactories();
                break;
            case INTEGRAL:
                testBoundReferences();
                testIterators();
                testObjectFactories();
                break;
            case STANDALONE:
            	testBoundReferences();
                testIterators();
                break;
        }
    }

    private void testBoundReferences() throws UserException {
        System.out.println("Testing bound reference 1");
        Util.assertTestIsBound(test1, rootNamingContext, name1);
        System.out.println("Testing bound reference 2");
        Util.assertTestIsBound(test2, rootNamingContext, name2);
        System.out.println("Testing bound reference 3");
        Util.assertTestIsBound(test3, rootNamingContext, name3);
        System.out.println("Testing bound reference 1 via corbaname");
        Util.assertCorbanameIsBound(test1, orb, "corbaname:rir:/NameService#"+name1);
        System.out.println("Testing bound references complete.");
    }

    private void testIterators() throws Exception {
        System.out.println("Testing iterators: narrowing context");
        NamingContextExt nc = NamingContextExtHelper.narrow(rootNamingContext.resolve(Util.ITERATOR_TEST_CONTEXT_PATH));
        // check the behaviour of the binding iterators
        for (int listSize = 0; listSize <= Util.EXPECTED_NAMES.size() + 1; listSize++) {
            System.out.println("Testing iterators: list size " + listSize);
            final BindingListHolder blh = new BindingListHolder();
            final BindingIteratorHolder bih = new BindingIteratorHolder();
            final BindingHolder bh = new BindingHolder();
            final Set<String> actualNames = new TreeSet<>();

            nc.list(listSize, blh, bih); // <-- this is what we are testing

            System.out.println("List returned count = " + blh.value.length);

            final int expectedListSize = Math.min(listSize, Util.EXPECTED_NAMES.size());
            assertEquals("Should have as many elements in the list as requested or available", expectedListSize,
                    blh.value.length);
            for (Binding b : blh.value) {
                String name = b.binding_name[0].id;
                assertTrue("Name '" + name + "' should be an expected one", Util.EXPECTED_NAMES.contains(name));
                assertFalse("Name '" + name + "' should not be a dupe", actualNames.contains(name));
                actualNames.add(name);
            }
            for (int i = expectedListSize; i < Util.EXPECTED_NAMES.size(); i++) {
                assertTrue(bih.value.next_one(bh));
                String name = bh.value.binding_name[0].id;
                assertTrue("Name '" + name + "' should be an expected one", Util.EXPECTED_NAMES.contains(name));
                assertFalse("Name '" + name + "' should not be a dupe", actualNames.contains(name));
                actualNames.add(name);
            }
            assertFalse(bih.value.next_one(bh));
            assertEquals(0, bh.value.binding_name.length);
            assertEquals(Util.EXPECTED_NAMES, actualNames);
        }
        System.out.println("Testing iterators complete.");
    }

    private enum WriteMethod {
        bind, bind_context, bind_new_context, destroy, new_context, rebind_context, rebind, unbind
    };

    private void testReadOnly() throws Exception {
        final NameComponent[] foo = Util.makeName("foo");
        for (WriteMethod method : WriteMethod.values()) {
            try {
                switch (method) {
                    case bind :
                        rootNamingContext.bind(foo, rootNamingContext);
                        break;
                    case bind_context :
                        rootNamingContext.bind_context(foo, rootNamingContext);
                        break;
                    case bind_new_context :
                        rootNamingContext.bind_new_context(foo);
                        break;
                    case destroy :
                        rootNamingContext.destroy();
                        break;
                    case new_context :
                        rootNamingContext.new_context();
                        break;
                    case rebind :
                        rootNamingContext.rebind(foo, rootNamingContext);
                        break;
                    case rebind_context :
                        rootNamingContext.rebind_context(foo, rootNamingContext);
                        break;
                    case unbind :
                        rootNamingContext.unbind(foo);
                        break;
                }
                Assert.fail(method + " should have thrown a NO_PERMISSION exception");
            } catch (NO_PERMISSION expected) {
                System.out.println(method + "() threw a NO_PERMISSION as expected.");
            }
        }
    }

    public void testObjectFactories() throws CannotProceed, InvalidName {
        System.out.println("Testing object factories: resolvable");
        Util.assertFactoryIsBound(rootNamingContext, Server.RESOLVABLE_TEST);
        System.out.println("Testing object factories: resolver");
        Util.assertFactoryIsBound(rootNamingContext, Server.RESOLVER_TEST);
        System.out.println("Testing object factories complete.");
    }

    @Override
    public void close() throws Exception {
        try {
            if (accessibility != READ_ONLY)
                Util.unbindEverything(rootNamingContext);
        } finally {
            try {
                test1.shutdown();
            } finally {
                orb.destroy();
            }
        }
    }
}

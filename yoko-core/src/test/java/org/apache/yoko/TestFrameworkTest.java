/*
 * Copyright 2019 IBM Corporation and others.
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
package org.apache.yoko;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This test is only to ensure the framework is correctly reporting failures in
 * server and client processes.
 * @author nrichard
 */
public class TestFrameworkTest extends AbstractOrbTestBase {
    private static final String TEST_FILE = "FrameworkTest.txt";

    public void setUp() throws Exception {
        super.setUp();
        setWaitForFile(TEST_FILE);
        System.setProperty("server.forked", "true");
        System.setProperty("client.forked", "true");
    }

    public void tearDown() throws Exception {
        super.tearDown();
        System.getProperties().remove("server.forked");
        System.getProperties().remove("client.forked");
    }

    public void testGoodClasses() throws Exception {
        runServerClientTest(GoodServer.class, GoodClient.class);
    }

    public void testBadClasses() {
        try {
            runServerClientTest(BadServer.class, BadClient.class);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertRootCause(Exception.class, e);
        }
    }

    public void testWorseClasses() {
        try {
            runServerClientTest(WorseServer.class, WorseClient.class);
            fail("Should have thrown an error");
        } catch (Exception e) {
            assertRootCause(Error.class, e);
        }
    }

    public void testBadServer() {
        try {
            runServerClientTest(BadServer.class, GoodClient.class);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertRootCause(Exception.class, e);
        }
    }

    public void testWorseServer() {
        try {
            runServerClientTest(WorseServer.class, GoodClient.class);
            fail("Should have thrown an error");
        } catch (Exception e) {
            assertRootCause(Error.class, e);
        }
    }

    public void testBadClient() {
        try {
            runServerClientTest(GoodServer.class, BadClient.class);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertRootCause(Exception.class, e);
        }
    }

    public void testWorseClient() {
        try {
            runServerClientTest(GoodServer.class, WorseClient.class);
            fail("Should have thrown an error");
        } catch (Exception e) {
            assertRootCause(Error.class, e);
        }
    }

    private void assertRootCause(Class<? extends Throwable> expectedExceptionClass, Throwable t) {
        while (t.getCause() != null)
            t = t.getCause();
        assertEquals(expectedExceptionClass, t.getClass());
    }

    public static final class GoodServer {

        public static void main(String[] args) throws Exception {
            try (FileWriter fw = new FileWriter(TEST_FILE)) {
                try (PrintWriter pw = new PrintWriter(fw)) {
                    pw.println("Hello, World.");
                }
            }
        }
    }

    public static final class GoodClient {
        public static void main(String[] args) throws Exception {
            Files.delete(Paths.get(TEST_FILE));
        }
    }

    public static final class BadServer {
        public static void main(String[] args) throws Exception {
            GoodServer.main(args);
            throw new Exception();
        }
    }

    public static final class BadClient {
        public static void main(String[] args) throws Exception {
            GoodClient.main(args);
            throw new Exception();
        }
    }

    public static final class WorseServer {
        public static void main(String[] args) throws Exception {
            GoodServer.main(args);
            throw new Error();
        }
    }

    public static final class WorseClient {
        public static void main(String[] args) throws Exception {
            GoodClient.main(args);
            throw new Error();
        }
    }

    public static final class WorstServer {
        public static void main(String[] args) throws Exception {
            GoodServer.main(args);
            System.exit(1);
        }
    }

    public static final class WorstClient {
        public static void main(String[] args) throws Exception {
            GoodClient.main(args);
            System.exit(2);
        }
    }

}

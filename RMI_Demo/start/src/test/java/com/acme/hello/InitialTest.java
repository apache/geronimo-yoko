/*
 * Copyright 2022 IBM Corporation and others.
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
package com.acme.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.rmi.Naming;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class InitialTest {
    static int port = 1099;
    private static String lookupURL;

    @BeforeAll
    static void setup() {
        lookupURL = "//localhost:" + port + "/MessengerService";
    }

    // This method runs in the server process
    @BeforeAll
    private static void logging() {
        // Next 6 lines specific to RMI serialization and logging
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);
        consoleHandler.setFormatter(new SimpleFormatter());
        final Logger serial = Logger.getLogger("java.io.serialization");
        serial.setLevel(Level.FINEST);
        serial.addHandler(consoleHandler);
    }

    @Test
    void testHello() throws Exception {
        logging();
        Hello obj = (Hello) Naming.lookup(lookupURL);         //objectname in registry
        System.out.println(obj.sayHello());
    }

    @Test
    void testSetGreeting() throws Exception {
        Hello hello = (Hello) Naming.lookup(lookupURL);
        hello.setGreeting("Good day!");
        Assertions.assertEquals("Good day!", hello.sayHello());
    }

}

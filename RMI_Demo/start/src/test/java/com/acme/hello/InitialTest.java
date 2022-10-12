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

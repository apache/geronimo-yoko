package com.acme.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testify.bus.Bus;
import testify.bus.TypeSpec;
import testify.jupiter.annotation.ConfigurePartRunner;
import testify.parts.PartRunner;

import java.rmi.Naming;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static com.acme.hello.InitialTest.ServerEvent.*;

@ConfigurePartRunner
public class InitialTest {
    static int port;
    private static String lookupURL;

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

    @BeforeAll
    static void setup(PartRunner runner) {
        // When forking use new Java Virtual Machine
        runner.useNewJVMWhenForking();
        // Create a new part called HelloServer
        runner.fork("HelloServer",
                // Tell Testify how to start this new part
                InitialTest::runServer,
                // Tell Testify how to stop this part
                bus -> bus
                        // Send a stp request
                        .put(STOP_REQUESTED, 0)
                        // Wait for stopped server response
                        .get(SERVER_STOPPED));
        // Wait for the server to start
        port = runner.bus("HelloServer").get(SERVER_STARTED);
        lookupURL = "//localhost:" + port + "/MessengerService";
        System.out.println(lookupURL);
    }

    // This method runs in the server process
    private static void runServer(Bus bus) {
        logging();
        // Notify the test process that the server has now started on a particular port
        bus.put(SERVER_STARTED, port);
        System.out.println(port);
        // Get STOP_REQUESTED event from the bus
        bus.get(STOP_REQUESTED);
        // call stop method from HelloServer class
        HelloServer.stop();
        // Send SERVER_STOPPED event and port back to the client
        bus.put(SERVER_STOPPED, port);
    }

    enum ServerEvent implements TypeSpec<Integer> { SERVER_STARTED, STOP_REQUESTED, SERVER_STOPPED }

    @Test
    void testHello() throws Exception {
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

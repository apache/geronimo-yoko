# Implementing Testify to a RMI Java Application

This guide will explain and allow you to implement Testify to a Remote Method Invocation (RMI) application.

## Steps to build Testify
1. Clone the following Yoko repository and go to the directory RMI in the root directory of the repository.

        git clone https://github.com/Testibus-Team4/yoko.git
        cd yoko

2. Navigate to `RMI_Demo/start` directory using the interface of your chosen IDE and also in the terminal using the command folowing command from the yoko directory.

        cd RMI_Demo/start

3. Find the `build.gradle` within that repository, and add the following code to the dependencies section of the file:

        testImplementation 'org.apache.yoko:yoko-testify:1.5.0.9cce293956'

    Your build.gradle file should now look like this:

        dependencies {
            testImplementation 'org.apache.yoko:yoko-testify:1.5.0.9cce293956'
            testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
            testRuntimeOnly 'org.junit.platform:junit-platform-runner:1.8.2'
            testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.2'
        }
    
    The dependency will be added to the project and the Testify library will be available for use.


4. Refresh the `build.gradle` dependencies using the following command:

        gradle --refresh-dependencies

5. Navigate to `/src/test/java/com/acme/hello/InitialTest.java` using your IDE interface. This is the file that will contain the tests.

6. Add the following imports into `IntitialTest.java`:

        import testify.bus.Bus;
        import testify.bus.TypeSpec;
        import testify.jupiter.annotation.ConfigurePartRunner;
        import testify.parts.PartRunner;

7. Next lets add the annotation to the `InitialTest.java` class. Do this by adding the following code above the class declaration:

        @ConfigurePartRunner(PartRunner.class)

8. Now lets expand the setup method to include the following code:

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

9. The above code will call on the `PartRunner` parameter. This allows us to use the methods within part runner and call on and use `useNewJVMWhenForking()` and also `fork()`. `useNewJVMWhenForking()` name means to use a new Java Virtual Machine every time something is forked. `fork()` calls upon the `runServer()` method.

        // This method runs in the server process
        private static void runServer(Bus bus) {
            logging();
            int port = HelloServer.start(0);
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

    Also; now you can go ahead and remove the call for `logging()` from the `testHello()` method. This is because you have now called it in the `runServer()` method.

    The above code will call on the `Bus` parameter. This allows us to use the methods within the bus and call on and use `put()` and also `get()`. `put()` will put the `SERVER_STARTED` event and the port number on the bus. `get()` will get the `STOP_REQUESTED` event from the bus. This allows the client and server communicate with each other with more detail. Using Bus also allows us to know how long a server takes to start, when the client starts to interact with the server and also when the server shuts down.


10. Since you are now able to send events and messages between the client and server; you can send the port across too. This means you can go ahead and replace:

            static int port = 1099;

    with:

            static int port;

11. You are now ready to build your application using gradle and see how Testify has helped testing. In the start` repository, run either of following commands:
    
    - To build with tests use:

            ./gradlew build

    - To build without tests use:

            ./gradlew build -x test

## How Testify works in InitialTest.java

        import testify.bus.Bus;
        import testify.bus.TypeSpec;
        import testify.jupiter.annotation.ConfigurePartRunner;
        import testify.parts.PartRunner;

These imports enable Testify in your testing. The annotation you are using from Testify is `@ConfigurePartRunner`. This annoation allows us to make use of runner.`useNewJVMWhenForking()` and `runner.fork()`. These runners allow you to run methods from your app/project within the testing. The `@ConfigurePartRunner `annotation is further explained in the [ConfigurePartRunner](/pages/annotations/part-runner) section.

In `IntitialTest.java` the tests are sending and getting events from methods using the [bus](/pages/bus/bus-concept). Thanks to these events you are making sure that a new instruction is not run until the previous one complete and the correct values are shared across processes/threads. This is done using TypeSpecs which are Testify specific. TypeSpecs are enums that you can call. In this case these are the events. Thanks to these TypeSpecs you are able to know:

- How long a server took to start (SERVER_STARTED)
- When the client starts to interact with the server
- When the server can shut down (STOP_REQUESTED)
- When the server shut down (SERVER_STOPPED)

This is all done in the `setup()` method which also is annotated using `@BeforeAll` - this means that nothing will happen until this method is completed. The most important method is `runServer()` which takes in bus as a parameter. In this method everything is declared and then passed to the `setup()` method inside the `runner.fork()` method as a parameter.
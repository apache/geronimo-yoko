Hello World Demo
================

Prerequisites
=============

If your environment already includes yoko-${current-yoko-version}.jar on the
CLASSPATH, and the JDK and ant bin directories on the PATH
it is not necessary to run the environment script described in
the samples directory README.  If your environment is not
properly configured, or if you are planning on using wsdl2idl/idl2wsdl,
javac, and java to build and run the demos, you must include the 
yoko-${current-yoko-version}.jar in the CLASSPATH. Also set 
YOKO_HOME to the installation directory.

Demo Use Cases
==============

This demo allows the user to run three use cases.

- Case 1: Web services client & Web services server.

  In this use case, a Web services client talks to a Web services server
through the IIOP protocol. Both the client and the server are implemented using
the CXF Web services framework and the IIOP protocol is enabled by installing
the Yoko CORBA binding into CXF.

  Note: CXF is _not_ part of the Yoko product, but a CXF kit is bundled
with Yoko for convenience of testing and running the Web services demos.

- Case 2: Web services client & CORBA server.

  In this use case, a Web services client talks to a CORBA server through the
IIOP protocol.  The client, which is implemented using CXF, loads the
Yoko CORBA binding to enable the IIOP protocol. The server is implemented using
the Yoko ORB.

This use case illustrates how a Web services client can be configured to access
a CORBA server, by converting the server's OMG IDL interface into a WSDL interface
with a CORBA binding.

- Case 3: CORBA client & Web services server.

  In this use case, a CORBA client talks to a Web services server through the
IIOP protocol. The client is implemented using the Yoko ORB. The server, which is
implemented using CXF, loads the Yoko CORBA binding to enable the IIOP protocol.

This use case illustrates how a Web service can be made accessible to CORBA clients,
by exposing the service through an OMG IDL interface that can be accessed
using the IIOP protocol.


Building the Demo
=================

To build the demo code, perform the following steps:

1. Open a command prompt and move into the directory <YOKO_HOME>/samples/ws/hello_world

2. Enter the following command (UNIX or Windows):

     ant generate.corba.wsdl

   This command adds a CORBA binding to the samples/ws/resources/HelloWorld.wsdl file,
   generating an output file, HelloWorld-corba.wsdl.

3. Open the HelloWorld-corba.wsdl file using your favorite text editor and add the
   following service definition in the scope of the wsdl:definitions element:
  
   <wsdl:service name="HelloWorldCORBAService">
     <wsdl:port name="HelloWorldCORBAPort" binding="tns:HelloWorldCORBABinding">
        <corba:address location="corbaloc::localhost:40000/hw" />
     </wsdl:port>
   </wsdl:service>

   The address specified here, localhost:40000, assumes that the client and server
   both run on the same host. If you change this address, you should make sure it is
   consistent with the address coded in the
   hello_world/src/yoko/server/Server.java, hello_world/src/corba/server/Server.java and
   hello_world/src/corba/client/Client.java files.

4. From the samples/ws/hello_world directory, enter the following command (UNIX or Windows):

     ant build

  This command uses the CXF wsdl2java utility to generate the server & client code.


Running the Demo - Use Case 1
=============================

To run the demo for the first use case, perform the following steps:

1. Open a command prompt and move into the directory <YOKO_HOME>/samples/ws/hello_world

2. Enter the following command to start the server (on a single command line):

   UNIX (must use forward slashes):

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath $CLASSPATH:build/classes yoko.server.Server &

   Windows (may use either forward or back slashes):

     start java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath %CLASSPATH%;build\classes yoko.server.Server

  The server process starts in the background. We have to set the Xbootclasspath,
  because the ORB classes in Yoko conflict with the JDK ORB classes.

3. Enter the following command to start the client, substituting <Name> with your name:

   UNIX:

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath $CLASSPATH:build/classes yoko.client.Client <Name>

   Windows:

     java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar
        -classpath %CLASSPATH%;build\classes yoko.client.Client <Name>

4. After running the client, use the kill command to terminate the server process (UNIX) or
   type Ctrl-C in the server's command window (Windows).


Running the Demo - Use Case 2
=============================

To run the demo for the second use case, perform the following steps:

1. Open a command prompt and move into the directory <YOKO_HOME>/samples/ws/hello_world

2. Enter the following command to start the CORBA server (on a single command line):

   UNIX (must use forward slashes):

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath $CLASSPATH:build/classes corba.server.Server &

   Windows (may use either forward or back slashes):

     start java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath %CLASSPATH%;build\classes corba.server.Server

  The server process starts in the background. We have to set the Xbootclasspath,
  because the ORB classes in Yoko conflict with the JDK ORB classes.

3. Enter the following command to start the Web services client, substituting <Name> with your name:

   UNIX:

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath $CLASSPATH:build/classes yoko.client.Client <Name>

   Windows:

     java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar
        -classpath %CLASSPATH%;build\classes yoko.client.Client <Name>

4. After running the client, use the kill command to terminate the server process (UNIX) or
   type Ctrl-C in the server's command window (Windows).


Running the Demo - Use Case 3
=============================

To run the demo for the third use case, perform the following steps:

1. Open a command prompt and move into the directory <YOKO_HOME>/samples/ws/hello_world

2. Enter the following command to start the Web services server (on a single command line):

   UNIX (must use forward slashes):

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath $CLASSPATH:build/classes yoko.server.Server &

   Windows (may use either forward or back slashes):

     start java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath %CLASSPATH%;build\classes yoko.server.Server

  The server process starts in the background. We have to set the Xbootclasspath,
  because the ORB classes in Yoko conflict with the JDK ORB classes.

3. Enter the following command to start the CORBA client, substituting <Name> with your name:

   UNIX:

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar 
        -classpath $CLASSPATH:build/classes corba.client.Client <Name>

   Windows:

     java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar
        -classpath %CLASSPATH%;build\classes corba.client.Client <Name>

4. After running the client, use the kill command to terminate the server process (UNIX) or
   type Ctrl-C in the server's command window (Windows).


Cleanup
=======

To remove the code generated from the WSDL file and the .class
files, either delete the build directory and its contents or run:

  ant clean


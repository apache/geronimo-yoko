Bank Demo
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


Building the Demo
=================

To build the demo code, perform the following steps:

1. Open a command prompt and move into the directory <YOKO_HOME>/samples/ws/bank

2. From the samples/ws/bank directory, enter the following command (UNIX or Windows):

     ant

  This command uses the CXF wsdl2java utility to generate the server & client code.


Running the Demo - Use Case 1
=============================

To run the demo for the first use case, perform the following steps:

1. Open a command prompt and move into the directory <YOKO_HOME>/samples/ws/bank

2. Enter the following command to start the server (on a single command line):

   UNIX (must use forward slashes):

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar \
     -Dcxf.config.file=file:$YOKO_HOME/samples/ws/etc/corba_bus_config.xml \
     -classpath $CLASSPATH:build/classes yoko.server.Server &

   Windows (may use either forward or back slashes):

     start java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar 
        -Dcxf.config.file=file:/%YOKO_HOME%\samples\ws\etc\corba_bus_config.xml
        -classpath %CLASSPATH%;build\classes yoko.server.Server

  The server process starts in the background. We have to set the Xbootclasspath,
  because the ORB classes in Yoko conflict with the JDK ORB classes.

3. Enter the following command to start the client:

   UNIX:

     java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-SNAPSHOT.jar \
        -Dcxf.config.file=file:$YOKO_HOME/samples/ws/etc/corba_bus_config.xml \
        -classpath $CLASSPATH:build/classes yoko.client.Client

   Windows:

     java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-SNAPSHOT.jar
        -Dcxf.config.file=file:/%YOKO_HOME%\samples\ws\etc\corba_bus_config.xml
        -classpath %CLASSPATH%;build\classes yoko.client.Client

4. After running the client, use the kill command to terminate the server process (UNIX) or
   type Ctrl-C in the server's command window (Windows).


Cleanup
=======

To remove the code generated from the WSDL file and the .class
files, either delete the build directory and its contents or run:

  ant clean


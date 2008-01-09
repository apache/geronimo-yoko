Hello World Demo
================

Prerequisite
------------

If your environment already includes yoko-${current-yoko-version}.jar 
on the CLASSPATH, and the JDK and ant bin directories on the PATH 
it is not necessary to run the environment script described in the 
samples directory README.  If your environment is not properly 
configured, or if you are planning on using wsdl2idl/idl2wsdl, javac, 
and java to build and run the demos, you must include the 
yoko-${current-yoko-version}.jar in the CLASSPATH. Also 
set YOKO_HOME to the installation directory.
 

Building and running the demo using ant
=======================================

From the samples/orb/hello_world directory, the ant build script
can be used to build and run the demo.

Using either UNIX or Windows, run:

  ant build

This will generate the Java code from the HelloWorld.idl and build the 
client and server classes.

To remove the code generated from the IDL and the .class files, run:

  ant clean


Running the demo
-----------------

From the samples/orb/hello_world directory run the commands, entered on a
single command line:

We have to set the Xbootclasspath because the ORB classes in yoko conflicts 
with the jdk classes.

For UNIX (must use forward slashes):
    java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-M2-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-M2-SNAPSHOT.jar \
         corba.server.Server &

    java -Xbootclasspath/p:$YOKO_HOME/lib/yoko-spec-corba-1.0-incubating-M2-SNAPSHOT.jar:$YOKO_HOME/lib/yoko-core-1.0-incubating-M2-SNAPSHOT.jar \
         corba.client.Client

The server process starts in the background.  After running the client,
use the kill command to terminate the server process.

For Windows (may use either forward or back slashes):
  start java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-M2-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-M2-SNAPSHOT.jar
         corba.server.Server

  java -Xbootclasspath/p:%YOKO_HOME%\lib\yoko-spec-corba-1.0-incubating-M2-SNAPSHOT.jar;%YOKO_HOME%\lib\yoko-core-1.0-incubating-M2-SNAPSHOT.jar
         corba.client.Client

A new command windows opens for the server process.  After running the
client, terminate the server process by issuing Ctrl-C in its command window.

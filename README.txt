                          Instructions to build Yoko
			 ============================

Build Requirements
------------------

1. maven2.0.2 and above as your MAVEN_HOME
2. JDK1.5 as your JAVA_HOME
3. Subversion 1.2 and above

Steps to build Yoko
-------------------

1. If you aren't using maven first time then we recommend cleaning local maven repository. 
   on unix clean: ~/.m2/ directory
   on win clean: %USERPROFILE%/m2/ directory

2. Open a shell and add JAVA_HOME and MAVEN_HOME environment variables to your path.

3. Run the command 'mvn install'.

4. To do a clean build run 'mvn clean install'

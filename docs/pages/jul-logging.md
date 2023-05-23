# Enabling J.U.L Logging

A Logger object is used to log messages for a particular system or application component. In other words, it is a means of tracking events that happen when some software runs by capturing and persisting important data and making it available for analysis at any point in time. 

## Why is Logging Used?

To see what is happening within a system, we must use @Logging to allow tracing, which can be found in the @Logging annotation, in the 'jupiter' package. When a class or test method has the @Logging annotation, the specified java.util.logging is enabled for it.

Since other logging frameworks (Log4J, SLF4J) are j.u.l-compatible, these  should work for them, but have not been tested. 
 
Upon turning on logging and running tests, all logging is collected and printed out - logging up to where the test began and after the test is run. Testify captures any enabled logging into a buffer, and flushes the buffer to the console - before and after a test. 
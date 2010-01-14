package org.omg.SecurityLevel1;


/**
* org/omg/SecurityLevel1/CurrentOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public interface CurrentOperations  extends org.omg.CORBA.CurrentOperations
{

  // thread specific operations
  org.omg.Security.SecAttribute[] get_attributes (org.omg.Security.AttributeType[] ttributes);
} // interface CurrentOperations

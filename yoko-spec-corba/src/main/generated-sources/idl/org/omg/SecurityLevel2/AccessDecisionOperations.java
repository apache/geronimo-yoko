package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/AccessDecisionOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public interface AccessDecisionOperations 
{
  boolean access_allowed (org.omg.SecurityLevel2.Credentials[] cred_list, org.omg.CORBA.Object target, String operation_name, String target_interface_name);
} // interface AccessDecisionOperations

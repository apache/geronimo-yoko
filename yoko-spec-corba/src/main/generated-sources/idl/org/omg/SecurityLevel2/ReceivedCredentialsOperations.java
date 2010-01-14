package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/ReceivedCredentialsOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public interface ReceivedCredentialsOperations  extends org.omg.SecurityLevel2.CredentialsOperations
{
  org.omg.SecurityLevel2.Credentials accepting_credentials ();
  short association_options_used ();
  org.omg.Security.DelegationState delegation_state ();
  org.omg.Security.DelegationMode delegation_mode ();
} // interface ReceivedCredentialsOperations

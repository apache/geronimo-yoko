package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/PrincipalAuthenticatorOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public interface PrincipalAuthenticatorOperations 
{
  int[] get_supported_authen_methods (String mechanism);
  org.omg.Security.AuthenticationStatus authenticate (int method, String mechanism, String security_name, org.omg.CORBA.Any auth_data, org.omg.Security.SecAttribute[] privileges, org.omg.SecurityLevel2.CredentialsHolder creds, org.omg.CORBA.AnyHolder continuation_data, org.omg.CORBA.AnyHolder auth_specific_data);
  org.omg.Security.AuthenticationStatus continue_authentication (org.omg.CORBA.Any response_data, org.omg.SecurityLevel2.Credentials creds, org.omg.CORBA.AnyHolder continuation_data, org.omg.CORBA.AnyHolder auth_specific_data);
} // interface PrincipalAuthenticatorOperations

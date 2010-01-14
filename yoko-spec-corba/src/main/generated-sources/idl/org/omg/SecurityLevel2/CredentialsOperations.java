package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/CredentialsOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public interface CredentialsOperations 
{
  org.omg.SecurityLevel2.Credentials copy ();
  void destroy ();
  org.omg.Security.InvocationCredentialsType credentials_type ();
  org.omg.Security.AuthenticationStatus authentication_state ();
  String mechanism ();
  short accepting_options_supported ();
  void accepting_options_supported (short newAccepting_options_supported);
  short accepting_options_required ();
  void accepting_options_required (short newAccepting_options_required);
  short invocation_options_supported ();
  void invocation_options_supported (short newInvocation_options_supported);
  short invocation_options_required ();
  void invocation_options_required (short newInvocation_options_required);
  boolean get_security_feature (org.omg.Security.CommunicationDirection direction, org.omg.Security.SecurityFeature feature);
  boolean set_attributes (org.omg.Security.SecAttribute[] requested_attributes, org.omg.Security.AttributeListHolder actual_attributes);
  org.omg.Security.SecAttribute[] get_attributes (org.omg.Security.AttributeType[] attributes);
  boolean is_valid (org.omg.TimeBase.UtcTHolder expiry_time);
  boolean refresh (org.omg.CORBA.Any refresh_data);
} // interface CredentialsOperations

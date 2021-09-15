package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/PrincipalAuthenticatorPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public abstract class PrincipalAuthenticatorPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.PrincipalAuthenticatorOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("get_supported_authen_methods", 0);
    _methods.put ("authenticate", 1);
    _methods.put ("continue_authentication", 2);
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // SecurityLevel2/PrincipalAuthenticator/get_supported_authen_methods
       {
         String mechanism = org.omg.Security.MechanismTypeHelper.read (in);
         int $result[] = null;
         $result = this.get_supported_authen_methods (mechanism);
         out = $rh.createReply();
         org.omg.Security.AuthenticationMethodListHelper.write (out, $result);
         break;
       }

       case 1:  // SecurityLevel2/PrincipalAuthenticator/authenticate
       {
         int method = org.omg.Security.AuthenticationMethodHelper.read (in);
         String mechanism = org.omg.Security.MechanismTypeHelper.read (in);
         String security_name = org.omg.Security.SecurityNameHelper.read (in);
         org.omg.CORBA.Any auth_data = in.read_any ();
         org.omg.Security.SecAttribute privileges[] = org.omg.Security.AttributeListHelper.read (in);
         org.omg.SecurityLevel2.CredentialsHolder creds = new org.omg.SecurityLevel2.CredentialsHolder ();
         org.omg.CORBA.AnyHolder continuation_data = new org.omg.CORBA.AnyHolder ();
         org.omg.CORBA.AnyHolder auth_specific_data = new org.omg.CORBA.AnyHolder ();
         org.omg.Security.AuthenticationStatus $result = null;
         $result = this.authenticate (method, mechanism, security_name, auth_data, privileges, creds, continuation_data, auth_specific_data);
         out = $rh.createReply();
         org.omg.Security.AuthenticationStatusHelper.write (out, $result);
         org.omg.SecurityLevel2.CredentialsHelper.write (out, creds.value);
         out.write_any (continuation_data.value);
         out.write_any (auth_specific_data.value);
         break;
       }

       case 2:  // SecurityLevel2/PrincipalAuthenticator/continue_authentication
       {
         org.omg.CORBA.Any response_data = in.read_any ();
         org.omg.SecurityLevel2.Credentials creds = org.omg.SecurityLevel2.CredentialsHelper.read (in);
         org.omg.CORBA.AnyHolder continuation_data = new org.omg.CORBA.AnyHolder ();
         org.omg.CORBA.AnyHolder auth_specific_data = new org.omg.CORBA.AnyHolder ();
         org.omg.Security.AuthenticationStatus $result = null;
         $result = this.continue_authentication (response_data, creds, continuation_data, auth_specific_data);
         out = $rh.createReply();
         org.omg.Security.AuthenticationStatusHelper.write (out, $result);
         out.write_any (continuation_data.value);
         out.write_any (auth_specific_data.value);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/PrincipalAuthenticator:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public PrincipalAuthenticator _this() 
  {
    return PrincipalAuthenticatorHelper.narrow(
    super._this_object());
  }

  public PrincipalAuthenticator _this(org.omg.CORBA.ORB orb) 
  {
    return PrincipalAuthenticatorHelper.narrow(
    super._this_object(orb));
  }


} // class PrincipalAuthenticatorPOA

package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/_PrincipalAuthenticatorStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public class _PrincipalAuthenticatorStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel2.PrincipalAuthenticator
{

  public int[] get_supported_authen_methods (String mechanism)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_supported_authen_methods", true);
                org.omg.Security.MechanismTypeHelper.write ($out, mechanism);
                $in = _invoke ($out);
                int $result[] = org.omg.Security.AuthenticationMethodListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_supported_authen_methods (mechanism        );
            } finally {
                _releaseReply ($in);
            }
  } // get_supported_authen_methods

  public org.omg.Security.AuthenticationStatus authenticate (int method, String mechanism, String security_name, org.omg.CORBA.Any auth_data, org.omg.Security.SecAttribute[] privileges, org.omg.SecurityLevel2.CredentialsHolder creds, org.omg.CORBA.AnyHolder continuation_data, org.omg.CORBA.AnyHolder auth_specific_data)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("authenticate", true);
                org.omg.Security.AuthenticationMethodHelper.write ($out, method);
                org.omg.Security.MechanismTypeHelper.write ($out, mechanism);
                org.omg.Security.SecurityNameHelper.write ($out, security_name);
                $out.write_any (auth_data);
                org.omg.Security.AttributeListHelper.write ($out, privileges);
                $in = _invoke ($out);
                org.omg.Security.AuthenticationStatus $result = org.omg.Security.AuthenticationStatusHelper.read ($in);
                creds.value = org.omg.SecurityLevel2.CredentialsHelper.read ($in);
                continuation_data.value = $in.read_any ();
                auth_specific_data.value = $in.read_any ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return authenticate (method, mechanism, security_name, auth_data, privileges, creds, continuation_data, auth_specific_data        );
            } finally {
                _releaseReply ($in);
            }
  } // authenticate

  public org.omg.Security.AuthenticationStatus continue_authentication (org.omg.CORBA.Any response_data, org.omg.SecurityLevel2.Credentials creds, org.omg.CORBA.AnyHolder continuation_data, org.omg.CORBA.AnyHolder auth_specific_data)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("continue_authentication", true);
                $out.write_any (response_data);
                org.omg.SecurityLevel2.CredentialsHelper.write ($out, creds);
                $in = _invoke ($out);
                org.omg.Security.AuthenticationStatus $result = org.omg.Security.AuthenticationStatusHelper.read ($in);
                continuation_data.value = $in.read_any ();
                auth_specific_data.value = $in.read_any ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return continue_authentication (response_data, creds, continuation_data, auth_specific_data        );
            } finally {
                _releaseReply ($in);
            }
  } // continue_authentication

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/PrincipalAuthenticator:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _PrincipalAuthenticatorStub

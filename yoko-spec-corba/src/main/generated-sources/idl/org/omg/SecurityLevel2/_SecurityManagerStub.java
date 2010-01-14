package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/_SecurityManagerStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


/* */
public class _SecurityManagerStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel2.SecurityManager
{


  // Process/Capsule/ORB Instance specific operations
  public org.omg.Security.MechandOptions[] supported_mechanisms ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_supported_mechanisms", true);
                $in = _invoke ($out);
                org.omg.Security.MechandOptions $result[] = org.omg.Security.MechandOptionsListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return supported_mechanisms (        );
            } finally {
                _releaseReply ($in);
            }
  } // supported_mechanisms

  public org.omg.SecurityLevel2.Credentials[] own_credentials ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_own_credentials", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.Credentials $result[] = org.omg.SecurityLevel2.CredentialsListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return own_credentials (        );
            } finally {
                _releaseReply ($in);
            }
  } // own_credentials

  public org.omg.SecurityLevel2.RequiredRights required_rights_object ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_required_rights_object", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.RequiredRights $result = org.omg.SecurityLevel2.RequiredRightsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return required_rights_object (        );
            } finally {
                _releaseReply ($in);
            }
  } // required_rights_object

  public org.omg.SecurityLevel2.PrincipalAuthenticator principal_authenticator ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_principal_authenticator", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.PrincipalAuthenticator $result = org.omg.SecurityLevel2.PrincipalAuthenticatorHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return principal_authenticator (        );
            } finally {
                _releaseReply ($in);
            }
  } // principal_authenticator

  public org.omg.SecurityLevel2.AccessDecision access_decision ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_access_decision", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.AccessDecision $result = org.omg.SecurityLevel2.AccessDecisionHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return access_decision (        );
            } finally {
                _releaseReply ($in);
            }
  } // access_decision

  public org.omg.SecurityLevel2.AuditDecision audit_decision ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_audit_decision", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.AuditDecision $result = org.omg.SecurityLevel2.AuditDecisionHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return audit_decision (        );
            } finally {
                _releaseReply ($in);
            }
  } // audit_decision

  public org.omg.SecurityLevel2.TargetCredentials get_target_credentials (org.omg.CORBA.Object obj_ref)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_target_credentials", true);
                org.omg.CORBA.ObjectHelper.write ($out, obj_ref);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.TargetCredentials $result = org.omg.SecurityLevel2.TargetCredentialsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_target_credentials (obj_ref        );
            } finally {
                _releaseReply ($in);
            }
  } // get_target_credentials

  public void remove_own_credentials (org.omg.SecurityLevel2.Credentials creds)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("remove_own_credentials", true);
                org.omg.SecurityLevel2.CredentialsHelper.write ($out, creds);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                remove_own_credentials (creds        );
            } finally {
                _releaseReply ($in);
            }
  } // remove_own_credentials

  public org.omg.CORBA.Policy get_security_policy (int policy_type)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_security_policy", true);
                org.omg.CORBA.PolicyTypeHelper.write ($out, policy_type);
                $in = _invoke ($out);
                org.omg.CORBA.Policy $result = org.omg.CORBA.PolicyHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_security_policy (policy_type        );
            } finally {
                _releaseReply ($in);
            }
  } // get_security_policy

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/SecurityManager:1.0"};

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
} // class _SecurityManagerStub

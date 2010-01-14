package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/_TargetCredentialsStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public class _TargetCredentialsStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel2.TargetCredentials
{

  public org.omg.SecurityLevel2.Credentials initiating_credentials ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_initiating_credentials", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.Credentials $result = org.omg.SecurityLevel2.CredentialsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return initiating_credentials (        );
            } finally {
                _releaseReply ($in);
            }
  } // initiating_credentials

  public short association_options_used ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_association_options_used", true);
                $in = _invoke ($out);
                short $result = org.omg.Security.AssociationOptionsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return association_options_used (        );
            } finally {
                _releaseReply ($in);
            }
  } // association_options_used

  public org.omg.SecurityLevel2.Credentials copy ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("copy", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.Credentials $result = org.omg.SecurityLevel2.CredentialsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return copy (        );
            } finally {
                _releaseReply ($in);
            }
  } // copy

  public void destroy ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("destroy", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                destroy (        );
            } finally {
                _releaseReply ($in);
            }
  } // destroy

  public org.omg.Security.InvocationCredentialsType credentials_type ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_credentials_type", true);
                $in = _invoke ($out);
                org.omg.Security.InvocationCredentialsType $result = org.omg.Security.InvocationCredentialsTypeHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return credentials_type (        );
            } finally {
                _releaseReply ($in);
            }
  } // credentials_type

  public org.omg.Security.AuthenticationStatus authentication_state ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_authentication_state", true);
                $in = _invoke ($out);
                org.omg.Security.AuthenticationStatus $result = org.omg.Security.AuthenticationStatusHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return authentication_state (        );
            } finally {
                _releaseReply ($in);
            }
  } // authentication_state

  public String mechanism ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_mechanism", true);
                $in = _invoke ($out);
                String $result = org.omg.Security.MechanismTypeHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return mechanism (        );
            } finally {
                _releaseReply ($in);
            }
  } // mechanism

  public short accepting_options_supported ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_accepting_options_supported", true);
                $in = _invoke ($out);
                short $result = org.omg.Security.AssociationOptionsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return accepting_options_supported (        );
            } finally {
                _releaseReply ($in);
            }
  } // accepting_options_supported

  public void accepting_options_supported (short newAccepting_options_supported)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_accepting_options_supported", true);
                org.omg.Security.AssociationOptionsHelper.write ($out, newAccepting_options_supported);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                accepting_options_supported (newAccepting_options_supported        );
            } finally {
                _releaseReply ($in);
            }
  } // accepting_options_supported

  public short accepting_options_required ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_accepting_options_required", true);
                $in = _invoke ($out);
                short $result = org.omg.Security.AssociationOptionsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return accepting_options_required (        );
            } finally {
                _releaseReply ($in);
            }
  } // accepting_options_required

  public void accepting_options_required (short newAccepting_options_required)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_accepting_options_required", true);
                org.omg.Security.AssociationOptionsHelper.write ($out, newAccepting_options_required);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                accepting_options_required (newAccepting_options_required        );
            } finally {
                _releaseReply ($in);
            }
  } // accepting_options_required

  public short invocation_options_supported ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_invocation_options_supported", true);
                $in = _invoke ($out);
                short $result = org.omg.Security.AssociationOptionsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return invocation_options_supported (        );
            } finally {
                _releaseReply ($in);
            }
  } // invocation_options_supported

  public void invocation_options_supported (short newInvocation_options_supported)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_invocation_options_supported", true);
                org.omg.Security.AssociationOptionsHelper.write ($out, newInvocation_options_supported);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                invocation_options_supported (newInvocation_options_supported        );
            } finally {
                _releaseReply ($in);
            }
  } // invocation_options_supported

  public short invocation_options_required ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_invocation_options_required", true);
                $in = _invoke ($out);
                short $result = org.omg.Security.AssociationOptionsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return invocation_options_required (        );
            } finally {
                _releaseReply ($in);
            }
  } // invocation_options_required

  public void invocation_options_required (short newInvocation_options_required)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_invocation_options_required", true);
                org.omg.Security.AssociationOptionsHelper.write ($out, newInvocation_options_required);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                invocation_options_required (newInvocation_options_required        );
            } finally {
                _releaseReply ($in);
            }
  } // invocation_options_required

  public boolean get_security_feature (org.omg.Security.CommunicationDirection direction, org.omg.Security.SecurityFeature feature)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_security_feature", true);
                org.omg.Security.CommunicationDirectionHelper.write ($out, direction);
                org.omg.Security.SecurityFeatureHelper.write ($out, feature);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_security_feature (direction, feature        );
            } finally {
                _releaseReply ($in);
            }
  } // get_security_feature

  public boolean set_attributes (org.omg.Security.SecAttribute[] requested_attributes, org.omg.Security.AttributeListHolder actual_attributes)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("set_attributes", true);
                org.omg.Security.AttributeListHelper.write ($out, requested_attributes);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                actual_attributes.value = org.omg.Security.AttributeListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return set_attributes (requested_attributes, actual_attributes        );
            } finally {
                _releaseReply ($in);
            }
  } // set_attributes

  public org.omg.Security.SecAttribute[] get_attributes (org.omg.Security.AttributeType[] attributes)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_attributes", true);
                org.omg.Security.AttributeTypeListHelper.write ($out, attributes);
                $in = _invoke ($out);
                org.omg.Security.SecAttribute $result[] = org.omg.Security.AttributeListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_attributes (attributes        );
            } finally {
                _releaseReply ($in);
            }
  } // get_attributes

  public boolean is_valid (org.omg.TimeBase.UtcTHolder expiry_time)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("is_valid", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                expiry_time.value = org.omg.Security.UtcTHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return is_valid (expiry_time        );
            } finally {
                _releaseReply ($in);
            }
  } // is_valid

  public boolean refresh (org.omg.CORBA.Any refresh_data)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("refresh", true);
                $out.write_any (refresh_data);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return refresh (refresh_data        );
            } finally {
                _releaseReply ($in);
            }
  } // refresh

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/TargetCredentials:1.0", 
    "IDL:SecurityLevel2/Credentials:1.0"};

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
} // class _TargetCredentialsStub

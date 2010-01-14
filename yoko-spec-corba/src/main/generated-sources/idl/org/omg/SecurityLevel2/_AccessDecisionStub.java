package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/_AccessDecisionStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public class _AccessDecisionStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel2.AccessDecision
{

  public boolean access_allowed (org.omg.SecurityLevel2.Credentials[] cred_list, org.omg.CORBA.Object target, String operation_name, String target_interface_name)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("access_allowed", true);
                org.omg.SecurityLevel2.CredentialsListHelper.write ($out, cred_list);
                org.omg.CORBA.ObjectHelper.write ($out, target);
                org.omg.CORBA.IdentifierHelper.write ($out, operation_name);
                org.omg.CORBA.IdentifierHelper.write ($out, target_interface_name);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return access_allowed (cred_list, target, operation_name, target_interface_name        );
            } finally {
                _releaseReply ($in);
            }
  } // access_allowed

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/AccessDecision:1.0"};

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
} // class _AccessDecisionStub

package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/_RequiredRightsStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// RequiredRights Interface
public class _RequiredRightsStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel2.RequiredRights
{

  public void get_required_rights (org.omg.CORBA.Object obj, String operation_name, String interface_name, org.omg.Security.RightsListHolder rights, org.omg.Security.RightsCombinatorHolder rights_combinator)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_required_rights", true);
                org.omg.CORBA.ObjectHelper.write ($out, obj);
                org.omg.CORBA.IdentifierHelper.write ($out, operation_name);
                org.omg.CORBA.RepositoryIdHelper.write ($out, interface_name);
                $in = _invoke ($out);
                rights.value = org.omg.Security.RightsListHelper.read ($in);
                rights_combinator.value = org.omg.Security.RightsCombinatorHelper.read ($in);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                get_required_rights (obj, operation_name, interface_name, rights, rights_combinator        );
            } finally {
                _releaseReply ($in);
            }
  } // get_required_rights

  public void set_required_rights (String operation_name, String interface_name, org.omg.Security.Right[] rights, org.omg.Security.RightsCombinator rights_combinator)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("set_required_rights", true);
                org.omg.CORBA.IdentifierHelper.write ($out, operation_name);
                org.omg.CORBA.RepositoryIdHelper.write ($out, interface_name);
                org.omg.Security.RightsListHelper.write ($out, rights);
                org.omg.Security.RightsCombinatorHelper.write ($out, rights_combinator);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                set_required_rights (operation_name, interface_name, rights, rights_combinator        );
            } finally {
                _releaseReply ($in);
            }
  } // set_required_rights

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/RequiredRights:1.0"};

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
} // class _RequiredRightsStub

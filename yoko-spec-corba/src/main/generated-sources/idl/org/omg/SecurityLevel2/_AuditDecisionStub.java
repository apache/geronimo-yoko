package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/_AuditDecisionStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public class _AuditDecisionStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel2.AuditDecision
{

  public boolean audit_needed (org.omg.Security.AuditEventType event_type, org.omg.Security.SelectorValue[] value_list)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("audit_needed", true);
                org.omg.Security.AuditEventTypeHelper.write ($out, event_type);
                org.omg.Security.SelectorValueListHelper.write ($out, value_list);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return audit_needed (event_type, value_list        );
            } finally {
                _releaseReply ($in);
            }
  } // audit_needed

  public org.omg.SecurityLevel2.AuditChannel audit_channel ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_audit_channel", true);
                $in = _invoke ($out);
                org.omg.SecurityLevel2.AuditChannel $result = org.omg.SecurityLevel2.AuditChannelHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return audit_channel (        );
            } finally {
                _releaseReply ($in);
            }
  } // audit_channel

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/AuditDecision:1.0"};

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
} // class _AuditDecisionStub

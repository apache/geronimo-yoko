package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/_AuditChannelStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public class _AuditChannelStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel2.AuditChannel
{

  public void audit_write (org.omg.Security.AuditEventType event_type, org.omg.SecurityLevel2.Credentials[] creds, org.omg.TimeBase.UtcT time, org.omg.Security.SelectorValue[] descriptors, org.omg.CORBA.Any event_specific_data)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("audit_write", true);
                org.omg.Security.AuditEventTypeHelper.write ($out, event_type);
                org.omg.SecurityLevel2.CredentialsListHelper.write ($out, creds);
                org.omg.Security.UtcTHelper.write ($out, time);
                org.omg.Security.SelectorValueListHelper.write ($out, descriptors);
                $out.write_any (event_specific_data);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                audit_write (event_type, creds, time, descriptors, event_specific_data        );
            } finally {
                _releaseReply ($in);
            }
  } // audit_write

  public int audit_channel_id ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_audit_channel_id", true);
                $in = _invoke ($out);
                int $result = org.omg.Security.AuditChannelIdHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return audit_channel_id (        );
            } finally {
                _releaseReply ($in);
            }
  } // audit_channel_id

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/AuditChannel:1.0"};

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
} // class _AuditChannelStub

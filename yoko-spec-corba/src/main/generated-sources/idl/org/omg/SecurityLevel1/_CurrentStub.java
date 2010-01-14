package org.omg.SecurityLevel1;


/**
* org/omg/SecurityLevel1/_CurrentStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public class _CurrentStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.SecurityLevel1.Current
{


  // thread specific operations
  public org.omg.Security.SecAttribute[] get_attributes (org.omg.Security.AttributeType[] ttributes)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_attributes", true);
                org.omg.Security.AttributeTypeListHelper.write ($out, ttributes);
                $in = _invoke ($out);
                org.omg.Security.SecAttribute $result[] = org.omg.Security.AttributeListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_attributes (ttributes        );
            } finally {
                _releaseReply ($in);
            }
  } // get_attributes

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:omg.org/SecurityLevel1/Current:1.0", 
    "IDL:CORBA/Current:1.0"};

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
} // class _CurrentStub

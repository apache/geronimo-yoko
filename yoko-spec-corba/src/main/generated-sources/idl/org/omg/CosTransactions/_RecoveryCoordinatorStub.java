package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/_RecoveryCoordinatorStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public class _RecoveryCoordinatorStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.CosTransactions.RecoveryCoordinator
{

  public org.omg.CosTransactions.Status replay_completion (org.omg.CosTransactions.Resource r) throws org.omg.CosTransactions.NotPrepared
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("replay_completion", true);
                org.omg.CosTransactions.ResourceHelper.write ($out, r);
                $in = _invoke ($out);
                org.omg.CosTransactions.Status $result = org.omg.CosTransactions.StatusHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/NotPrepared:1.0"))
                    throw org.omg.CosTransactions.NotPreparedHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return replay_completion (r        );
            } finally {
                _releaseReply ($in);
            }
  } // replay_completion

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CosTransactions/RecoveryCoordinator:1.0"};

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
} // class _RecoveryCoordinatorStub

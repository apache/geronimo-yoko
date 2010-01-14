package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/_CurrentStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


// Current transaction
public class _CurrentStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.CosTransactions.Current
{

  public void begin () throws org.omg.CosTransactions.SubtransactionsUnavailable
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("begin", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/SubtransactionsUnavailable:1.0"))
                    throw org.omg.CosTransactions.SubtransactionsUnavailableHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                begin (        );
            } finally {
                _releaseReply ($in);
            }
  } // begin

  public void commit (boolean report_heuristics) throws org.omg.CosTransactions.NoTransaction, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("commit", true);
                $out.write_boolean (report_heuristics);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/NoTransaction:1.0"))
                    throw org.omg.CosTransactions.NoTransactionHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicMixed:1.0"))
                    throw org.omg.CosTransactions.HeuristicMixedHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicHazard:1.0"))
                    throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                commit (report_heuristics        );
            } finally {
                _releaseReply ($in);
            }
  } // commit

  public void rollback () throws org.omg.CosTransactions.NoTransaction
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("rollback", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/NoTransaction:1.0"))
                    throw org.omg.CosTransactions.NoTransactionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                rollback (        );
            } finally {
                _releaseReply ($in);
            }
  } // rollback

  public void rollback_only () throws org.omg.CosTransactions.NoTransaction
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("rollback_only", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/NoTransaction:1.0"))
                    throw org.omg.CosTransactions.NoTransactionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                rollback_only (        );
            } finally {
                _releaseReply ($in);
            }
  } // rollback_only

  public org.omg.CosTransactions.Status get_status ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_status", true);
                $in = _invoke ($out);
                org.omg.CosTransactions.Status $result = org.omg.CosTransactions.StatusHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_status (        );
            } finally {
                _releaseReply ($in);
            }
  } // get_status

  public String get_transaction_name ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_transaction_name", true);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_transaction_name (        );
            } finally {
                _releaseReply ($in);
            }
  } // get_transaction_name

  public void set_timeout (int seconds)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("set_timeout", true);
                $out.write_ulong (seconds);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                set_timeout (seconds        );
            } finally {
                _releaseReply ($in);
            }
  } // set_timeout

  public int get_timeout ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_timeout", true);
                $in = _invoke ($out);
                int $result = $in.read_ulong ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_timeout (        );
            } finally {
                _releaseReply ($in);
            }
  } // get_timeout

  public org.omg.CosTransactions.Control get_control ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("get_control", true);
                $in = _invoke ($out);
                org.omg.CosTransactions.Control $result = org.omg.CosTransactions.ControlHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return get_control (        );
            } finally {
                _releaseReply ($in);
            }
  } // get_control

  public org.omg.CosTransactions.Control suspend ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("suspend", true);
                $in = _invoke ($out);
                org.omg.CosTransactions.Control $result = org.omg.CosTransactions.ControlHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return suspend (        );
            } finally {
                _releaseReply ($in);
            }
  } // suspend

  public void resume (org.omg.CosTransactions.Control which) throws org.omg.CosTransactions.InvalidControl
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("resume", true);
                org.omg.CosTransactions.ControlHelper.write ($out, which);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/InvalidControl:1.0"))
                    throw org.omg.CosTransactions.InvalidControlHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                resume (which        );
            } finally {
                _releaseReply ($in);
            }
  } // resume

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CosTransactions/Current:1.0", 
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

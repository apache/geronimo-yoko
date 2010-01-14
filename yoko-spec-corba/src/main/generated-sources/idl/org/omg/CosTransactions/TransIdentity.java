package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/TransIdentity.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TransIdentity implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.CosTransactions.Coordinator coord = null;
  public org.omg.CosTransactions.Terminator term = null;
  public org.omg.CosTransactions.otid_t otid = null;

  public TransIdentity ()
  {
  } // ctor

  public TransIdentity (org.omg.CosTransactions.Coordinator _coord, org.omg.CosTransactions.Terminator _term, org.omg.CosTransactions.otid_t _otid)
  {
    coord = _coord;
    term = _term;
    otid = _otid;
  } // ctor

} // class TransIdentity

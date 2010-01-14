package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/otid_t.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class otid_t implements org.omg.CORBA.portable.IDLEntity
{
  public int formatID = (int)0;

  /*format identifier. 0 is OSI TP */
  public int bqual_length = (int)0;
  public byte tid[] = null;

  public otid_t ()
  {
  } // ctor

  public otid_t (int _formatID, int _bqual_length, byte[] _tid)
  {
    formatID = _formatID;
    bqual_length = _bqual_length;
    tid = _tid;
  } // ctor

} // class otid_t

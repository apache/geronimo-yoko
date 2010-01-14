package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/VoteHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class VoteHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Vote value = null;

  public VoteHolder ()
  {
  }

  public VoteHolder (org.omg.CosTransactions.Vote initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.VoteHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.VoteHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.VoteHelper.type ();
  }

}

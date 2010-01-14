package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/SubtransactionsUnavailableHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class SubtransactionsUnavailableHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.SubtransactionsUnavailable value = null;

  public SubtransactionsUnavailableHolder ()
  {
  }

  public SubtransactionsUnavailableHolder (org.omg.CosTransactions.SubtransactionsUnavailable initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.SubtransactionsUnavailableHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.SubtransactionsUnavailableHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.SubtransactionsUnavailableHelper.type ();
  }

}

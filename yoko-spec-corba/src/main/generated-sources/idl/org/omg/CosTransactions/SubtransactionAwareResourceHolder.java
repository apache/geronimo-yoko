package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/SubtransactionAwareResourceHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class SubtransactionAwareResourceHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.SubtransactionAwareResource value = null;

  public SubtransactionAwareResourceHolder ()
  {
  }

  public SubtransactionAwareResourceHolder (org.omg.CosTransactions.SubtransactionAwareResource initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.SubtransactionAwareResourceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.SubtransactionAwareResourceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.SubtransactionAwareResourceHelper.type ();
  }

}

package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/NoTransactionHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class NoTransactionHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.NoTransaction value = null;

  public NoTransactionHolder ()
  {
  }

  public NoTransactionHolder (org.omg.CosTransactions.NoTransaction initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.NoTransactionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.NoTransactionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.NoTransactionHelper.type ();
  }

}

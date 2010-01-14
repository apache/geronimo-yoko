package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/NotSubtransactionHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class NotSubtransactionHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.NotSubtransaction value = null;

  public NotSubtransactionHolder ()
  {
  }

  public NotSubtransactionHolder (org.omg.CosTransactions.NotSubtransaction initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.NotSubtransactionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.NotSubtransactionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.NotSubtransactionHelper.type ();
  }

}

package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/TransactionFactoryHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TransactionFactoryHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.TransactionFactory value = null;

  public TransactionFactoryHolder ()
  {
  }

  public TransactionFactoryHolder (org.omg.CosTransactions.TransactionFactory initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.TransactionFactoryHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.TransactionFactoryHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.TransactionFactoryHelper.type ();
  }

}

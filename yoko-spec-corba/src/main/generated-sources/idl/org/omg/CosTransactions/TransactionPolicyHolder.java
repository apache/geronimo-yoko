package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/TransactionPolicyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TransactionPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.TransactionPolicy value = null;

  public TransactionPolicyHolder ()
  {
  }

  public TransactionPolicyHolder (org.omg.CosTransactions.TransactionPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.TransactionPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.TransactionPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.TransactionPolicyHelper.type ();
  }

}

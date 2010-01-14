package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/NonTxTargetPolicyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class NonTxTargetPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.NonTxTargetPolicy value = null;

  public NonTxTargetPolicyHolder ()
  {
  }

  public NonTxTargetPolicyHolder (org.omg.CosTransactions.NonTxTargetPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.NonTxTargetPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.NonTxTargetPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.NonTxTargetPolicyHelper.type ();
  }

}

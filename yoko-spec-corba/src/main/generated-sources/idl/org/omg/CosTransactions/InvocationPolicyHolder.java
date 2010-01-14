package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/InvocationPolicyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class InvocationPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.InvocationPolicy value = null;

  public InvocationPolicyHolder ()
  {
  }

  public InvocationPolicyHolder (org.omg.CosTransactions.InvocationPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.InvocationPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.InvocationPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.InvocationPolicyHelper.type ();
  }

}

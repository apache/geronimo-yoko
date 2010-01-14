package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/TransIdentityHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TransIdentityHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.TransIdentity value = null;

  public TransIdentityHolder ()
  {
  }

  public TransIdentityHolder (org.omg.CosTransactions.TransIdentity initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.TransIdentityHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.TransIdentityHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.TransIdentityHelper.type ();
  }

}

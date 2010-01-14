package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/UnavailableHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class UnavailableHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Unavailable value = null;

  public UnavailableHolder ()
  {
  }

  public UnavailableHolder (org.omg.CosTransactions.Unavailable initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.UnavailableHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.UnavailableHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.UnavailableHelper.type ();
  }

}

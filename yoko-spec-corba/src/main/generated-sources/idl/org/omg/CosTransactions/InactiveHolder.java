package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/InactiveHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class InactiveHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Inactive value = null;

  public InactiveHolder ()
  {
  }

  public InactiveHolder (org.omg.CosTransactions.Inactive initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.InactiveHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.InactiveHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.InactiveHelper.type ();
  }

}

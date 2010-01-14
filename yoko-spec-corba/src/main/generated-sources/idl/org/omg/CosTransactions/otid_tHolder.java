package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/otid_tHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class otid_tHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.otid_t value = null;

  public otid_tHolder ()
  {
  }

  public otid_tHolder (org.omg.CosTransactions.otid_t initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.otid_tHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.otid_tHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.otid_tHelper.type ();
  }

}

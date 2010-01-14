package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/CurrentHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


// Current transaction
public final class CurrentHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Current value = null;

  public CurrentHolder ()
  {
  }

  public CurrentHolder (org.omg.CosTransactions.Current initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.CurrentHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.CurrentHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.CurrentHelper.type ();
  }

}

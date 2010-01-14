package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/ControlHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class ControlHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Control value = null;

  public ControlHolder ()
  {
  }

  public ControlHolder (org.omg.CosTransactions.Control initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.ControlHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.ControlHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.ControlHelper.type ();
  }

}

package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/StatusHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


// DATATYPES
public final class StatusHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Status value = null;

  public StatusHolder ()
  {
  }

  public StatusHolder (org.omg.CosTransactions.Status initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.StatusHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.StatusHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.StatusHelper.type ();
  }

}

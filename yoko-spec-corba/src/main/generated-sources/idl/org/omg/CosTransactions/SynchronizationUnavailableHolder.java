package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/SynchronizationUnavailableHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class SynchronizationUnavailableHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.SynchronizationUnavailable value = null;

  public SynchronizationUnavailableHolder ()
  {
  }

  public SynchronizationUnavailableHolder (org.omg.CosTransactions.SynchronizationUnavailable initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.SynchronizationUnavailableHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.SynchronizationUnavailableHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.SynchronizationUnavailableHelper.type ();
  }

}

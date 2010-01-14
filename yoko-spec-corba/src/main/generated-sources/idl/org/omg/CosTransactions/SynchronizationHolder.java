package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/SynchronizationHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


// Inheritance from TransactionalObject is for backward compatability //
public final class SynchronizationHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Synchronization value = null;

  public SynchronizationHolder ()
  {
  }

  public SynchronizationHolder (org.omg.CosTransactions.Synchronization initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.SynchronizationHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.SynchronizationHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.SynchronizationHelper.type ();
  }

}

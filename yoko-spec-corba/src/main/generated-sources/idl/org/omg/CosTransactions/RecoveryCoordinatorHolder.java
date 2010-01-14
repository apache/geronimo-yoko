package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/RecoveryCoordinatorHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class RecoveryCoordinatorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.RecoveryCoordinator value = null;

  public RecoveryCoordinatorHolder ()
  {
  }

  public RecoveryCoordinatorHolder (org.omg.CosTransactions.RecoveryCoordinator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.RecoveryCoordinatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.RecoveryCoordinatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.RecoveryCoordinatorHelper.type ();
  }

}

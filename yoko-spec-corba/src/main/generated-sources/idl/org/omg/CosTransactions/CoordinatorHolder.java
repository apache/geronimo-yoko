package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/CoordinatorHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class CoordinatorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Coordinator value = null;

  public CoordinatorHolder ()
  {
  }

  public CoordinatorHolder (org.omg.CosTransactions.Coordinator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.CoordinatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.CoordinatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.CoordinatorHelper.type ();
  }

}

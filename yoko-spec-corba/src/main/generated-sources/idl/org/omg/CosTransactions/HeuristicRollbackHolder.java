package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/HeuristicRollbackHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class HeuristicRollbackHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.HeuristicRollback value = null;

  public HeuristicRollbackHolder ()
  {
  }

  public HeuristicRollbackHolder (org.omg.CosTransactions.HeuristicRollback initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.HeuristicRollbackHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.HeuristicRollbackHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.HeuristicRollbackHelper.type ();
  }

}

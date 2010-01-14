package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/HeuristicMixedHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class HeuristicMixedHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.HeuristicMixed value = null;

  public HeuristicMixedHolder ()
  {
  }

  public HeuristicMixedHolder (org.omg.CosTransactions.HeuristicMixed initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.HeuristicMixedHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.HeuristicMixedHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.HeuristicMixedHelper.type ();
  }

}

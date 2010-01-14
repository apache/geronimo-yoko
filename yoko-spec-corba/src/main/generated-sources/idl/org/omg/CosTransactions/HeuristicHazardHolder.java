package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/HeuristicHazardHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class HeuristicHazardHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.HeuristicHazard value = null;

  public HeuristicHazardHolder ()
  {
  }

  public HeuristicHazardHolder (org.omg.CosTransactions.HeuristicHazard initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.HeuristicHazardHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.HeuristicHazardHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.HeuristicHazardHelper.type ();
  }

}

package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/NotPreparedHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class NotPreparedHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.NotPrepared value = null;

  public NotPreparedHolder ()
  {
  }

  public NotPreparedHolder (org.omg.CosTransactions.NotPrepared initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.NotPreparedHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.NotPreparedHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.NotPreparedHelper.type ();
  }

}

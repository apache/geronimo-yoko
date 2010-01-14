package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/TerminatorHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TerminatorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Terminator value = null;

  public TerminatorHolder ()
  {
  }

  public TerminatorHolder (org.omg.CosTransactions.Terminator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.TerminatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.TerminatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.TerminatorHelper.type ();
  }

}

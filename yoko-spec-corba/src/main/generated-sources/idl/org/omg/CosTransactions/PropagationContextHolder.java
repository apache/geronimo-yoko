package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/PropagationContextHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class PropagationContextHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.PropagationContext value = null;

  public PropagationContextHolder ()
  {
  }

  public PropagationContextHolder (org.omg.CosTransactions.PropagationContext initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.PropagationContextHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.PropagationContextHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.PropagationContextHelper.type ();
  }

}

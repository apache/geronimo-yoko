package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/ResourceHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class ResourceHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Resource value = null;

  public ResourceHolder ()
  {
  }

  public ResourceHolder (org.omg.CosTransactions.Resource initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.ResourceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.ResourceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.ResourceHelper.type ();
  }

}

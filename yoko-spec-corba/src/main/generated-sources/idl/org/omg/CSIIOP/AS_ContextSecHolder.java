package org.omg.CSIIOP;

/**
* org/omg/CSIIOP/AS_ContextSecHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class AS_ContextSecHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.AS_ContextSec value = null;

  public AS_ContextSecHolder ()
  {
  }

  public AS_ContextSecHolder (org.omg.CSIIOP.AS_ContextSec initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.AS_ContextSecHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.AS_ContextSecHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.AS_ContextSecHelper.type ();
  }

}

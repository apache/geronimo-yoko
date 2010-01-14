package org.omg.Security;

/**
* org/omg/Security/SelectorValueHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SelectorValueHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.SelectorValue value = null;

  public SelectorValueHolder ()
  {
  }

  public SelectorValueHolder (org.omg.Security.SelectorValue initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.SelectorValueHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.SelectorValueHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.SelectorValueHelper.type ();
  }

}

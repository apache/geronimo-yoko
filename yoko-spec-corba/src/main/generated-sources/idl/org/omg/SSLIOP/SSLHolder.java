package org.omg.SSLIOP;

/**
* org/omg/SSLIOP/SSLHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SSLHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SSLIOP.SSL value = null;

  public SSLHolder ()
  {
  }

  public SSLHolder (org.omg.SSLIOP.SSL initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SSLIOP.SSLHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SSLIOP.SSLHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SSLIOP.SSLHelper.type ();
  }

}

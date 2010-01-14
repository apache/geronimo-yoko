package org.omg.GSSUP;

/**
* org/omg/GSSUP/ErrorTokenHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class ErrorTokenHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.GSSUP.ErrorToken value = null;

  public ErrorTokenHolder ()
  {
  }

  public ErrorTokenHolder (org.omg.GSSUP.ErrorToken initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.GSSUP.ErrorTokenHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.GSSUP.ErrorTokenHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.GSSUP.ErrorTokenHelper.type ();
  }

}

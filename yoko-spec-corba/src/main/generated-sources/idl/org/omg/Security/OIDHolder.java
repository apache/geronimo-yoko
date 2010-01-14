package org.omg.Security;


/**
* org/omg/Security/OIDHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class OIDHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public OIDHolder ()
  {
  }

  public OIDHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.OIDHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.OIDHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.OIDHelper.type ();
  }

}

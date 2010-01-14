package org.omg.CSI;

/**
* org/omg/CSI/IdentityTokenHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class IdentityTokenHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.IdentityToken value = null;

  public IdentityTokenHolder ()
  {
  }

  public IdentityTokenHolder (org.omg.CSI.IdentityToken initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.IdentityTokenHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.IdentityTokenHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.IdentityTokenHelper.type ();
  }

}

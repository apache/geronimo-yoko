package org.omg.CSI;


/**
* org/omg/CSI/AuthorizationTokenHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// AuthorizationElements
public final class AuthorizationTokenHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.AuthorizationElement value[] = null;

  public AuthorizationTokenHolder ()
  {
  }

  public AuthorizationTokenHolder (org.omg.CSI.AuthorizationElement[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.AuthorizationTokenHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.AuthorizationTokenHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.AuthorizationTokenHelper.type ();
  }

}

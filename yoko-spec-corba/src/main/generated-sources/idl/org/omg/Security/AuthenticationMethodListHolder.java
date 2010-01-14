package org.omg.Security;


/**
* org/omg/Security/AuthenticationMethodListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AuthenticationMethodListHolder implements org.omg.CORBA.portable.Streamable
{
  public int value[] = null;

  public AuthenticationMethodListHolder ()
  {
  }

  public AuthenticationMethodListHolder (int[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AuthenticationMethodListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AuthenticationMethodListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AuthenticationMethodListHelper.type ();
  }

}

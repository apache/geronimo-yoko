package org.omg.Security;

/**
* org/omg/Security/AuthenticationStatusHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Authentication return status
public final class AuthenticationStatusHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.AuthenticationStatus value = null;

  public AuthenticationStatusHolder ()
  {
  }

  public AuthenticationStatusHolder (org.omg.Security.AuthenticationStatus initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AuthenticationStatusHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AuthenticationStatusHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AuthenticationStatusHelper.type ();
  }

}

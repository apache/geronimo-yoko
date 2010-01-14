package org.omg.Security;

/**
* org/omg/Security/InvocationCredentialsTypeHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Credential types
public final class InvocationCredentialsTypeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.InvocationCredentialsType value = null;

  public InvocationCredentialsTypeHolder ()
  {
  }

  public InvocationCredentialsTypeHolder (org.omg.Security.InvocationCredentialsType initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.InvocationCredentialsTypeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.InvocationCredentialsTypeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.InvocationCredentialsTypeHelper.type ();
  }

}

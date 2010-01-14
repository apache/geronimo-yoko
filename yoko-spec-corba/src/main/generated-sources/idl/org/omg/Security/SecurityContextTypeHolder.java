package org.omg.Security;

/**
* org/omg/Security/SecurityContextTypeHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Type of SecurityContext
public final class SecurityContextTypeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.SecurityContextType value = null;

  public SecurityContextTypeHolder ()
  {
  }

  public SecurityContextTypeHolder (org.omg.Security.SecurityContextType initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.SecurityContextTypeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.SecurityContextTypeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.SecurityContextTypeHelper.type ();
  }

}

package org.omg.Security;

/**
* org/omg/Security/SecAttributeHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SecAttributeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.SecAttribute value = null;

  public SecAttributeHolder ()
  {
  }

  public SecAttributeHolder (org.omg.Security.SecAttribute initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.SecAttributeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.SecAttributeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.SecAttributeHelper.type ();
  }

}

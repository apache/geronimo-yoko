package org.omg.Security;

/**
* org/omg/Security/AttributeTypeHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AttributeTypeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.AttributeType value = null;

  public AttributeTypeHolder ()
  {
  }

  public AttributeTypeHolder (org.omg.Security.AttributeType initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AttributeTypeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AttributeTypeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AttributeTypeHelper.type ();
  }

}

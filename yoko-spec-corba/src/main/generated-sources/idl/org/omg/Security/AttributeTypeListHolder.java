package org.omg.Security;


/**
* org/omg/Security/AttributeTypeListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AttributeTypeListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.AttributeType value[] = null;

  public AttributeTypeListHolder ()
  {
  }

  public AttributeTypeListHolder (org.omg.Security.AttributeType[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AttributeTypeListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AttributeTypeListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AttributeTypeListHelper.type ();
  }

}

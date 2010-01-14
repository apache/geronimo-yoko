package org.omg.Security;


/**
* org/omg/Security/AttributeListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AttributeListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.SecAttribute value[] = null;

  public AttributeListHolder ()
  {
  }

  public AttributeListHolder (org.omg.Security.SecAttribute[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AttributeListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AttributeListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AttributeListHelper.type ();
  }

}

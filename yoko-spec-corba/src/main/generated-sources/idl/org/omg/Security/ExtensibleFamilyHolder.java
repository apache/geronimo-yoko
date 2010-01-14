package org.omg.Security;

/**
* org/omg/Security/ExtensibleFamilyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class ExtensibleFamilyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.ExtensibleFamily value = null;

  public ExtensibleFamilyHolder ()
  {
  }

  public ExtensibleFamilyHolder (org.omg.Security.ExtensibleFamily initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.ExtensibleFamilyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.ExtensibleFamilyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.ExtensibleFamilyHelper.type ();
  }

}

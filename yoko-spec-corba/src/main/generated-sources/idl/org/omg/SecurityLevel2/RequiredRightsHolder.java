package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/RequiredRightsHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// RequiredRights Interface
public final class RequiredRightsHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.RequiredRights value = null;

  public RequiredRightsHolder ()
  {
  }

  public RequiredRightsHolder (org.omg.SecurityLevel2.RequiredRights initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.RequiredRightsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.RequiredRightsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.RequiredRightsHelper.type ();
  }

}

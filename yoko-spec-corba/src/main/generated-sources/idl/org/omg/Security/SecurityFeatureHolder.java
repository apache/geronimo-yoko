package org.omg.Security;

/**
* org/omg/Security/SecurityFeatureHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Security features available on credentials.
public final class SecurityFeatureHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.SecurityFeature value = null;

  public SecurityFeatureHolder ()
  {
  }

  public SecurityFeatureHolder (org.omg.Security.SecurityFeature initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.SecurityFeatureHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.SecurityFeatureHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.SecurityFeatureHelper.type ();
  }

}

package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/TargetCredentialsHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public final class TargetCredentialsHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.TargetCredentials value = null;

  public TargetCredentialsHolder ()
  {
  }

  public TargetCredentialsHolder (org.omg.SecurityLevel2.TargetCredentials initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.TargetCredentialsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.TargetCredentialsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.TargetCredentialsHelper.type ();
  }

}

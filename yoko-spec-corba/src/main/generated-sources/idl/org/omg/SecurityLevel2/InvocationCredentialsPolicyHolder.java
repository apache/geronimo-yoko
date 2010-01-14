package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/InvocationCredentialsPolicyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


/* */
public final class InvocationCredentialsPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.InvocationCredentialsPolicy value = null;

  public InvocationCredentialsPolicyHolder ()
  {
  }

  public InvocationCredentialsPolicyHolder (org.omg.SecurityLevel2.InvocationCredentialsPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.InvocationCredentialsPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.InvocationCredentialsPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.InvocationCredentialsPolicyHelper.type ();
  }

}

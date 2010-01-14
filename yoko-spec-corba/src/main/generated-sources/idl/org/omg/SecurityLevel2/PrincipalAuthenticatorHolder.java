package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/PrincipalAuthenticatorHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public final class PrincipalAuthenticatorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.PrincipalAuthenticator value = null;

  public PrincipalAuthenticatorHolder ()
  {
  }

  public PrincipalAuthenticatorHolder (org.omg.SecurityLevel2.PrincipalAuthenticator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.PrincipalAuthenticatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.PrincipalAuthenticatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.PrincipalAuthenticatorHelper.type ();
  }

}

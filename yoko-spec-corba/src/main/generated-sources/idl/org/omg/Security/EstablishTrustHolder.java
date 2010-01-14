package org.omg.Security;

/**
* org/omg/Security/EstablishTrustHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class EstablishTrustHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.EstablishTrust value = null;

  public EstablishTrustHolder ()
  {
  }

  public EstablishTrustHolder (org.omg.Security.EstablishTrust initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.EstablishTrustHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.EstablishTrustHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.EstablishTrustHelper.type ();
  }

}

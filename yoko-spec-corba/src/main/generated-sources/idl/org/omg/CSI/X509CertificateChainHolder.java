package org.omg.CSI;


/**
* org/omg/CSI/X509CertificateChainHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// representation of Certificate is as defined in [IETF RFC 2459].
public final class X509CertificateChainHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public X509CertificateChainHolder ()
  {
  }

  public X509CertificateChainHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.X509CertificateChainHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.X509CertificateChainHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.X509CertificateChainHelper.type ();
  }

}

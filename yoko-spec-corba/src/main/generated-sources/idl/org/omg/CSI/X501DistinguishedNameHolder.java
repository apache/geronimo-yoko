package org.omg.CSI;


/**
* org/omg/CSI/X501DistinguishedNameHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// octets containing the ASN.1 encoding.
public final class X501DistinguishedNameHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public X501DistinguishedNameHolder ()
  {
  }

  public X501DistinguishedNameHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.X501DistinguishedNameHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.X501DistinguishedNameHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.X501DistinguishedNameHelper.type ();
  }

}

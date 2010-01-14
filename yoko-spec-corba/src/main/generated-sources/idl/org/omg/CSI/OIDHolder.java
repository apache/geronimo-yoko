package org.omg.CSI;


/**
* org/omg/CSI/OIDHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// ASN.1 Encoding of an OBJECT IDENTIFIER
public final class OIDHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public OIDHolder ()
  {
  }

  public OIDHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.OIDHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.OIDHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.OIDHelper.type ();
  }

}

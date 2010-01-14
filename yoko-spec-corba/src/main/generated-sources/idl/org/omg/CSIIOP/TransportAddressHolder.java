package org.omg.CSIIOP;

/**
* org/omg/CSIIOP/TransportAddressHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TransportAddressHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.TransportAddress value = null;

  public TransportAddressHolder ()
  {
  }

  public TransportAddressHolder (org.omg.CSIIOP.TransportAddress initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.TransportAddressHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.TransportAddressHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.TransportAddressHelper.type ();
  }

}

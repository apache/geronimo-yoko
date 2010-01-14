package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/TransportAddressListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TransportAddressListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.TransportAddress value[] = null;

  public TransportAddressListHolder ()
  {
  }

  public TransportAddressListHolder (org.omg.CSIIOP.TransportAddress[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.TransportAddressListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.TransportAddressListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.TransportAddressListHelper.type ();
  }

}

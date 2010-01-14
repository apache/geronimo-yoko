package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/ServiceSpecificNameHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class ServiceSpecificNameHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public ServiceSpecificNameHolder ()
  {
  }

  public ServiceSpecificNameHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.ServiceSpecificNameHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.ServiceSpecificNameHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.ServiceSpecificNameHelper.type ();
  }

}

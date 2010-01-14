package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/ServiceConfigurationListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class ServiceConfigurationListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.ServiceConfiguration value[] = null;

  public ServiceConfigurationListHolder ()
  {
  }

  public ServiceConfigurationListHolder (org.omg.CSIIOP.ServiceConfiguration[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.ServiceConfigurationListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.ServiceConfigurationListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.ServiceConfigurationListHelper.type ();
  }

}

package org.omg.Security;

/**
* org/omg/Security/ChannelBindingsHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class ChannelBindingsHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.ChannelBindings value = null;

  public ChannelBindingsHolder ()
  {
  }

  public ChannelBindingsHolder (org.omg.Security.ChannelBindings initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.ChannelBindingsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.ChannelBindingsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.ChannelBindingsHelper.type ();
  }

}

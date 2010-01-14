package org.omg.Security;

/**
* org/omg/Security/OpaqueBufferHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class OpaqueBufferHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.OpaqueBuffer value = null;

  public OpaqueBufferHolder ()
  {
  }

  public OpaqueBufferHolder (org.omg.Security.OpaqueBuffer initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.OpaqueBufferHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.OpaqueBufferHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.OpaqueBufferHelper.type ();
  }

}

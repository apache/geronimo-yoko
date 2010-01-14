package org.omg.CSI;

/**
* org/omg/CSI/ContextErrorHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class ContextErrorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.ContextError value = null;

  public ContextErrorHolder ()
  {
  }

  public ContextErrorHolder (org.omg.CSI.ContextError initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.ContextErrorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.ContextErrorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.ContextErrorHelper.type ();
  }

}

package org.omg.CSI;

/**
* org/omg/CSI/MessageInContextHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class MessageInContextHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.MessageInContext value = null;

  public MessageInContextHolder ()
  {
  }

  public MessageInContextHolder (org.omg.CSI.MessageInContext initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.MessageInContextHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.MessageInContextHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.MessageInContextHelper.type ();
  }

}

package org.omg.CSI;


/**
* org/omg/CSI/UTF8StringHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// UTF-8 Encoding of String
public final class UTF8StringHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public UTF8StringHolder ()
  {
  }

  public UTF8StringHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.UTF8StringHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.UTF8StringHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.UTF8StringHelper.type ();
  }

}

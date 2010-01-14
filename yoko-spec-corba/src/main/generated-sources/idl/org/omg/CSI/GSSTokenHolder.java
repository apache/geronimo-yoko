package org.omg.CSI;


/**
* org/omg/CSI/GSSTokenHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// tokens) is mechanism dependent.
public final class GSSTokenHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public GSSTokenHolder ()
  {
  }

  public GSSTokenHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.GSSTokenHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.GSSTokenHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.GSSTokenHelper.type ();
  }

}

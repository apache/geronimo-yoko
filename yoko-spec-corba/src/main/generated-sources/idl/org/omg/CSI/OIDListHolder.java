package org.omg.CSI;


/**
* org/omg/CSI/OIDListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class OIDListHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[][] = null;

  public OIDListHolder ()
  {
  }

  public OIDListHolder (byte[][] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.OIDListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.OIDListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.OIDListHelper.type ();
  }

}

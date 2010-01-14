package org.omg.CSI;


/**
* org/omg/CSI/IdentityExtensionHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class IdentityExtensionHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public IdentityExtensionHolder ()
  {
  }

  public IdentityExtensionHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.IdentityExtensionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.IdentityExtensionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.IdentityExtensionHelper.type ();
  }

}

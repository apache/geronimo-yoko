package org.omg.CSI;


/**
* org/omg/CSI/GSS_NT_ExportedNameListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class GSS_NT_ExportedNameListHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[][] = null;

  public GSS_NT_ExportedNameListHolder ()
  {
  }

  public GSS_NT_ExportedNameListHolder (byte[][] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.GSS_NT_ExportedNameListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.GSS_NT_ExportedNameListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.GSS_NT_ExportedNameListHelper.type ();
  }

}

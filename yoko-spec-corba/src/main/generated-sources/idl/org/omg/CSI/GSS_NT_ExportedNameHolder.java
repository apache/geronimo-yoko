package org.omg.CSI;


/**
* org/omg/CSI/GSS_NT_ExportedNameHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Exported Name Object Format," p. 84.
public final class GSS_NT_ExportedNameHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public GSS_NT_ExportedNameHolder ()
  {
  }

  public GSS_NT_ExportedNameHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.GSS_NT_ExportedNameHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.GSS_NT_ExportedNameHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.GSS_NT_ExportedNameHelper.type ();
  }

}

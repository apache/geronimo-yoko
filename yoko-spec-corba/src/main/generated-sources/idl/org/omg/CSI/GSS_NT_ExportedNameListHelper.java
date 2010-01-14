package org.omg.CSI;


/**
* org/omg/CSI/GSS_NT_ExportedNameListHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class GSS_NT_ExportedNameListHelper
{
  private static String  _id = "IDL:omg.org/CSI/GSS_NT_ExportedNameList:1.0";

  public static void insert (org.omg.CORBA.Any a, byte[][] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static byte[][] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.GSS_NT_ExportedNameHelper.id (), "GSS_NT_ExportedName", __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.GSS_NT_ExportedNameListHelper.id (), "GSS_NT_ExportedNameList", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static byte[][] read (org.omg.CORBA.portable.InputStream istream)
  {
    byte value[][] = null;
    int _len0 = istream.read_long ();
    value = new byte[_len0][];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = org.omg.CSI.GSS_NT_ExportedNameHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, byte[][] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      org.omg.CSI.GSS_NT_ExportedNameHelper.write (ostream, value[_i0]);
  }

}

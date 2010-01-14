package org.omg.CSI;


/**
* org/omg/CSI/IdentityExtensionHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class IdentityExtensionHelper
{
  private static String  _id = "IDL:omg.org/CSI/IdentityExtension:1.0";

  public static void insert (org.omg.CORBA.Any a, byte[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static byte[] extract (org.omg.CORBA.Any a)
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
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.IdentityExtensionHelper.id (), "IdentityExtension", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static byte[] read (org.omg.CORBA.portable.InputStream istream)
  {
    byte value[] = null;
    int _len0 = istream.read_long ();
    value = new byte[_len0];
    istream.read_octet_array (value, 0, _len0);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, byte[] value)
  {
    ostream.write_long (value.length);
    ostream.write_octet_array (value, 0, value.length);
  }

}

package org.omg.CSI;


/**
* org/omg/CSI/ContextIdHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// identifier value of 0.
abstract public class ContextIdHelper
{
  private static String  _id = "IDL:omg.org/CSI/ContextId:1.0";

  public static void insert (org.omg.CORBA.Any a, long that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static long extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulonglong);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.ContextIdHelper.id (), "ContextId", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static long read (org.omg.CORBA.portable.InputStream istream)
  {
    long value = (long)0;
    value = istream.read_ulonglong ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, long value)
  {
    ostream.write_ulonglong (value);
  }

}

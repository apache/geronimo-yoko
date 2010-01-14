package org.omg.GSSUP;


/**
* org/omg/GSSUP/ErrorTokenHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class ErrorTokenHelper
{
  private static String  _id = "IDL:omg.org/GSSUP/ErrorToken:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.GSSUP.ErrorToken that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.GSSUP.ErrorToken extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [1];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.GSSUP.ErrorCodeHelper.id (), "ErrorCode", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "error_code",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.GSSUP.ErrorTokenHelper.id (), "ErrorToken", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.GSSUP.ErrorToken read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.GSSUP.ErrorToken value = new org.omg.GSSUP.ErrorToken ();
    value.error_code = istream.read_ulong ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.GSSUP.ErrorToken value)
  {
    ostream.write_ulong (value.error_code);
  }

}

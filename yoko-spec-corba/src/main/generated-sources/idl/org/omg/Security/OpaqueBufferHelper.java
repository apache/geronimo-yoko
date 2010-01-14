package org.omg.Security;


/**
* org/omg/Security/OpaqueBufferHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class OpaqueBufferHelper
{
  private static String  _id = "IDL:omg.org/Security/OpaqueBuffer:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.OpaqueBuffer that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.OpaqueBuffer extract (org.omg.CORBA.Any a)
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [3];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.OpaqueHelper.id (), "Opaque", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "buffer",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
          _members0[1] = new org.omg.CORBA.StructMember (
            "startpos",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
          _members0[2] = new org.omg.CORBA.StructMember (
            "endpos",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.Security.OpaqueBufferHelper.id (), "OpaqueBuffer", _members0);
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

  public static org.omg.Security.OpaqueBuffer read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.Security.OpaqueBuffer value = new org.omg.Security.OpaqueBuffer ();
    value.buffer = org.omg.Security.OpaqueHelper.read (istream);
    value.startpos = istream.read_ulong ();
    value.endpos = istream.read_ulong ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.OpaqueBuffer value)
  {
    org.omg.Security.OpaqueHelper.write (ostream, value.buffer);
    ostream.write_ulong (value.startpos);
    ostream.write_ulong (value.endpos);
  }

}

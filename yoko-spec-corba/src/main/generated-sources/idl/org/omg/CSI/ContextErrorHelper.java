package org.omg.CSI;


/**
* org/omg/CSI/ContextErrorHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class ContextErrorHelper
{
  private static String  _id = "IDL:omg.org/CSI/ContextError:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSI.ContextError that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSI.ContextError extract (org.omg.CORBA.Any a)
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [4];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulonglong);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.ContextIdHelper.id (), "ContextId", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "client_context_id",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
          _members0[1] = new org.omg.CORBA.StructMember (
            "major_status",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
          _members0[2] = new org.omg.CORBA.StructMember (
            "minor_status",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.GSSTokenHelper.id (), "GSSToken", _tcOf_members0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "error_token",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CSI.ContextErrorHelper.id (), "ContextError", _members0);
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

  public static org.omg.CSI.ContextError read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSI.ContextError value = new org.omg.CSI.ContextError ();
    value.client_context_id = istream.read_ulonglong ();
    value.major_status = istream.read_long ();
    value.minor_status = istream.read_long ();
    value.error_token = org.omg.CSI.GSSTokenHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSI.ContextError value)
  {
    ostream.write_ulonglong (value.client_context_id);
    ostream.write_long (value.major_status);
    ostream.write_long (value.minor_status);
    org.omg.CSI.GSSTokenHelper.write (ostream, value.error_token);
  }

}

package org.omg.Security;


/**
* org/omg/Security/ChannelBindingsHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class ChannelBindingsHelper
{
  private static String  _id = "IDL:omg.org/Security/ChannelBindings:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.ChannelBindings that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.ChannelBindings extract (org.omg.CORBA.Any a)
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [5];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
          _members0[0] = new org.omg.CORBA.StructMember (
            "initiator_addrtype",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "initiator_address",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
          _members0[2] = new org.omg.CORBA.StructMember (
            "acceptor_addrtype",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "acceptor_address",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _members0[4] = new org.omg.CORBA.StructMember (
            "application_data",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.Security.ChannelBindingsHelper.id (), "ChannelBindings", _members0);
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

  public static org.omg.Security.ChannelBindings read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.Security.ChannelBindings value = new org.omg.Security.ChannelBindings ();
    value.initiator_addrtype = istream.read_ulong ();
    int _len0 = istream.read_long ();
    value.initiator_address = new byte[_len0];
    istream.read_octet_array (value.initiator_address, 0, _len0);
    value.acceptor_addrtype = istream.read_ulong ();
    int _len1 = istream.read_long ();
    value.acceptor_address = new byte[_len1];
    istream.read_octet_array (value.acceptor_address, 0, _len1);
    int _len2 = istream.read_long ();
    value.application_data = new byte[_len2];
    istream.read_octet_array (value.application_data, 0, _len2);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.ChannelBindings value)
  {
    ostream.write_ulong (value.initiator_addrtype);
    ostream.write_long (value.initiator_address.length);
    ostream.write_octet_array (value.initiator_address, 0, value.initiator_address.length);
    ostream.write_ulong (value.acceptor_addrtype);
    ostream.write_long (value.acceptor_address.length);
    ostream.write_octet_array (value.acceptor_address, 0, value.acceptor_address.length);
    ostream.write_long (value.application_data.length);
    ostream.write_octet_array (value.application_data, 0, value.application_data.length);
  }

}

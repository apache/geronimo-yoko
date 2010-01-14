package org.omg.GSSUP;


/**
* org/omg/GSSUP/InitialContextTokenHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class InitialContextTokenHelper
{
  private static String  _id = "IDL:omg.org/GSSUP/InitialContextToken:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.GSSUP.InitialContextToken that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.GSSUP.InitialContextToken extract (org.omg.CORBA.Any a)
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
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.UTF8StringHelper.id (), "UTF8String", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "username",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.UTF8StringHelper.id (), "UTF8String", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "password",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.GSS_NT_ExportedNameHelper.id (), "GSS_NT_ExportedName", _tcOf_members0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "target_name",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.GSSUP.InitialContextTokenHelper.id (), "InitialContextToken", _members0);
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

  public static org.omg.GSSUP.InitialContextToken read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.GSSUP.InitialContextToken value = new org.omg.GSSUP.InitialContextToken ();
    value.username = org.omg.CSI.UTF8StringHelper.read (istream);
    value.password = org.omg.CSI.UTF8StringHelper.read (istream);
    value.target_name = org.omg.CSI.GSS_NT_ExportedNameHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.GSSUP.InitialContextToken value)
  {
    org.omg.CSI.UTF8StringHelper.write (ostream, value.username);
    org.omg.CSI.UTF8StringHelper.write (ostream, value.password);
    org.omg.CSI.GSS_NT_ExportedNameHelper.write (ostream, value.target_name);
  }

}

package org.omg.SSLIOP;


/**
* org/omg/SSLIOP/SSLHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class SSLHelper
{
  private static String  _id = "IDL:omg.org/SSLIOP/SSL:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.SSLIOP.SSL that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.SSLIOP.SSL extract (org.omg.CORBA.Any a)
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
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.AssociationOptionsHelper.id (), "AssociationOptions", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "target_supports",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.AssociationOptionsHelper.id (), "AssociationOptions", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "target_requires",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _members0[2] = new org.omg.CORBA.StructMember (
            "port",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.SSLIOP.SSLHelper.id (), "SSL", _members0);
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

  public static org.omg.SSLIOP.SSL read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.SSLIOP.SSL value = new org.omg.SSLIOP.SSL ();
    value.target_supports = istream.read_ushort ();
    value.target_requires = istream.read_ushort ();
    value.port = istream.read_ushort ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.SSLIOP.SSL value)
  {
    ostream.write_ushort (value.target_supports);
    ostream.write_ushort (value.target_requires);
    ostream.write_ushort (value.port);
  }

}

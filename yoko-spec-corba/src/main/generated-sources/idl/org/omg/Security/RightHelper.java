package org.omg.Security;


/**
* org/omg/Security/RightHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class RightHelper
{
  private static String  _id = "IDL:omg.org/Security/Right:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.Right that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.Right extract (org.omg.CORBA.Any a)
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [2];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.Security.ExtensibleFamilyHelper.type ();
          _members0[0] = new org.omg.CORBA.StructMember (
            "rights_family",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "the_right",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.Security.RightHelper.id (), "Right", _members0);
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

  public static org.omg.Security.Right read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.Security.Right value = new org.omg.Security.Right ();
    value.rights_family = org.omg.Security.ExtensibleFamilyHelper.read (istream);
    value.the_right = istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.Right value)
  {
    org.omg.Security.ExtensibleFamilyHelper.write (ostream, value.rights_family);
    ostream.write_string (value.the_right);
  }

}

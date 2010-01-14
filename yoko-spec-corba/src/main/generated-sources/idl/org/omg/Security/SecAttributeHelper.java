package org.omg.Security;


/**
* org/omg/Security/SecAttributeHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class SecAttributeHelper
{
  private static String  _id = "IDL:omg.org/Security/SecAttribute:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.SecAttribute that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.SecAttribute extract (org.omg.CORBA.Any a)
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
          _tcOf_members0 = org.omg.Security.AttributeTypeHelper.type ();
          _members0[0] = new org.omg.CORBA.StructMember (
            "attribute_type",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.OIDHelper.id (), "OID", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "defining_authority",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.OpaqueHelper.id (), "Opaque", _tcOf_members0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "value",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.Security.SecAttributeHelper.id (), "SecAttribute", _members0);
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

  public static org.omg.Security.SecAttribute read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.Security.SecAttribute value = new org.omg.Security.SecAttribute ();
    value.attribute_type = org.omg.Security.AttributeTypeHelper.read (istream);
    value.defining_authority = org.omg.Security.OIDHelper.read (istream);
    value.value = org.omg.Security.OpaqueHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.SecAttribute value)
  {
    org.omg.Security.AttributeTypeHelper.write (ostream, value.attribute_type);
    org.omg.Security.OIDHelper.write (ostream, value.defining_authority);
    org.omg.Security.OpaqueHelper.write (ostream, value.value);
  }

}

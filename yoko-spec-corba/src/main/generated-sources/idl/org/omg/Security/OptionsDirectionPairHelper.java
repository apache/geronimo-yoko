package org.omg.Security;


/**
* org/omg/Security/OptionsDirectionPairHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class OptionsDirectionPairHelper
{
  private static String  _id = "IDL:omg.org/Security/OptionsDirectionPair:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.OptionsDirectionPair that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.OptionsDirectionPair extract (org.omg.CORBA.Any a)
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
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.AssociationOptionsHelper.id (), "AssociationOptions", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "options",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.Security.CommunicationDirectionHelper.type ();
          _members0[1] = new org.omg.CORBA.StructMember (
            "direction",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.Security.OptionsDirectionPairHelper.id (), "OptionsDirectionPair", _members0);
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

  public static org.omg.Security.OptionsDirectionPair read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.Security.OptionsDirectionPair value = new org.omg.Security.OptionsDirectionPair ();
    value.options = istream.read_ushort ();
    value.direction = org.omg.Security.CommunicationDirectionHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.OptionsDirectionPair value)
  {
    ostream.write_ushort (value.options);
    org.omg.Security.CommunicationDirectionHelper.write (ostream, value.direction);
  }

}

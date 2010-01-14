package org.omg.Security;


/**
* org/omg/Security/QOPHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// for an object reference and used to protect messages
abstract public class QOPHelper
{
  private static String  _id = "IDL:omg.org/Security/QOP:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.QOP that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.QOP extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_enum_tc (org.omg.Security.QOPHelper.id (), "QOP", new String[] { "SecQOPNoProtection", "SecQOPIntegrity", "SecQOPConfidentiality", "SecQOPIntegrityAndConfidentiality"} );
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.Security.QOP read (org.omg.CORBA.portable.InputStream istream)
  {
    return org.omg.Security.QOP.from_int (istream.read_long ());
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.QOP value)
  {
    ostream.write_long (value.value ());
  }

}

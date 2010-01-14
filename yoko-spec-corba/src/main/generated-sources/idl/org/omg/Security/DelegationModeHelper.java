package org.omg.Security;


/**
* org/omg/Security/DelegationModeHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Delegation mode which can be administered
abstract public class DelegationModeHelper
{
  private static String  _id = "IDL:omg.org/Security/DelegationMode:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.DelegationMode that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.DelegationMode extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_enum_tc (org.omg.Security.DelegationModeHelper.id (), "DelegationMode", new String[] { "SecDelModeNoDelegation", "SecDelModeSimpleDelegation", "SecDelModeCompositeDelegation"} );
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.Security.DelegationMode read (org.omg.CORBA.portable.InputStream istream)
  {
    return org.omg.Security.DelegationMode.from_int (istream.read_long ());
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.DelegationMode value)
  {
    ostream.write_long (value.value ());
  }

}

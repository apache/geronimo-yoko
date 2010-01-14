package org.omg.Security;


/**
* org/omg/Security/UtcTHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// pick up from TimeBase
abstract public class UtcTHelper
{
  private static String  _id = "IDL:omg.org/Security/UtcT:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.TimeBase.UtcT that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.TimeBase.UtcT extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.TimeBase.UtcTHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.UtcTHelper.id (), "UtcT", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.TimeBase.UtcT read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.TimeBase.UtcT value = null;
    value = org.omg.TimeBase.UtcTHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.TimeBase.UtcT value)
  {
    org.omg.TimeBase.UtcTHelper.write (ostream, value);
  }

}

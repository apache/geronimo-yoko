package org.omg.Security;


/**
* org/omg/Security/IntervalTHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class IntervalTHelper
{
  private static String  _id = "IDL:omg.org/Security/IntervalT:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.TimeBase.IntervalT that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.TimeBase.IntervalT extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.TimeBase.IntervalTHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.Security.IntervalTHelper.id (), "IntervalT", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.TimeBase.IntervalT read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.TimeBase.IntervalT value = null;
    value = org.omg.TimeBase.IntervalTHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.TimeBase.IntervalT value)
  {
    org.omg.TimeBase.IntervalTHelper.write (ostream, value);
  }

}

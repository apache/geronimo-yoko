package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/TransportAddressListHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

abstract public class TransportAddressListHelper
{
  private static String  _id = "IDL:omg.org/CSIIOP/TransportAddressList:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSIIOP.TransportAddress[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSIIOP.TransportAddress[] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CSIIOP.TransportAddressHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSIIOP.TransportAddressListHelper.id (), "TransportAddressList", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.CSIIOP.TransportAddress[] read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSIIOP.TransportAddress value[] = null;
    int _len0 = istream.read_long ();
    value = new org.omg.CSIIOP.TransportAddress[_len0];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = org.omg.CSIIOP.TransportAddressHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSIIOP.TransportAddress[] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      org.omg.CSIIOP.TransportAddressHelper.write (ostream, value[_i0]);
  }

}

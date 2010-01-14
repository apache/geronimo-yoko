package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/CompoundSecMechListHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

abstract public class CompoundSecMechListHelper
{
  private static String  _id = "IDL:omg.org/CSIIOP/CompoundSecMechList:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSIIOP.CompoundSecMechList that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSIIOP.CompoundSecMechList extract (org.omg.CORBA.Any a)
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
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_boolean);
          _members0[0] = new org.omg.CORBA.StructMember (
            "stateful",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CSIIOP.CompoundSecMechHelper.type ();
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSIIOP.CompoundSecMechanismsHelper.id (), "CompoundSecMechanisms", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "mechanism_list",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CSIIOP.CompoundSecMechListHelper.id (), "CompoundSecMechList", _members0);
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

  public static org.omg.CSIIOP.CompoundSecMechList read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSIIOP.CompoundSecMechList value = new org.omg.CSIIOP.CompoundSecMechList ();
    value.stateful = istream.read_boolean ();
    value.mechanism_list = org.omg.CSIIOP.CompoundSecMechanismsHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSIIOP.CompoundSecMechList value)
  {
    ostream.write_boolean (value.stateful);
    org.omg.CSIIOP.CompoundSecMechanismsHelper.write (ostream, value.mechanism_list);
  }

}

package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/CompoundSecMechHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

abstract public class CompoundSecMechHelper
{
  private static String  _id = "IDL:omg.org/CSIIOP/CompoundSecMech:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSIIOP.CompoundSecMech that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSIIOP.CompoundSecMech extract (org.omg.CORBA.Any a)
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [4];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSIIOP.AssociationOptionsHelper.id (), "AssociationOptions", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "target_requires",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.IOP.TaggedComponentHelper.type ();
          _members0[1] = new org.omg.CORBA.StructMember (
            "transport_mech",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CSIIOP.AS_ContextSecHelper.type ();
          _members0[2] = new org.omg.CORBA.StructMember (
            "as_context_mech",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CSIIOP.SAS_ContextSecHelper.type ();
          _members0[3] = new org.omg.CORBA.StructMember (
            "sas_context_mech",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CSIIOP.CompoundSecMechHelper.id (), "CompoundSecMech", _members0);
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

  public static org.omg.CSIIOP.CompoundSecMech read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSIIOP.CompoundSecMech value = new org.omg.CSIIOP.CompoundSecMech ();
    value.target_requires = istream.read_ushort ();
    value.transport_mech = org.omg.IOP.TaggedComponentHelper.read (istream);
    value.as_context_mech = org.omg.CSIIOP.AS_ContextSecHelper.read (istream);
    value.sas_context_mech = org.omg.CSIIOP.SAS_ContextSecHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSIIOP.CompoundSecMech value)
  {
    ostream.write_ushort (value.target_requires);
    org.omg.IOP.TaggedComponentHelper.write (ostream, value.transport_mech);
    org.omg.CSIIOP.AS_ContextSecHelper.write (ostream, value.as_context_mech);
    org.omg.CSIIOP.SAS_ContextSecHelper.write (ostream, value.sas_context_mech);
  }

}

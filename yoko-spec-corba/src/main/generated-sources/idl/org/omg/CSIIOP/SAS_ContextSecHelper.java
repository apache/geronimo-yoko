package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/SAS_ContextSecHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

abstract public class SAS_ContextSecHelper
{
  private static String  _id = "IDL:omg.org/CSIIOP/SAS_ContextSec:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSIIOP.SAS_ContextSec that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSIIOP.SAS_ContextSec extract (org.omg.CORBA.Any a)
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [5];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSIIOP.AssociationOptionsHelper.id (), "AssociationOptions", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "target_supports",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSIIOP.AssociationOptionsHelper.id (), "AssociationOptions", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "target_requires",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CSIIOP.ServiceConfigurationHelper.type ();
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSIIOP.ServiceConfigurationListHelper.id (), "ServiceConfigurationList", _tcOf_members0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "privilege_authorities",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.OIDHelper.id (), "OID", _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.OIDListHelper.id (), "OIDList", _tcOf_members0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "supported_naming_mechanisms",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.IdentityTokenTypeHelper.id (), "IdentityTokenType", _tcOf_members0);
          _members0[4] = new org.omg.CORBA.StructMember (
            "supported_identity_types",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CSIIOP.SAS_ContextSecHelper.id (), "SAS_ContextSec", _members0);
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

  public static org.omg.CSIIOP.SAS_ContextSec read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSIIOP.SAS_ContextSec value = new org.omg.CSIIOP.SAS_ContextSec ();
    value.target_supports = istream.read_ushort ();
    value.target_requires = istream.read_ushort ();
    value.privilege_authorities = org.omg.CSIIOP.ServiceConfigurationListHelper.read (istream);
    value.supported_naming_mechanisms = org.omg.CSI.OIDListHelper.read (istream);
    value.supported_identity_types = istream.read_ulong ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSIIOP.SAS_ContextSec value)
  {
    ostream.write_ushort (value.target_supports);
    ostream.write_ushort (value.target_requires);
    org.omg.CSIIOP.ServiceConfigurationListHelper.write (ostream, value.privilege_authorities);
    org.omg.CSI.OIDListHelper.write (ostream, value.supported_naming_mechanisms);
    ostream.write_ulong (value.supported_identity_types);
  }

}

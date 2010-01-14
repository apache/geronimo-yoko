package org.omg.CSI;


/**
* org/omg/CSI/IdentityTokenHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class IdentityTokenHelper
{
  private static String  _id = "IDL:omg.org/CSI/IdentityToken:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSI.IdentityToken that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSI.IdentityToken extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      org.omg.CORBA.TypeCode _disTypeCode0;
      _disTypeCode0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
      _disTypeCode0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.IdentityTokenTypeHelper.id (), "IdentityTokenType", _disTypeCode0);
      org.omg.CORBA.UnionMember[] _members0 = new org.omg.CORBA.UnionMember [6];
      org.omg.CORBA.TypeCode _tcOf_members0;
      org.omg.CORBA.Any _anyOf_members0;

      // Branch for absent (case label org.omg.CSI.ITTAbsent.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_ulong ((int)org.omg.CSI.ITTAbsent.value);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_boolean);
      _members0[0] = new org.omg.CORBA.UnionMember (
        "absent",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for anonymous (case label org.omg.CSI.ITTAnonymous.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_ulong ((int)org.omg.CSI.ITTAnonymous.value);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_boolean);
      _members0[1] = new org.omg.CORBA.UnionMember (
        "anonymous",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for principal_name (case label org.omg.CSI.ITTPrincipalName.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_ulong ((int)org.omg.CSI.ITTPrincipalName.value);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.GSS_NT_ExportedNameHelper.id (), "GSS_NT_ExportedName", _tcOf_members0);
      _members0[2] = new org.omg.CORBA.UnionMember (
        "principal_name",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for certificate_chain (case label org.omg.CSI.ITTX509CertChain.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_ulong ((int)org.omg.CSI.ITTX509CertChain.value);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.X509CertificateChainHelper.id (), "X509CertificateChain", _tcOf_members0);
      _members0[3] = new org.omg.CORBA.UnionMember (
        "certificate_chain",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for dn (case label org.omg.CSI.ITTDistinguishedName.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_ulong ((int)org.omg.CSI.ITTDistinguishedName.value);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.X501DistinguishedNameHelper.id (), "X501DistinguishedName", _tcOf_members0);
      _members0[4] = new org.omg.CORBA.UnionMember (
        "dn",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for id (Default case)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_octet ((byte)0); // default member label
      _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.IdentityExtensionHelper.id (), "IdentityExtension", _tcOf_members0);
      _members0[5] = new org.omg.CORBA.UnionMember (
        "id",
        _anyOf_members0,
        _tcOf_members0,
        null);
      __typeCode = org.omg.CORBA.ORB.init ().create_union_tc (org.omg.CSI.IdentityTokenHelper.id (), "IdentityToken", _disTypeCode0, _members0);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.CSI.IdentityToken read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSI.IdentityToken value = new org.omg.CSI.IdentityToken ();
    int _dis0 = (int)0;
    _dis0 = istream.read_ulong ();
    switch (_dis0)
    {
      case org.omg.CSI.ITTAbsent.value:
        boolean _absent = false;
        _absent = istream.read_boolean ();
        value.absent (_absent);
        break;
      case org.omg.CSI.ITTAnonymous.value:
        boolean _anonymous = false;
        _anonymous = istream.read_boolean ();
        value.anonymous (_anonymous);
        break;
      case org.omg.CSI.ITTPrincipalName.value:
        byte _principal_name[] = null;
        _principal_name = org.omg.CSI.GSS_NT_ExportedNameHelper.read (istream);
        value.principal_name (_principal_name);
        break;
      case org.omg.CSI.ITTX509CertChain.value:
        byte _certificate_chain[] = null;
        _certificate_chain = org.omg.CSI.X509CertificateChainHelper.read (istream);
        value.certificate_chain (_certificate_chain);
        break;
      case org.omg.CSI.ITTDistinguishedName.value:
        byte _dn[] = null;
        _dn = org.omg.CSI.X501DistinguishedNameHelper.read (istream);
        value.dn (_dn);
        break;
      default:
        byte _id[] = null;
        _id = org.omg.CSI.IdentityExtensionHelper.read (istream);
        value.id (_dis0, _id);
        break;
    }
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSI.IdentityToken value)
  {
    ostream.write_ulong (value.discriminator ());
    switch (value.discriminator ())
    {
      case org.omg.CSI.ITTAbsent.value:
        ostream.write_boolean (value.absent ());
        break;
      case org.omg.CSI.ITTAnonymous.value:
        ostream.write_boolean (value.anonymous ());
        break;
      case org.omg.CSI.ITTPrincipalName.value:
        org.omg.CSI.GSS_NT_ExportedNameHelper.write (ostream, value.principal_name ());
        break;
      case org.omg.CSI.ITTX509CertChain.value:
        org.omg.CSI.X509CertificateChainHelper.write (ostream, value.certificate_chain ());
        break;
      case org.omg.CSI.ITTDistinguishedName.value:
        org.omg.CSI.X501DistinguishedNameHelper.write (ostream, value.dn ());
        break;
      default:
        org.omg.CSI.IdentityExtensionHelper.write (ostream, value.id ());
        break;
    }
  }

}

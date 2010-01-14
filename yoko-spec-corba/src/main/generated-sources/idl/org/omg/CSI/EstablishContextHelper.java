package org.omg.CSI;


/**
* org/omg/CSI/EstablishContextHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class EstablishContextHelper
{
  private static String  _id = "IDL:omg.org/CSI/EstablishContext:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSI.EstablishContext that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSI.EstablishContext extract (org.omg.CORBA.Any a)
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
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulonglong);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.ContextIdHelper.id (), "ContextId", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "client_context_id",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CSI.AuthorizationElementHelper.type ();
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.AuthorizationTokenHelper.id (), "AuthorizationToken", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "authorization_token",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CSI.IdentityTokenHelper.type ();
          _members0[2] = new org.omg.CORBA.StructMember (
            "identity_token",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.GSSTokenHelper.id (), "GSSToken", _tcOf_members0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "client_authentication_token",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CSI.EstablishContextHelper.id (), "EstablishContext", _members0);
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

  public static org.omg.CSI.EstablishContext read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSI.EstablishContext value = new org.omg.CSI.EstablishContext ();
    value.client_context_id = istream.read_ulonglong ();
    value.authorization_token = org.omg.CSI.AuthorizationTokenHelper.read (istream);
    value.identity_token = org.omg.CSI.IdentityTokenHelper.read (istream);
    value.client_authentication_token = org.omg.CSI.GSSTokenHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSI.EstablishContext value)
  {
    ostream.write_ulonglong (value.client_context_id);
    org.omg.CSI.AuthorizationTokenHelper.write (ostream, value.authorization_token);
    org.omg.CSI.IdentityTokenHelper.write (ostream, value.identity_token);
    org.omg.CSI.GSSTokenHelper.write (ostream, value.client_authentication_token);
  }

}

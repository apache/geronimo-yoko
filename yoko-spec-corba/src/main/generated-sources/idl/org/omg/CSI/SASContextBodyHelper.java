package org.omg.CSI;


/**
* org/omg/CSI/SASContextBodyHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

abstract public class SASContextBodyHelper
{
  private static String  _id = "IDL:omg.org/CSI/SASContextBody:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CSI.SASContextBody that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CSI.SASContextBody extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      org.omg.CORBA.TypeCode _disTypeCode0;
      _disTypeCode0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_short);
      _disTypeCode0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CSI.MsgTypeHelper.id (), "MsgType", _disTypeCode0);
      org.omg.CORBA.UnionMember[] _members0 = new org.omg.CORBA.UnionMember [4];
      org.omg.CORBA.TypeCode _tcOf_members0;
      org.omg.CORBA.Any _anyOf_members0;

      // Branch for establish_msg (case label org.omg.CSI.MTEstablishContext.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_short ((short)org.omg.CSI.MTEstablishContext.value);
      _tcOf_members0 = org.omg.CSI.EstablishContextHelper.type ();
      _members0[0] = new org.omg.CORBA.UnionMember (
        "establish_msg",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for complete_msg (case label org.omg.CSI.MTCompleteEstablishContext.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_short ((short)org.omg.CSI.MTCompleteEstablishContext.value);
      _tcOf_members0 = org.omg.CSI.CompleteEstablishContextHelper.type ();
      _members0[1] = new org.omg.CORBA.UnionMember (
        "complete_msg",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for error_msg (case label org.omg.CSI.MTContextError.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_short ((short)org.omg.CSI.MTContextError.value);
      _tcOf_members0 = org.omg.CSI.ContextErrorHelper.type ();
      _members0[2] = new org.omg.CORBA.UnionMember (
        "error_msg",
        _anyOf_members0,
        _tcOf_members0,
        null);

      // Branch for in_context_msg (case label org.omg.CSI.MTMessageInContext.value)
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_short ((short)org.omg.CSI.MTMessageInContext.value);
      _tcOf_members0 = org.omg.CSI.MessageInContextHelper.type ();
      _members0[3] = new org.omg.CORBA.UnionMember (
        "in_context_msg",
        _anyOf_members0,
        _tcOf_members0,
        null);
      __typeCode = org.omg.CORBA.ORB.init ().create_union_tc (org.omg.CSI.SASContextBodyHelper.id (), "SASContextBody", _disTypeCode0, _members0);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.CSI.SASContextBody read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CSI.SASContextBody value = new org.omg.CSI.SASContextBody ();
    short _dis0 = (short)0;
    _dis0 = istream.read_short ();
    switch (_dis0)
    {
      case org.omg.CSI.MTEstablishContext.value:
        org.omg.CSI.EstablishContext _establish_msg = null;
        _establish_msg = org.omg.CSI.EstablishContextHelper.read (istream);
        value.establish_msg (_establish_msg);
        break;
      case org.omg.CSI.MTCompleteEstablishContext.value:
        org.omg.CSI.CompleteEstablishContext _complete_msg = null;
        _complete_msg = org.omg.CSI.CompleteEstablishContextHelper.read (istream);
        value.complete_msg (_complete_msg);
        break;
      case org.omg.CSI.MTContextError.value:
        org.omg.CSI.ContextError _error_msg = null;
        _error_msg = org.omg.CSI.ContextErrorHelper.read (istream);
        value.error_msg (_error_msg);
        break;
      case org.omg.CSI.MTMessageInContext.value:
        org.omg.CSI.MessageInContext _in_context_msg = null;
        _in_context_msg = org.omg.CSI.MessageInContextHelper.read (istream);
        value.in_context_msg (_in_context_msg);
        break;
      default:
        value._default( _dis0 ) ;
        break;
    }
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CSI.SASContextBody value)
  {
    ostream.write_short (value.discriminator ());
    switch (value.discriminator ())
    {
      case org.omg.CSI.MTEstablishContext.value:
        org.omg.CSI.EstablishContextHelper.write (ostream, value.establish_msg ());
        break;
      case org.omg.CSI.MTCompleteEstablishContext.value:
        org.omg.CSI.CompleteEstablishContextHelper.write (ostream, value.complete_msg ());
        break;
      case org.omg.CSI.MTContextError.value:
        org.omg.CSI.ContextErrorHelper.write (ostream, value.error_msg ());
        break;
      case org.omg.CSI.MTMessageInContext.value:
        org.omg.CSI.MessageInContextHelper.write (ostream, value.in_context_msg ());
        break;
    }
  }

}

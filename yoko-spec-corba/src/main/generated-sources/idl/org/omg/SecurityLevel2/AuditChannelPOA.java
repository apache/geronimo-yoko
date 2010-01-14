package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/AuditChannelPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public abstract class AuditChannelPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.AuditChannelOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("audit_write", new java.lang.Integer (0));
    _methods.put ("_get_audit_channel_id", new java.lang.Integer (1));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // SecurityLevel2/AuditChannel/audit_write
       {
         org.omg.Security.AuditEventType event_type = org.omg.Security.AuditEventTypeHelper.read (in);
         org.omg.SecurityLevel2.Credentials creds[] = org.omg.SecurityLevel2.CredentialsListHelper.read (in);
         org.omg.TimeBase.UtcT time = org.omg.Security.UtcTHelper.read (in);
         org.omg.Security.SelectorValue descriptors[] = org.omg.Security.SelectorValueListHelper.read (in);
         org.omg.CORBA.Any event_specific_data = in.read_any ();
         this.audit_write (event_type, creds, time, descriptors, event_specific_data);
         out = $rh.createReply();
         break;
       }

       case 1:  // SecurityLevel2/AuditChannel/_get_audit_channel_id
       {
         int $result = (int)0;
         $result = this.audit_channel_id ();
         out = $rh.createReply();
         out.write_ulong ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/AuditChannel:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public AuditChannel _this() 
  {
    return AuditChannelHelper.narrow(
    super._this_object());
  }

  public AuditChannel _this(org.omg.CORBA.ORB orb) 
  {
    return AuditChannelHelper.narrow(
    super._this_object(orb));
  }


} // class AuditChannelPOA

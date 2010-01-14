package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/AuditDecisionPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public abstract class AuditDecisionPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.AuditDecisionOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("audit_needed", new java.lang.Integer (0));
    _methods.put ("_get_audit_channel", new java.lang.Integer (1));
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
       case 0:  // SecurityLevel2/AuditDecision/audit_needed
       {
         org.omg.Security.AuditEventType event_type = org.omg.Security.AuditEventTypeHelper.read (in);
         org.omg.Security.SelectorValue value_list[] = org.omg.Security.SelectorValueListHelper.read (in);
         boolean $result = false;
         $result = this.audit_needed (event_type, value_list);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 1:  // SecurityLevel2/AuditDecision/_get_audit_channel
       {
         org.omg.SecurityLevel2.AuditChannel $result = null;
         $result = this.audit_channel ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.AuditChannelHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/AuditDecision:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public AuditDecision _this() 
  {
    return AuditDecisionHelper.narrow(
    super._this_object());
  }

  public AuditDecision _this(org.omg.CORBA.ORB orb) 
  {
    return AuditDecisionHelper.narrow(
    super._this_object(orb));
  }


} // class AuditDecisionPOA

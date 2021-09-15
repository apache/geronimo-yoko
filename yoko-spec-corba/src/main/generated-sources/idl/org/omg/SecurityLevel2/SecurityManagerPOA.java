package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/SecurityManagerPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


/* */
public abstract class SecurityManagerPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.SecurityManagerOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("_get_supported_mechanisms", 0);
    _methods.put ("_get_own_credentials", 1);
    _methods.put ("_get_required_rights_object", 2);
    _methods.put ("_get_principal_authenticator", 3);
    _methods.put ("_get_access_decision", 4);
    _methods.put ("_get_audit_decision", 5);
    _methods.put ("get_target_credentials", 6);
    _methods.put ("remove_own_credentials", 7);
    _methods.put ("get_security_policy", 8);
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

  // Process/Capsule/ORB Instance specific operations
       case 0:  // SecurityLevel2/SecurityManager/_get_supported_mechanisms
       {
         org.omg.Security.MechandOptions $result[] = null;
         $result = this.supported_mechanisms ();
         out = $rh.createReply();
         org.omg.Security.MechandOptionsListHelper.write (out, $result);
         break;
       }

       case 1:  // SecurityLevel2/SecurityManager/_get_own_credentials
       {
         org.omg.SecurityLevel2.Credentials $result[] = null;
         $result = this.own_credentials ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.CredentialsListHelper.write (out, $result);
         break;
       }

       case 2:  // SecurityLevel2/SecurityManager/_get_required_rights_object
       {
         org.omg.SecurityLevel2.RequiredRights $result = null;
         $result = this.required_rights_object ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.RequiredRightsHelper.write (out, $result);
         break;
       }

       case 3:  // SecurityLevel2/SecurityManager/_get_principal_authenticator
       {
         org.omg.SecurityLevel2.PrincipalAuthenticator $result = null;
         $result = this.principal_authenticator ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.PrincipalAuthenticatorHelper.write (out, $result);
         break;
       }

       case 4:  // SecurityLevel2/SecurityManager/_get_access_decision
       {
         org.omg.SecurityLevel2.AccessDecision $result = null;
         $result = this.access_decision ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.AccessDecisionHelper.write (out, $result);
         break;
       }

       case 5:  // SecurityLevel2/SecurityManager/_get_audit_decision
       {
         org.omg.SecurityLevel2.AuditDecision $result = null;
         $result = this.audit_decision ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.AuditDecisionHelper.write (out, $result);
         break;
       }

       case 6:  // SecurityLevel2/SecurityManager/get_target_credentials
       {
         org.omg.CORBA.Object obj_ref = org.omg.CORBA.ObjectHelper.read (in);
         org.omg.SecurityLevel2.TargetCredentials $result = null;
         $result = this.get_target_credentials (obj_ref);
         out = $rh.createReply();
         org.omg.SecurityLevel2.TargetCredentialsHelper.write (out, $result);
         break;
       }

       case 7:  // SecurityLevel2/SecurityManager/remove_own_credentials
       {
         org.omg.SecurityLevel2.Credentials creds = org.omg.SecurityLevel2.CredentialsHelper.read (in);
         this.remove_own_credentials (creds);
         out = $rh.createReply();
         break;
       }

       case 8:  // SecurityLevel2/SecurityManager/get_security_policy
       {
         int policy_type = org.omg.CORBA.PolicyTypeHelper.read (in);
         org.omg.CORBA.Policy $result = null;
         $result = this.get_security_policy (policy_type);
         out = $rh.createReply();
         org.omg.CORBA.PolicyHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/SecurityManager:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public SecurityManager _this() 
  {
    return SecurityManagerHelper.narrow(
    super._this_object());
  }

  public SecurityManager _this(org.omg.CORBA.ORB orb) 
  {
    return SecurityManagerHelper.narrow(
    super._this_object(orb));
  }


} // class SecurityManagerPOA

package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/AccessDecisionPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public abstract class AccessDecisionPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.AccessDecisionOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("access_allowed", 0);
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
       case 0:  // SecurityLevel2/AccessDecision/access_allowed
       {
         org.omg.SecurityLevel2.Credentials cred_list[] = org.omg.SecurityLevel2.CredentialsListHelper.read (in);
         org.omg.CORBA.Object target = org.omg.CORBA.ObjectHelper.read (in);
         String operation_name = org.omg.CORBA.IdentifierHelper.read (in);
         String target_interface_name = org.omg.CORBA.IdentifierHelper.read (in);
         boolean $result = false;
         $result = this.access_allowed (cred_list, target, operation_name, target_interface_name);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/AccessDecision:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public AccessDecision _this() 
  {
    return AccessDecisionHelper.narrow(
    super._this_object());
  }

  public AccessDecision _this(org.omg.CORBA.ORB orb) 
  {
    return AccessDecisionHelper.narrow(
    super._this_object(orb));
  }


} // class AccessDecisionPOA

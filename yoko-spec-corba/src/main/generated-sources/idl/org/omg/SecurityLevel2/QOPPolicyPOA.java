package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/QOPPolicyPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public abstract class QOPPolicyPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.QOPPolicyOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("_get_qop", new java.lang.Integer (0));
    _methods.put ("_get_policy_type", new java.lang.Integer (1));
    _methods.put ("copy", new java.lang.Integer (2));
    _methods.put ("destroy", new java.lang.Integer (3));
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
       case 0:  // SecurityLevel2/QOPPolicy/_get_qop
       {
         org.omg.Security.QOP $result = null;
         $result = this.qop ();
         out = $rh.createReply();
         org.omg.Security.QOPHelper.write (out, $result);
         break;
       }

       case 1:  // org/omg/CORBA/Policy/_get_policy_type
       {
         int $result = (int)0;
         $result = this.policy_type ();
         out = $rh.createReply();
         out.write_ulong ($result);
         break;
       }

       case 2:  // org/omg/CORBA/Policy/copy
       {
         org.omg.CORBA.Policy $result = null;
         $result = this.copy ();
         out = $rh.createReply();
         org.omg.CORBA.PolicyHelper.write (out, $result);
         break;
       }

       case 3:  // org/omg/CORBA/Policy/destroy
       {
         this.destroy ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/QOPPolicy:1.0", 
    "IDL:CORBA/Policy:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public QOPPolicy _this() 
  {
    return QOPPolicyHelper.narrow(
    super._this_object());
  }

  public QOPPolicy _this(org.omg.CORBA.ORB orb) 
  {
    return QOPPolicyHelper.narrow(
    super._this_object(orb));
  }


} // class QOPPolicyPOA

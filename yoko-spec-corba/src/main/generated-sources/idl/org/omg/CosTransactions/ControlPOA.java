package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/ControlPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public abstract class ControlPOA extends org.omg.PortableServer.Servant
 implements org.omg.CosTransactions.ControlOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("get_terminator", new java.lang.Integer (0));
    _methods.put ("get_coordinator", new java.lang.Integer (1));
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
       case 0:  // CosTransactions/Control/get_terminator
       {
         try {
           org.omg.CosTransactions.Terminator $result = null;
           $result = this.get_terminator ();
           out = $rh.createReply();
           org.omg.CosTransactions.TerminatorHelper.write (out, $result);
         } catch (org.omg.CosTransactions.Unavailable $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.UnavailableHelper.write (out, $ex);
         }
         break;
       }

       case 1:  // CosTransactions/Control/get_coordinator
       {
         try {
           org.omg.CosTransactions.Coordinator $result = null;
           $result = this.get_coordinator ();
           out = $rh.createReply();
           org.omg.CosTransactions.CoordinatorHelper.write (out, $result);
         } catch (org.omg.CosTransactions.Unavailable $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.UnavailableHelper.write (out, $ex);
         }
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CosTransactions/Control:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Control _this() 
  {
    return ControlHelper.narrow(
    super._this_object());
  }

  public Control _this(org.omg.CORBA.ORB orb) 
  {
    return ControlHelper.narrow(
    super._this_object(orb));
  }


} // class ControlPOA

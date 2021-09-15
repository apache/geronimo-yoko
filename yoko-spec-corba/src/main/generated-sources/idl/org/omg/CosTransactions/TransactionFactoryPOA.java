package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/TransactionFactoryPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public abstract class TransactionFactoryPOA extends org.omg.PortableServer.Servant
 implements org.omg.CosTransactions.TransactionFactoryOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("create", 0);
    _methods.put ("recreate", 1);
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
       case 0:  // CosTransactions/TransactionFactory/create
       {
         int time_out = in.read_ulong ();
         org.omg.CosTransactions.Control $result = null;
         $result = this.create (time_out);
         out = $rh.createReply();
         org.omg.CosTransactions.ControlHelper.write (out, $result);
         break;
       }

       case 1:  // CosTransactions/TransactionFactory/recreate
       {
         org.omg.CosTransactions.PropagationContext ctx = org.omg.CosTransactions.PropagationContextHelper.read (in);
         org.omg.CosTransactions.Control $result = null;
         $result = this.recreate (ctx);
         out = $rh.createReply();
         org.omg.CosTransactions.ControlHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CosTransactions/TransactionFactory:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public TransactionFactory _this() 
  {
    return TransactionFactoryHelper.narrow(
    super._this_object());
  }

  public TransactionFactory _this(org.omg.CORBA.ORB orb) 
  {
    return TransactionFactoryHelper.narrow(
    super._this_object(orb));
  }


} // class TransactionFactoryPOA

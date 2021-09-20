package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/CoordinatorPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public abstract class CoordinatorPOA extends org.omg.PortableServer.Servant
 implements org.omg.CosTransactions.CoordinatorOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("get_status", 0);
    _methods.put ("get_parent_status", 1);
    _methods.put ("get_top_level_status", 2);
    _methods.put ("is_same_transaction", 3);
    _methods.put ("is_related_transaction", 4);
    _methods.put ("is_ancestor_transaction", 5);
    _methods.put ("is_descendant_transaction", 6);
    _methods.put ("is_top_level_transaction", 7);
    _methods.put ("hash_transaction", 8);
    _methods.put ("hash_top_level_tran", 9);
    _methods.put ("register_resource", 10);
    _methods.put ("register_synchronization", 11);
    _methods.put ("register_subtran_aware", 12);
    _methods.put ("rollback_only", 13);
    _methods.put ("get_transaction_name", 14);
    _methods.put ("create_subtransaction", 15);
    _methods.put ("get_txcontext", 16);
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
       case 0:  // CosTransactions/Coordinator/get_status
       {
         org.omg.CosTransactions.Status $result = null;
         $result = this.get_status ();
         out = $rh.createReply();
         org.omg.CosTransactions.StatusHelper.write (out, $result);
         break;
       }

       case 1:  // CosTransactions/Coordinator/get_parent_status
       {
         org.omg.CosTransactions.Status $result = null;
         $result = this.get_parent_status ();
         out = $rh.createReply();
         org.omg.CosTransactions.StatusHelper.write (out, $result);
         break;
       }

       case 2:  // CosTransactions/Coordinator/get_top_level_status
       {
         org.omg.CosTransactions.Status $result = null;
         $result = this.get_top_level_status ();
         out = $rh.createReply();
         org.omg.CosTransactions.StatusHelper.write (out, $result);
         break;
       }

       case 3:  // CosTransactions/Coordinator/is_same_transaction
       {
         org.omg.CosTransactions.Coordinator tc = org.omg.CosTransactions.CoordinatorHelper.read (in);
         boolean $result = false;
         $result = this.is_same_transaction (tc);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 4:  // CosTransactions/Coordinator/is_related_transaction
       {
         org.omg.CosTransactions.Coordinator tc = org.omg.CosTransactions.CoordinatorHelper.read (in);
         boolean $result = false;
         $result = this.is_related_transaction (tc);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 5:  // CosTransactions/Coordinator/is_ancestor_transaction
       {
         org.omg.CosTransactions.Coordinator tc = org.omg.CosTransactions.CoordinatorHelper.read (in);
         boolean $result = false;
         $result = this.is_ancestor_transaction (tc);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 6:  // CosTransactions/Coordinator/is_descendant_transaction
       {
         org.omg.CosTransactions.Coordinator tc = org.omg.CosTransactions.CoordinatorHelper.read (in);
         boolean $result = false;
         $result = this.is_descendant_transaction (tc);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 7:  // CosTransactions/Coordinator/is_top_level_transaction
       {
         boolean $result = false;
         $result = this.is_top_level_transaction ();
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 8:  // CosTransactions/Coordinator/hash_transaction
       {
         int $result = (int)0;
         $result = this.hash_transaction ();
         out = $rh.createReply();
         out.write_ulong ($result);
         break;
       }

       case 9:  // CosTransactions/Coordinator/hash_top_level_tran
       {
         int $result = (int)0;
         $result = this.hash_top_level_tran ();
         out = $rh.createReply();
         out.write_ulong ($result);
         break;
       }

       case 10:  // CosTransactions/Coordinator/register_resource
       {
         try {
           org.omg.CosTransactions.Resource r = org.omg.CosTransactions.ResourceHelper.read (in);
           org.omg.CosTransactions.RecoveryCoordinator $result = null;
           $result = this.register_resource (r);
           out = $rh.createReply();
           org.omg.CosTransactions.RecoveryCoordinatorHelper.write (out, $result);
         } catch (org.omg.CosTransactions.Inactive $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.InactiveHelper.write (out, $ex);
         }
         break;
       }

       case 11:  // CosTransactions/Coordinator/register_synchronization
       {
         try {
           org.omg.CosTransactions.Synchronization sync = org.omg.CosTransactions.SynchronizationHelper.read (in);
           this.register_synchronization (sync);
           out = $rh.createReply();
         } catch (org.omg.CosTransactions.Inactive $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.InactiveHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.SynchronizationUnavailable $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.SynchronizationUnavailableHelper.write (out, $ex);
         }
         break;
       }

       case 12:  // CosTransactions/Coordinator/register_subtran_aware
       {
         try {
           org.omg.CosTransactions.SubtransactionAwareResource r = org.omg.CosTransactions.SubtransactionAwareResourceHelper.read (in);
           this.register_subtran_aware (r);
           out = $rh.createReply();
         } catch (org.omg.CosTransactions.Inactive $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.InactiveHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.NotSubtransaction $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.NotSubtransactionHelper.write (out, $ex);
         }
         break;
       }

       case 13:  // CosTransactions/Coordinator/rollback_only
       {
         try {
           this.rollback_only ();
           out = $rh.createReply();
         } catch (org.omg.CosTransactions.Inactive $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.InactiveHelper.write (out, $ex);
         }
         break;
       }

       case 14:  // CosTransactions/Coordinator/get_transaction_name
       {
         String $result = null;
         $result = this.get_transaction_name ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 15:  // CosTransactions/Coordinator/create_subtransaction
       {
         try {
           org.omg.CosTransactions.Control $result = null;
           $result = this.create_subtransaction ();
           out = $rh.createReply();
           org.omg.CosTransactions.ControlHelper.write (out, $result);
         } catch (org.omg.CosTransactions.SubtransactionsUnavailable $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.SubtransactionsUnavailableHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.Inactive $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.InactiveHelper.write (out, $ex);
         }
         break;
       }

       case 16:  // CosTransactions/Coordinator/get_txcontext
       {
         try {
           org.omg.CosTransactions.PropagationContext $result = null;
           $result = this.get_txcontext ();
           out = $rh.createReply();
           org.omg.CosTransactions.PropagationContextHelper.write (out, $result);
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
    "IDL:CosTransactions/Coordinator:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Coordinator _this() 
  {
    return CoordinatorHelper.narrow(
    super._this_object());
  }

  public Coordinator _this(org.omg.CORBA.ORB orb) 
  {
    return CoordinatorHelper.narrow(
    super._this_object(orb));
  }


} // class CoordinatorPOA

package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/ResourcePOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public abstract class ResourcePOA extends org.omg.PortableServer.Servant
 implements org.omg.CosTransactions.ResourceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("prepare", 0);
    _methods.put ("rollback", 1);
    _methods.put ("commit", 2);
    _methods.put ("commit_one_phase", 3);
    _methods.put ("forget", 4);
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
       case 0:  // CosTransactions/Resource/prepare
       {
         try {
           org.omg.CosTransactions.Vote $result = null;
           $result = this.prepare ();
           out = $rh.createReply();
           org.omg.CosTransactions.VoteHelper.write (out, $result);
         } catch (org.omg.CosTransactions.HeuristicMixed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicMixedHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.HeuristicHazard $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicHazardHelper.write (out, $ex);
         }
         break;
       }

       case 1:  // CosTransactions/Resource/rollback
       {
         try {
           this.rollback ();
           out = $rh.createReply();
         } catch (org.omg.CosTransactions.HeuristicCommit $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicCommitHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.HeuristicMixed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicMixedHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.HeuristicHazard $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicHazardHelper.write (out, $ex);
         }
         break;
       }

       case 2:  // CosTransactions/Resource/commit
       {
         try {
           this.commit ();
           out = $rh.createReply();
         } catch (org.omg.CosTransactions.NotPrepared $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.NotPreparedHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.HeuristicRollback $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicRollbackHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.HeuristicMixed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicMixedHelper.write (out, $ex);
         } catch (org.omg.CosTransactions.HeuristicHazard $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicHazardHelper.write (out, $ex);
         }
         break;
       }

       case 3:  // CosTransactions/Resource/commit_one_phase
       {
         try {
           this.commit_one_phase ();
           out = $rh.createReply();
         } catch (org.omg.CosTransactions.HeuristicHazard $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosTransactions.HeuristicHazardHelper.write (out, $ex);
         }
         break;
       }

       case 4:  // CosTransactions/Resource/forget
       {
         this.forget ();
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
    "IDL:CosTransactions/Resource:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Resource _this() 
  {
    return ResourceHelper.narrow(
    super._this_object());
  }

  public Resource _this(org.omg.CORBA.ORB orb) 
  {
    return ResourceHelper.narrow(
    super._this_object(orb));
  }


} // class ResourcePOA

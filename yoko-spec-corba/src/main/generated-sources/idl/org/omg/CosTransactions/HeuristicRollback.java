package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/HeuristicRollback.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class HeuristicRollback extends org.omg.CORBA.UserException
{

  public HeuristicRollback ()
  {
    super(HeuristicRollbackHelper.id());
  } // ctor


  public HeuristicRollback (String $reason)
  {
    super(HeuristicRollbackHelper.id() + "  " + $reason);
  } // ctor

} // class HeuristicRollback

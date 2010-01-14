package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/HeuristicCommit.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class HeuristicCommit extends org.omg.CORBA.UserException
{

  public HeuristicCommit ()
  {
    super(HeuristicCommitHelper.id());
  } // ctor


  public HeuristicCommit (String $reason)
  {
    super(HeuristicCommitHelper.id() + "  " + $reason);
  } // ctor

} // class HeuristicCommit

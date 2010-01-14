package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/ResourceOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public interface ResourceOperations 
{
  org.omg.CosTransactions.Vote prepare () throws org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void rollback () throws org.omg.CosTransactions.HeuristicCommit, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void commit () throws org.omg.CosTransactions.NotPrepared, org.omg.CosTransactions.HeuristicRollback, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void commit_one_phase () throws org.omg.CosTransactions.HeuristicHazard;
  void forget ();
} // interface ResourceOperations

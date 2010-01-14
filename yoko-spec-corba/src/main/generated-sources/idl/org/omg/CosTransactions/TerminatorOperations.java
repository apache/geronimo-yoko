package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/TerminatorOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public interface TerminatorOperations 
{
  void commit (boolean report_heuristics) throws org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void rollback ();
} // interface TerminatorOperations

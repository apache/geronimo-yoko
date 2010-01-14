package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/SubtransactionAwareResourceOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public interface SubtransactionAwareResourceOperations  extends org.omg.CosTransactions.ResourceOperations
{
  void commit_subtransaction (org.omg.CosTransactions.Coordinator parent);
  void rollback_subtransaction ();
} // interface SubtransactionAwareResourceOperations

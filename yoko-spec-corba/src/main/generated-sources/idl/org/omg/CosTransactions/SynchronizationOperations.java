package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/SynchronizationOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


// Inheritance from TransactionalObject is for backward compatability //
public interface SynchronizationOperations  extends org.omg.CosTransactions.TransactionalObjectOperations
{
  void before_completion ();
  void after_completion (org.omg.CosTransactions.Status s);
} // interface SynchronizationOperations

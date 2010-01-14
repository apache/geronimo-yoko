package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/TransactionFactoryOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public interface TransactionFactoryOperations 
{
  org.omg.CosTransactions.Control create (int time_out);
  org.omg.CosTransactions.Control recreate (org.omg.CosTransactions.PropagationContext ctx);
} // interface TransactionFactoryOperations

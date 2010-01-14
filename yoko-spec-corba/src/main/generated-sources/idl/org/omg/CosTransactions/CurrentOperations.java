package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/CurrentOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


// Current transaction
public interface CurrentOperations  extends org.omg.CORBA.CurrentOperations
{
  void begin () throws org.omg.CosTransactions.SubtransactionsUnavailable;
  void commit (boolean report_heuristics) throws org.omg.CosTransactions.NoTransaction, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void rollback () throws org.omg.CosTransactions.NoTransaction;
  void rollback_only () throws org.omg.CosTransactions.NoTransaction;
  org.omg.CosTransactions.Status get_status ();
  String get_transaction_name ();
  void set_timeout (int seconds);
  int get_timeout ();
  org.omg.CosTransactions.Control get_control ();
  org.omg.CosTransactions.Control suspend ();
  void resume (org.omg.CosTransactions.Control which) throws org.omg.CosTransactions.InvalidControl;
} // interface CurrentOperations

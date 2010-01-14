package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/NoTransaction.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class NoTransaction extends org.omg.CORBA.UserException
{

  public NoTransaction ()
  {
    super(NoTransactionHelper.id());
  } // ctor


  public NoTransaction (String $reason)
  {
    super(NoTransactionHelper.id() + "  " + $reason);
  } // ctor

} // class NoTransaction

package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/SubtransactionsUnavailable.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class SubtransactionsUnavailable extends org.omg.CORBA.UserException
{

  public SubtransactionsUnavailable ()
  {
    super(SubtransactionsUnavailableHelper.id());
  } // ctor


  public SubtransactionsUnavailable (String $reason)
  {
    super(SubtransactionsUnavailableHelper.id() + "  " + $reason);
  } // ctor

} // class SubtransactionsUnavailable

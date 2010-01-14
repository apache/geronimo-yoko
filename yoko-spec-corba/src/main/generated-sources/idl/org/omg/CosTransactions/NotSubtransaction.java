package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/NotSubtransaction.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class NotSubtransaction extends org.omg.CORBA.UserException
{

  public NotSubtransaction ()
  {
    super(NotSubtransactionHelper.id());
  } // ctor


  public NotSubtransaction (String $reason)
  {
    super(NotSubtransactionHelper.id() + "  " + $reason);
  } // ctor

} // class NotSubtransaction

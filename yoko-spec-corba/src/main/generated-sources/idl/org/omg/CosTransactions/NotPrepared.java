package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/NotPrepared.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class NotPrepared extends org.omg.CORBA.UserException
{

  public NotPrepared ()
  {
    super(NotPreparedHelper.id());
  } // ctor


  public NotPrepared (String $reason)
  {
    super(NotPreparedHelper.id() + "  " + $reason);
  } // ctor

} // class NotPrepared

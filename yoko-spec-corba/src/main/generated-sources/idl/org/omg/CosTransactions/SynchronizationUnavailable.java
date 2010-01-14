package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/SynchronizationUnavailable.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class SynchronizationUnavailable extends org.omg.CORBA.UserException
{

  public SynchronizationUnavailable ()
  {
    super(SynchronizationUnavailableHelper.id());
  } // ctor


  public SynchronizationUnavailable (String $reason)
  {
    super(SynchronizationUnavailableHelper.id() + "  " + $reason);
  } // ctor

} // class SynchronizationUnavailable

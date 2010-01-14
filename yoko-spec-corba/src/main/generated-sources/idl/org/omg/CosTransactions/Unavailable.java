package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/Unavailable.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class Unavailable extends org.omg.CORBA.UserException
{

  public Unavailable ()
  {
    super(UnavailableHelper.id());
  } // ctor


  public Unavailable (String $reason)
  {
    super(UnavailableHelper.id() + "  " + $reason);
  } // ctor

} // class Unavailable

package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/Inactive.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class Inactive extends org.omg.CORBA.UserException
{

  public Inactive ()
  {
    super(InactiveHelper.id());
  } // ctor


  public Inactive (String $reason)
  {
    super(InactiveHelper.id() + "  " + $reason);
  } // ctor

} // class Inactive

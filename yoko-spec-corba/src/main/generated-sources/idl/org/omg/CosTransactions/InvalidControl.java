package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/InvalidControl.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class InvalidControl extends org.omg.CORBA.UserException
{

  public InvalidControl ()
  {
    super(InvalidControlHelper.id());
  } // ctor


  public InvalidControl (String $reason)
  {
    super(InvalidControlHelper.id() + "  " + $reason);
  } // ctor

} // class InvalidControl

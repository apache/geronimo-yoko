package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/HeuristicHazard.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class HeuristicHazard extends org.omg.CORBA.UserException
{

  public HeuristicHazard ()
  {
    super(HeuristicHazardHelper.id());
  } // ctor


  public HeuristicHazard (String $reason)
  {
    super(HeuristicHazardHelper.id() + "  " + $reason);
  } // ctor

} // class HeuristicHazard

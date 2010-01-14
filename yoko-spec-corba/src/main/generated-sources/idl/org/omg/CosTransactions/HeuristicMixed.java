package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/HeuristicMixed.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class HeuristicMixed extends org.omg.CORBA.UserException
{

  public HeuristicMixed ()
  {
    super(HeuristicMixedHelper.id());
  } // ctor


  public HeuristicMixed (String $reason)
  {
    super(HeuristicMixedHelper.id() + "  " + $reason);
  } // ctor

} // class HeuristicMixed

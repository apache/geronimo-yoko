package org.omg.CosNaming.NamingContextExtPackage;


/**
* org/omg/CosNaming/NamingContextExtPackage/InvalidAddress.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class InvalidAddress extends org.omg.CORBA.UserException
{

  public InvalidAddress ()
  {
    super(InvalidAddressHelper.id());
  } // ctor


  public InvalidAddress (String $reason)
  {
    super(InvalidAddressHelper.id() + "  " + $reason);
  } // ctor

} // class InvalidAddress

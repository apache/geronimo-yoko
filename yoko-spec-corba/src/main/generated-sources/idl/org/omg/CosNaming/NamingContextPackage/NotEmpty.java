package org.omg.CosNaming.NamingContextPackage;


/**
* org/omg/CosNaming/NamingContextPackage/NotEmpty.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class NotEmpty extends org.omg.CORBA.UserException
{

  public NotEmpty ()
  {
    super(NotEmptyHelper.id());
  } // ctor


  public NotEmpty (String $reason)
  {
    super(NotEmptyHelper.id() + "  " + $reason);
  } // ctor

} // class NotEmpty

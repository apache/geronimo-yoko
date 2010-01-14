package org.omg.Security;


/**
* org/omg/Security/EstablishTrust.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class EstablishTrust implements org.omg.CORBA.portable.IDLEntity
{
  public boolean trust_in_client = false;
  public boolean trust_in_target = false;

  public EstablishTrust ()
  {
  } // ctor

  public EstablishTrust (boolean _trust_in_client, boolean _trust_in_target)
  {
    trust_in_client = _trust_in_client;
    trust_in_target = _trust_in_target;
  } // ctor

} // class EstablishTrust

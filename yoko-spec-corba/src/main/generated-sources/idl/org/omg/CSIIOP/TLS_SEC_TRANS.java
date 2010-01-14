package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/TLS_SEC_TRANS.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TLS_SEC_TRANS implements org.omg.CORBA.portable.IDLEntity
{
  public short target_supports = (short)0;
  public short target_requires = (short)0;
  public org.omg.CSIIOP.TransportAddress addresses[] = null;

  public TLS_SEC_TRANS ()
  {
  } // ctor

  public TLS_SEC_TRANS (short _target_supports, short _target_requires, org.omg.CSIIOP.TransportAddress[] _addresses)
  {
    target_supports = _target_supports;
    target_requires = _target_requires;
    addresses = _addresses;
  } // ctor

} // class TLS_SEC_TRANS

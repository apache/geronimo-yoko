package org.omg.SSLIOP;


/**
* org/omg/SSLIOP/SSL.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SSL implements org.omg.CORBA.portable.IDLEntity
{
  public short target_supports = (short)0;
  public short target_requires = (short)0;
  public short port = (short)0;

  public SSL ()
  {
  } // ctor

  public SSL (short _target_supports, short _target_requires, short _port)
  {
    target_supports = _target_supports;
    target_requires = _target_requires;
    port = _port;
  } // ctor

} // class SSL

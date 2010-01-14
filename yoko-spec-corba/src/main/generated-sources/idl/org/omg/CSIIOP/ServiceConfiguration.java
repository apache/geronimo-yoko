package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/ServiceConfiguration.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class ServiceConfiguration implements org.omg.CORBA.portable.IDLEntity
{
  public int syntax = (int)0;
  public byte name[] = null;

  public ServiceConfiguration ()
  {
  } // ctor

  public ServiceConfiguration (int _syntax, byte[] _name)
  {
    syntax = _syntax;
    name = _name;
  } // ctor

} // class ServiceConfiguration

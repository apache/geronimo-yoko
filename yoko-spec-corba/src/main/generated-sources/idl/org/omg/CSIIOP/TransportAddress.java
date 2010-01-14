package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/TransportAddress.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class TransportAddress implements org.omg.CORBA.portable.IDLEntity
{
  public String host_name = null;
  public short port = (short)0;

  public TransportAddress ()
  {
  } // ctor

  public TransportAddress (String _host_name, short _port)
  {
    host_name = _host_name;
    port = _port;
  } // ctor

} // class TransportAddress

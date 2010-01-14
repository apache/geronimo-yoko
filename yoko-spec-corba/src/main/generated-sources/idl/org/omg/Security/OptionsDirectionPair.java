package org.omg.Security;


/**
* org/omg/Security/OptionsDirectionPair.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class OptionsDirectionPair implements org.omg.CORBA.portable.IDLEntity
{
  public short options = (short)0;
  public org.omg.Security.CommunicationDirection direction = null;

  public OptionsDirectionPair ()
  {
  } // ctor

  public OptionsDirectionPair (short _options, org.omg.Security.CommunicationDirection _direction)
  {
    options = _options;
    direction = _direction;
  } // ctor

} // class OptionsDirectionPair

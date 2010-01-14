package org.omg.Security;


/**
* org/omg/Security/MechandOptions.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class MechandOptions implements org.omg.CORBA.portable.IDLEntity
{
  public String mechanism_type = null;
  public short options_supported = (short)0;

  public MechandOptions ()
  {
  } // ctor

  public MechandOptions (String _mechanism_type, short _options_supported)
  {
    mechanism_type = _mechanism_type;
    options_supported = _options_supported;
  } // ctor

} // class MechandOptions

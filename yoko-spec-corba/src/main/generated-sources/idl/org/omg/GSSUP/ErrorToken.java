package org.omg.GSSUP;


/**
* org/omg/GSSUP/ErrorToken.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class ErrorToken implements org.omg.CORBA.portable.IDLEntity
{
  public int error_code = (int)0;

  public ErrorToken ()
  {
  } // ctor

  public ErrorToken (int _error_code)
  {
    error_code = _error_code;
  } // ctor

} // class ErrorToken

package org.omg.GSSUP;


/**
* org/omg/GSSUP/InitialContextToken.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class InitialContextToken implements org.omg.CORBA.portable.IDLEntity
{
  public byte username[] = null;
  public byte password[] = null;
  public byte target_name[] = null;

  public InitialContextToken ()
  {
  } // ctor

  public InitialContextToken (byte[] _username, byte[] _password, byte[] _target_name)
  {
    username = _username;
    password = _password;
    target_name = _target_name;
  } // ctor

} // class InitialContextToken

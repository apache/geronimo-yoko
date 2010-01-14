package org.omg.CSI;


/**
* org/omg/CSI/AuthorizationElement.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AuthorizationElement implements org.omg.CORBA.portable.IDLEntity
{
  public int the_type = (int)0;
  public byte the_element[] = null;

  public AuthorizationElement ()
  {
  } // ctor

  public AuthorizationElement (int _the_type, byte[] _the_element)
  {
    the_type = _the_type;
    the_element = _the_element;
  } // ctor

} // class AuthorizationElement

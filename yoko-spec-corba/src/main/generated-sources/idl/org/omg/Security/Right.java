package org.omg.Security;


/**
* org/omg/Security/Right.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class Right implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.Security.ExtensibleFamily rights_family = null;
  public String the_right = null;

  public Right ()
  {
  } // ctor

  public Right (org.omg.Security.ExtensibleFamily _rights_family, String _the_right)
  {
    rights_family = _rights_family;
    the_right = _the_right;
  } // ctor

} // class Right

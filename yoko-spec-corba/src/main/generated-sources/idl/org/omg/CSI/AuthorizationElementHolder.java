package org.omg.CSI;

/**
* org/omg/CSI/AuthorizationElementHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AuthorizationElementHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.AuthorizationElement value = null;

  public AuthorizationElementHolder ()
  {
  }

  public AuthorizationElementHolder (org.omg.CSI.AuthorizationElement initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.AuthorizationElementHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.AuthorizationElementHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.AuthorizationElementHelper.type ();
  }

}

package org.omg.CSI;


/**
* org/omg/CSI/AuthorizationElementContentsHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AuthorizationElementContentsHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public AuthorizationElementContentsHolder ()
  {
  }

  public AuthorizationElementContentsHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.AuthorizationElementContentsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.AuthorizationElementContentsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.AuthorizationElementContentsHelper.type ();
  }

}

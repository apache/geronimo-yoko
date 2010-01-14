package org.omg.Security;

/**
* org/omg/Security/CommunicationDirectionHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// secure invocation policy applies
public final class CommunicationDirectionHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.CommunicationDirection value = null;

  public CommunicationDirectionHolder ()
  {
  }

  public CommunicationDirectionHolder (org.omg.Security.CommunicationDirection initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.CommunicationDirectionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.CommunicationDirectionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.CommunicationDirectionHelper.type ();
  }

}

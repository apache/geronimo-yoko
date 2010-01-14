package org.omg.Security;

/**
* org/omg/Security/QOPHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// for an object reference and used to protect messages
public final class QOPHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.QOP value = null;

  public QOPHolder ()
  {
  }

  public QOPHolder (org.omg.Security.QOP initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.QOPHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.QOPHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.QOPHelper.type ();
  }

}

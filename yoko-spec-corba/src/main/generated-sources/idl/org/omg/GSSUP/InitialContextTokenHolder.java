package org.omg.GSSUP;

/**
* org/omg/GSSUP/InitialContextTokenHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class InitialContextTokenHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.GSSUP.InitialContextToken value = null;

  public InitialContextTokenHolder ()
  {
  }

  public InitialContextTokenHolder (org.omg.GSSUP.InitialContextToken initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.GSSUP.InitialContextTokenHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.GSSUP.InitialContextTokenHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.GSSUP.InitialContextTokenHelper.type ();
  }

}

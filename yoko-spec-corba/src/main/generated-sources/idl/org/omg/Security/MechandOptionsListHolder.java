package org.omg.Security;


/**
* org/omg/Security/MechandOptionsListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class MechandOptionsListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.MechandOptions value[] = null;

  public MechandOptionsListHolder ()
  {
  }

  public MechandOptionsListHolder (org.omg.Security.MechandOptions[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.MechandOptionsListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.MechandOptionsListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.MechandOptionsListHelper.type ();
  }

}

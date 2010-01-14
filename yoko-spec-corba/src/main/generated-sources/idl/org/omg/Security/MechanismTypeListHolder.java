package org.omg.Security;


/**
* org/omg/Security/MechanismTypeListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class MechanismTypeListHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public MechanismTypeListHolder ()
  {
  }

  public MechanismTypeListHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.MechanismTypeListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.MechanismTypeListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.MechanismTypeListHelper.type ();
  }

}

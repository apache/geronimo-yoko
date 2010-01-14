package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/CurrentHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


/* */
public final class CurrentHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.Current value = null;

  public CurrentHolder ()
  {
  }

  public CurrentHolder (org.omg.SecurityLevel2.Current initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.CurrentHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.CurrentHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.CurrentHelper.type ();
  }

}

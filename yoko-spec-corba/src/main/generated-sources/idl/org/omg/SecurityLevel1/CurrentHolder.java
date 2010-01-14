package org.omg.SecurityLevel1;

/**
* org/omg/SecurityLevel1/CurrentHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public final class CurrentHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel1.Current value = null;

  public CurrentHolder ()
  {
  }

  public CurrentHolder (org.omg.SecurityLevel1.Current initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel1.CurrentHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel1.CurrentHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel1.CurrentHelper.type ();
  }

}

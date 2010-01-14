package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/SecurityManagerHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


/* */
public final class SecurityManagerHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.SecurityManager value = null;

  public SecurityManagerHolder ()
  {
  }

  public SecurityManagerHolder (org.omg.SecurityLevel2.SecurityManager initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.SecurityManagerHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.SecurityManagerHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.SecurityManagerHelper.type ();
  }

}

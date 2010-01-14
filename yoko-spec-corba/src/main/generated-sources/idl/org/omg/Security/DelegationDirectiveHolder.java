package org.omg.Security;

/**
* org/omg/Security/DelegationDirectiveHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class DelegationDirectiveHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.DelegationDirective value = null;

  public DelegationDirectiveHolder ()
  {
  }

  public DelegationDirectiveHolder (org.omg.Security.DelegationDirective initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.DelegationDirectiveHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.DelegationDirectiveHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.DelegationDirectiveHelper.type ();
  }

}

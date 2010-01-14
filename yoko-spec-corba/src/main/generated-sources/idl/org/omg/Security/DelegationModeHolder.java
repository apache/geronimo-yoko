package org.omg.Security;

/**
* org/omg/Security/DelegationModeHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Delegation mode which can be administered
public final class DelegationModeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.DelegationMode value = null;

  public DelegationModeHolder ()
  {
  }

  public DelegationModeHolder (org.omg.Security.DelegationMode initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.DelegationModeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.DelegationModeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.DelegationModeHelper.type ();
  }

}

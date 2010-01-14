package org.omg.Security;

/**
* org/omg/Security/DelegationStateHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Delegation related
public final class DelegationStateHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.DelegationState value = null;

  public DelegationStateHolder ()
  {
  }

  public DelegationStateHolder (org.omg.Security.DelegationState initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.DelegationStateHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.DelegationStateHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.DelegationStateHelper.type ();
  }

}

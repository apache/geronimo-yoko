package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/AccessDecisionHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public final class AccessDecisionHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.AccessDecision value = null;

  public AccessDecisionHolder ()
  {
  }

  public AccessDecisionHolder (org.omg.SecurityLevel2.AccessDecision initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.AccessDecisionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.AccessDecisionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.AccessDecisionHelper.type ();
  }

}

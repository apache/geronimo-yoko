package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/MechanismPolicyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


/* */
public final class MechanismPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.MechanismPolicy value = null;

  public MechanismPolicyHolder ()
  {
  }

  public MechanismPolicyHolder (org.omg.SecurityLevel2.MechanismPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.MechanismPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.MechanismPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.MechanismPolicyHelper.type ();
  }

}

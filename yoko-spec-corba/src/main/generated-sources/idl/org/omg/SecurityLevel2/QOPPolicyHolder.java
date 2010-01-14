package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/QOPPolicyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public final class QOPPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.QOPPolicy value = null;

  public QOPPolicyHolder ()
  {
  }

  public QOPPolicyHolder (org.omg.SecurityLevel2.QOPPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.QOPPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.QOPPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.QOPPolicyHelper.type ();
  }

}

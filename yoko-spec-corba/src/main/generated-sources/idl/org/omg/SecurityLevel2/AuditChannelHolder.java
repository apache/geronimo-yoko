package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/AuditChannelHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public final class AuditChannelHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.AuditChannel value = null;

  public AuditChannelHolder ()
  {
  }

  public AuditChannelHolder (org.omg.SecurityLevel2.AuditChannel initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.AuditChannelHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.AuditChannelHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.AuditChannelHelper.type ();
  }

}

package org.omg.Security;


/**
* org/omg/Security/AuditEventTypeListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AuditEventTypeListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.AuditEventType value[] = null;

  public AuditEventTypeListHolder ()
  {
  }

  public AuditEventTypeListHolder (org.omg.Security.AuditEventType[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AuditEventTypeListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AuditEventTypeListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AuditEventTypeListHelper.type ();
  }

}

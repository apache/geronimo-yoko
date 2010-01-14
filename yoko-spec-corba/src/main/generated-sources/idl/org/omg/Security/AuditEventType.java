package org.omg.Security;


/**
* org/omg/Security/AuditEventType.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AuditEventType implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.Security.ExtensibleFamily event_family = null;
  public short event_type = (short)0;

  public AuditEventType ()
  {
  } // ctor

  public AuditEventType (org.omg.Security.ExtensibleFamily _event_family, short _event_type)
  {
    event_family = _event_family;
    event_type = _event_type;
  } // ctor

} // class AuditEventType

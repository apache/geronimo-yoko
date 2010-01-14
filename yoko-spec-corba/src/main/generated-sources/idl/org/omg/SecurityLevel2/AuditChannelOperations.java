package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/AuditChannelOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public interface AuditChannelOperations 
{
  void audit_write (org.omg.Security.AuditEventType event_type, org.omg.SecurityLevel2.Credentials[] creds, org.omg.TimeBase.UtcT time, org.omg.Security.SelectorValue[] descriptors, org.omg.CORBA.Any event_specific_data);
  int audit_channel_id ();
} // interface AuditChannelOperations

package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/AuditDecisionOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public interface AuditDecisionOperations 
{
  boolean audit_needed (org.omg.Security.AuditEventType event_type, org.omg.Security.SelectorValue[] value_list);
  org.omg.SecurityLevel2.AuditChannel audit_channel ();
} // interface AuditDecisionOperations

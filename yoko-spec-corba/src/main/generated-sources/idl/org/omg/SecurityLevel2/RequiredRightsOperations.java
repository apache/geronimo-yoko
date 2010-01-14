package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/RequiredRightsOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// RequiredRights Interface
public interface RequiredRightsOperations 
{
  void get_required_rights (org.omg.CORBA.Object obj, String operation_name, String interface_name, org.omg.Security.RightsListHolder rights, org.omg.Security.RightsCombinatorHolder rights_combinator);
  void set_required_rights (String operation_name, String interface_name, org.omg.Security.Right[] rights, org.omg.Security.RightsCombinator rights_combinator);
} // interface RequiredRightsOperations

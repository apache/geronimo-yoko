package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/ControlOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public interface ControlOperations 
{
  org.omg.CosTransactions.Terminator get_terminator () throws org.omg.CosTransactions.Unavailable;
  org.omg.CosTransactions.Coordinator get_coordinator () throws org.omg.CosTransactions.Unavailable;
} // interface ControlOperations

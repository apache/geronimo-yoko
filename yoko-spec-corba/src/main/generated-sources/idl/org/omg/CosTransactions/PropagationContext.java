package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/PropagationContext.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class PropagationContext implements org.omg.CORBA.portable.IDLEntity
{
  public int timeout = (int)0;
  public org.omg.CosTransactions.TransIdentity current = null;
  public org.omg.CosTransactions.TransIdentity parents[] = null;
  public org.omg.CORBA.Any implementation_specific_data = null;

  public PropagationContext ()
  {
  } // ctor

  public PropagationContext (int _timeout, org.omg.CosTransactions.TransIdentity _current, org.omg.CosTransactions.TransIdentity[] _parents, org.omg.CORBA.Any _implementation_specific_data)
  {
    timeout = _timeout;
    current = _current;
    parents = _parents;
    implementation_specific_data = _implementation_specific_data;
  } // ctor

} // class PropagationContext

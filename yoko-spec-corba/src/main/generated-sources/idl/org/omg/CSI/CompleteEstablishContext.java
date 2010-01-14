package org.omg.CSI;


/**
* org/omg/CSI/CompleteEstablishContext.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class CompleteEstablishContext implements org.omg.CORBA.portable.IDLEntity
{
  public long client_context_id = (long)0;
  public boolean context_stateful = false;
  public byte final_context_token[] = null;

  public CompleteEstablishContext ()
  {
  } // ctor

  public CompleteEstablishContext (long _client_context_id, boolean _context_stateful, byte[] _final_context_token)
  {
    client_context_id = _client_context_id;
    context_stateful = _context_stateful;
    final_context_token = _final_context_token;
  } // ctor

} // class CompleteEstablishContext

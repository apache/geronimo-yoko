package org.omg.CSI;


/**
* org/omg/CSI/MessageInContext.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class MessageInContext implements org.omg.CORBA.portable.IDLEntity
{
  public long client_context_id = (long)0;
  public boolean discard_context = false;

  public MessageInContext ()
  {
  } // ctor

  public MessageInContext (long _client_context_id, boolean _discard_context)
  {
    client_context_id = _client_context_id;
    discard_context = _discard_context;
  } // ctor

} // class MessageInContext

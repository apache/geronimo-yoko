package org.omg.CSI;


/**
* org/omg/CSI/ContextError.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class ContextError implements org.omg.CORBA.portable.IDLEntity
{
  public long client_context_id = (long)0;
  public int major_status = (int)0;
  public int minor_status = (int)0;
  public byte error_token[] = null;

  public ContextError ()
  {
  } // ctor

  public ContextError (long _client_context_id, int _major_status, int _minor_status, byte[] _error_token)
  {
    client_context_id = _client_context_id;
    major_status = _major_status;
    minor_status = _minor_status;
    error_token = _error_token;
  } // ctor

} // class ContextError

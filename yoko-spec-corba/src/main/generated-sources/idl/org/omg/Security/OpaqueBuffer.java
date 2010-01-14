package org.omg.Security;


/**
* org/omg/Security/OpaqueBuffer.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class OpaqueBuffer implements org.omg.CORBA.portable.IDLEntity
{
  public byte buffer[] = null;
  public int startpos = (int)0;
  public int endpos = (int)0;

  public OpaqueBuffer ()
  {
  } // ctor

  public OpaqueBuffer (byte[] _buffer, int _startpos, int _endpos)
  {
    buffer = _buffer;
    startpos = _startpos;
    endpos = _endpos;
  } // ctor

} // class OpaqueBuffer

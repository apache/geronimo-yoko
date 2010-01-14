package org.omg.CSI;


/**
* org/omg/CSI/EstablishContext.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class EstablishContext implements org.omg.CORBA.portable.IDLEntity
{
  public long client_context_id = (long)0;
  public org.omg.CSI.AuthorizationElement authorization_token[] = null;
  public org.omg.CSI.IdentityToken identity_token = null;
  public byte client_authentication_token[] = null;

  public EstablishContext ()
  {
  } // ctor

  public EstablishContext (long _client_context_id, org.omg.CSI.AuthorizationElement[] _authorization_token, org.omg.CSI.IdentityToken _identity_token, byte[] _client_authentication_token)
  {
    client_context_id = _client_context_id;
    authorization_token = _authorization_token;
    identity_token = _identity_token;
    client_authentication_token = _client_authentication_token;
  } // ctor

} // class EstablishContext

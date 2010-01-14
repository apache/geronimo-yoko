package org.omg.Security;


/**
* org/omg/Security/SecAttribute.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SecAttribute implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.Security.AttributeType attribute_type = null;
  public byte defining_authority[] = null;
  public byte value[] = null;

  public SecAttribute ()
  {
  } // ctor

  public SecAttribute (org.omg.Security.AttributeType _attribute_type, byte[] _defining_authority, byte[] _value)
  {
    attribute_type = _attribute_type;
    defining_authority = _defining_authority;
    value = _value;
  } // ctor

} // class SecAttribute

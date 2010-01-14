package org.omg.Security;


/**
* org/omg/Security/AttributeType.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AttributeType implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.Security.ExtensibleFamily attribute_family = null;
  public int attribute_type = (int)0;

  public AttributeType ()
  {
  } // ctor

  public AttributeType (org.omg.Security.ExtensibleFamily _attribute_family, int _attribute_type)
  {
    attribute_family = _attribute_family;
    attribute_type = _attribute_type;
  } // ctor

} // class AttributeType

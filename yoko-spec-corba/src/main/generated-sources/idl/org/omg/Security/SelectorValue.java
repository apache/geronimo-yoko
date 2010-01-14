package org.omg.Security;


/**
* org/omg/Security/SelectorValue.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SelectorValue implements org.omg.CORBA.portable.IDLEntity
{
  public int selector = (int)0;
  public org.omg.CORBA.Any value = null;

  public SelectorValue ()
  {
  } // ctor

  public SelectorValue (int _selector, org.omg.CORBA.Any _value)
  {
    selector = _selector;
    value = _value;
  } // ctor

} // class SelectorValue

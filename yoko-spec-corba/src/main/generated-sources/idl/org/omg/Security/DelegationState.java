package org.omg.Security;


/**
* org/omg/Security/DelegationState.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Delegation related
public class DelegationState implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.Security.DelegationState[] __array = new org.omg.Security.DelegationState [__size];

  public static final int _SecInitiator = 0;
  public static final org.omg.Security.DelegationState SecInitiator = new org.omg.Security.DelegationState(_SecInitiator);
  public static final int _SecDelegate = 1;
  public static final org.omg.Security.DelegationState SecDelegate = new org.omg.Security.DelegationState(_SecDelegate);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.DelegationState from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected DelegationState (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class DelegationState

package org.omg.Security;


/**
* org/omg/Security/DelegationMode.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Delegation mode which can be administered
public class DelegationMode implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 3;
  private static org.omg.Security.DelegationMode[] __array = new org.omg.Security.DelegationMode [__size];

  public static final int _SecDelModeNoDelegation = 0;
  public static final org.omg.Security.DelegationMode SecDelModeNoDelegation = new org.omg.Security.DelegationMode(_SecDelModeNoDelegation);
  public static final int _SecDelModeSimpleDelegation = 1;
  public static final org.omg.Security.DelegationMode SecDelModeSimpleDelegation = new org.omg.Security.DelegationMode(_SecDelModeSimpleDelegation);
  public static final int _SecDelModeCompositeDelegation = 2;
  public static final org.omg.Security.DelegationMode SecDelModeCompositeDelegation = new org.omg.Security.DelegationMode(_SecDelModeCompositeDelegation);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.DelegationMode from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected DelegationMode (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class DelegationMode

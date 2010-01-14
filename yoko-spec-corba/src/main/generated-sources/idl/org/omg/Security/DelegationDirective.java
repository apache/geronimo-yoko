package org.omg.Security;


/**
* org/omg/Security/DelegationDirective.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public class DelegationDirective implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.Security.DelegationDirective[] __array = new org.omg.Security.DelegationDirective [__size];

  public static final int _Delegate = 0;
  public static final org.omg.Security.DelegationDirective Delegate = new org.omg.Security.DelegationDirective(_Delegate);
  public static final int _NoDelegate = 1;
  public static final org.omg.Security.DelegationDirective NoDelegate = new org.omg.Security.DelegationDirective(_NoDelegate);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.DelegationDirective from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected DelegationDirective (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class DelegationDirective

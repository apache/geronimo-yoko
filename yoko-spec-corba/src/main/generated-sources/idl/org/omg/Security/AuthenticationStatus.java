package org.omg.Security;


/**
* org/omg/Security/AuthenticationStatus.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Authentication return status
public class AuthenticationStatus implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 4;
  private static org.omg.Security.AuthenticationStatus[] __array = new org.omg.Security.AuthenticationStatus [__size];

  public static final int _SecAuthSuccess = 0;
  public static final org.omg.Security.AuthenticationStatus SecAuthSuccess = new org.omg.Security.AuthenticationStatus(_SecAuthSuccess);
  public static final int _SecAuthFailure = 1;
  public static final org.omg.Security.AuthenticationStatus SecAuthFailure = new org.omg.Security.AuthenticationStatus(_SecAuthFailure);
  public static final int _SecAuthContinue = 2;
  public static final org.omg.Security.AuthenticationStatus SecAuthContinue = new org.omg.Security.AuthenticationStatus(_SecAuthContinue);
  public static final int _SecAuthExpired = 3;
  public static final org.omg.Security.AuthenticationStatus SecAuthExpired = new org.omg.Security.AuthenticationStatus(_SecAuthExpired);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.AuthenticationStatus from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected AuthenticationStatus (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class AuthenticationStatus

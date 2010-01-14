package org.omg.Security;


/**
* org/omg/Security/InvocationCredentialsType.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Credential types
public class InvocationCredentialsType implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 3;
  private static org.omg.Security.InvocationCredentialsType[] __array = new org.omg.Security.InvocationCredentialsType [__size];

  public static final int _SecOwnCredentials = 0;
  public static final org.omg.Security.InvocationCredentialsType SecOwnCredentials = new org.omg.Security.InvocationCredentialsType(_SecOwnCredentials);
  public static final int _SecReceivedCredentials = 1;
  public static final org.omg.Security.InvocationCredentialsType SecReceivedCredentials = new org.omg.Security.InvocationCredentialsType(_SecReceivedCredentials);
  public static final int _SecTargetCredentials = 2;
  public static final org.omg.Security.InvocationCredentialsType SecTargetCredentials = new org.omg.Security.InvocationCredentialsType(_SecTargetCredentials);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.InvocationCredentialsType from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected InvocationCredentialsType (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class InvocationCredentialsType

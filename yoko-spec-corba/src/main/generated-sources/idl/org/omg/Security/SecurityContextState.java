package org.omg.Security;


/**
* org/omg/Security/SecurityContextState.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Operational State of a Security Context
public class SecurityContextState implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 7;
  private static org.omg.Security.SecurityContextState[] __array = new org.omg.Security.SecurityContextState [__size];

  public static final int _SecContextInitialized = 0;
  public static final org.omg.Security.SecurityContextState SecContextInitialized = new org.omg.Security.SecurityContextState(_SecContextInitialized);
  public static final int _SecContextContinued = 1;
  public static final org.omg.Security.SecurityContextState SecContextContinued = new org.omg.Security.SecurityContextState(_SecContextContinued);
  public static final int _SecContextClientEstablished = 2;
  public static final org.omg.Security.SecurityContextState SecContextClientEstablished = new org.omg.Security.SecurityContextState(_SecContextClientEstablished);
  public static final int _SecContextEstablished = 3;
  public static final org.omg.Security.SecurityContextState SecContextEstablished = new org.omg.Security.SecurityContextState(_SecContextEstablished);
  public static final int _SecContextEstablishExpired = 4;
  public static final org.omg.Security.SecurityContextState SecContextEstablishExpired = new org.omg.Security.SecurityContextState(_SecContextEstablishExpired);
  public static final int _SecContextExpired = 5;
  public static final org.omg.Security.SecurityContextState SecContextExpired = new org.omg.Security.SecurityContextState(_SecContextExpired);
  public static final int _SecContextInvalid = 6;
  public static final org.omg.Security.SecurityContextState SecContextInvalid = new org.omg.Security.SecurityContextState(_SecContextInvalid);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.SecurityContextState from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected SecurityContextState (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class SecurityContextState

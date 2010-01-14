package org.omg.Security;


/**
* org/omg/Security/RightsCombinator.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public class RightsCombinator implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.Security.RightsCombinator[] __array = new org.omg.Security.RightsCombinator [__size];

  public static final int _SecAllRights = 0;
  public static final org.omg.Security.RightsCombinator SecAllRights = new org.omg.Security.RightsCombinator(_SecAllRights);
  public static final int _SecAnyRight = 1;
  public static final org.omg.Security.RightsCombinator SecAnyRight = new org.omg.Security.RightsCombinator(_SecAnyRight);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.RightsCombinator from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected RightsCombinator (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class RightsCombinator

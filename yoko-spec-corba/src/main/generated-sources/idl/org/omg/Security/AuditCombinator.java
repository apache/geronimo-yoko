package org.omg.Security;


/**
* org/omg/Security/AuditCombinator.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public class AuditCombinator implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.Security.AuditCombinator[] __array = new org.omg.Security.AuditCombinator [__size];

  public static final int _SecAllSelectors = 0;
  public static final org.omg.Security.AuditCombinator SecAllSelectors = new org.omg.Security.AuditCombinator(_SecAllSelectors);
  public static final int _SecAnySelector = 1;
  public static final org.omg.Security.AuditCombinator SecAnySelector = new org.omg.Security.AuditCombinator(_SecAnySelector);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.AuditCombinator from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected AuditCombinator (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class AuditCombinator

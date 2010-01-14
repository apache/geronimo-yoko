package org.omg.Security;


/**
* org/omg/Security/DayOfTheWeek.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public class DayOfTheWeek implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 7;
  private static org.omg.Security.DayOfTheWeek[] __array = new org.omg.Security.DayOfTheWeek [__size];

  public static final int _Monday = 0;
  public static final org.omg.Security.DayOfTheWeek Monday = new org.omg.Security.DayOfTheWeek(_Monday);
  public static final int _Tuesday = 1;
  public static final org.omg.Security.DayOfTheWeek Tuesday = new org.omg.Security.DayOfTheWeek(_Tuesday);
  public static final int _Wednesday = 2;
  public static final org.omg.Security.DayOfTheWeek Wednesday = new org.omg.Security.DayOfTheWeek(_Wednesday);
  public static final int _Thursday = 3;
  public static final org.omg.Security.DayOfTheWeek Thursday = new org.omg.Security.DayOfTheWeek(_Thursday);
  public static final int _Friday = 4;
  public static final org.omg.Security.DayOfTheWeek Friday = new org.omg.Security.DayOfTheWeek(_Friday);
  public static final int _Saturday = 5;
  public static final org.omg.Security.DayOfTheWeek Saturday = new org.omg.Security.DayOfTheWeek(_Saturday);
  public static final int _Sunday = 6;
  public static final org.omg.Security.DayOfTheWeek Sunday = new org.omg.Security.DayOfTheWeek(_Sunday);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.DayOfTheWeek from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected DayOfTheWeek (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class DayOfTheWeek

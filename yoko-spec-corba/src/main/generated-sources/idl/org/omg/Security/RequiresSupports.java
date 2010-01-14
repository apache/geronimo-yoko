package org.omg.Security;


/**
* org/omg/Security/RequiresSupports.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// administered are the "required" or "supported" set
public class RequiresSupports implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.Security.RequiresSupports[] __array = new org.omg.Security.RequiresSupports [__size];

  public static final int _SecRequires = 0;
  public static final org.omg.Security.RequiresSupports SecRequires = new org.omg.Security.RequiresSupports(_SecRequires);
  public static final int _SecSupports = 1;
  public static final org.omg.Security.RequiresSupports SecSupports = new org.omg.Security.RequiresSupports(_SecSupports);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.RequiresSupports from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected RequiresSupports (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class RequiresSupports

package org.omg.Security;


/**
* org/omg/Security/CommunicationDirection.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// secure invocation policy applies
public class CommunicationDirection implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 3;
  private static org.omg.Security.CommunicationDirection[] __array = new org.omg.Security.CommunicationDirection [__size];

  public static final int _SecDirectionBoth = 0;
  public static final org.omg.Security.CommunicationDirection SecDirectionBoth = new org.omg.Security.CommunicationDirection(_SecDirectionBoth);
  public static final int _SecDirectionRequest = 1;
  public static final org.omg.Security.CommunicationDirection SecDirectionRequest = new org.omg.Security.CommunicationDirection(_SecDirectionRequest);
  public static final int _SecDirectionReply = 2;
  public static final org.omg.Security.CommunicationDirection SecDirectionReply = new org.omg.Security.CommunicationDirection(_SecDirectionReply);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.CommunicationDirection from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected CommunicationDirection (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class CommunicationDirection

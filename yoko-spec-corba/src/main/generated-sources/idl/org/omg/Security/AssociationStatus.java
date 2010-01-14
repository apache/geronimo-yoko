package org.omg.Security;


/**
* org/omg/Security/AssociationStatus.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Association return status
public class AssociationStatus implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 3;
  private static org.omg.Security.AssociationStatus[] __array = new org.omg.Security.AssociationStatus [__size];

  public static final int _SecAssocSuccess = 0;
  public static final org.omg.Security.AssociationStatus SecAssocSuccess = new org.omg.Security.AssociationStatus(_SecAssocSuccess);
  public static final int _SecAssocFailure = 1;
  public static final org.omg.Security.AssociationStatus SecAssocFailure = new org.omg.Security.AssociationStatus(_SecAssocFailure);
  public static final int _SecAssocContinue = 2;
  public static final org.omg.Security.AssociationStatus SecAssocContinue = new org.omg.Security.AssociationStatus(_SecAssocContinue);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.AssociationStatus from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected AssociationStatus (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class AssociationStatus

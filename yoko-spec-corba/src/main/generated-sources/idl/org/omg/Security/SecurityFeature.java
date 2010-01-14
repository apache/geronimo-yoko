package org.omg.Security;


/**
* org/omg/Security/SecurityFeature.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Security features available on credentials.
public class SecurityFeature implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 11;
  private static org.omg.Security.SecurityFeature[] __array = new org.omg.Security.SecurityFeature [__size];

  public static final int _SecNoDelegation = 0;
  public static final org.omg.Security.SecurityFeature SecNoDelegation = new org.omg.Security.SecurityFeature(_SecNoDelegation);
  public static final int _SecSimpleDelegation = 1;
  public static final org.omg.Security.SecurityFeature SecSimpleDelegation = new org.omg.Security.SecurityFeature(_SecSimpleDelegation);
  public static final int _SecCompositeDelegation = 2;
  public static final org.omg.Security.SecurityFeature SecCompositeDelegation = new org.omg.Security.SecurityFeature(_SecCompositeDelegation);
  public static final int _SecNoProtection = 3;
  public static final org.omg.Security.SecurityFeature SecNoProtection = new org.omg.Security.SecurityFeature(_SecNoProtection);
  public static final int _SecIntegrity = 4;
  public static final org.omg.Security.SecurityFeature SecIntegrity = new org.omg.Security.SecurityFeature(_SecIntegrity);
  public static final int _SecConfidentiality = 5;
  public static final org.omg.Security.SecurityFeature SecConfidentiality = new org.omg.Security.SecurityFeature(_SecConfidentiality);
  public static final int _SecIntegrityAndConfidentiality = 6;
  public static final org.omg.Security.SecurityFeature SecIntegrityAndConfidentiality = new org.omg.Security.SecurityFeature(_SecIntegrityAndConfidentiality);
  public static final int _SecDetectReplay = 7;
  public static final org.omg.Security.SecurityFeature SecDetectReplay = new org.omg.Security.SecurityFeature(_SecDetectReplay);
  public static final int _SecDetectMisordering = 8;
  public static final org.omg.Security.SecurityFeature SecDetectMisordering = new org.omg.Security.SecurityFeature(_SecDetectMisordering);
  public static final int _SecEstablishTrustInTarget = 9;
  public static final org.omg.Security.SecurityFeature SecEstablishTrustInTarget = new org.omg.Security.SecurityFeature(_SecEstablishTrustInTarget);
  public static final int _SecEstablishTrustInClient = 10;
  public static final org.omg.Security.SecurityFeature SecEstablishTrustInClient = new org.omg.Security.SecurityFeature(_SecEstablishTrustInClient);

  public int value ()
  {
    return __value;
  }

  public static org.omg.Security.SecurityFeature from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected SecurityFeature (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class SecurityFeature

package org.omg.CSI;


/**
* org/omg/CSI/IdentityToken.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class IdentityToken implements org.omg.CORBA.portable.IDLEntity
{
  private boolean ___absent;
  private boolean ___anonymous;
  private byte[] ___principal_name;
  private byte[] ___certificate_chain;
  private byte[] ___dn;
  private byte[] ___id;
  private int __discriminator;
  private boolean __uninitialized = true;

  public IdentityToken ()
  {
  }

  public int discriminator ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    return __discriminator;
  }

  public boolean absent ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifyabsent (__discriminator);
    return ___absent;
  }

  public void absent (boolean value)
  {
    __discriminator = org.omg.CSI.ITTAbsent.value;
    ___absent = value;
    __uninitialized = false;
  }

  public void absent (int discriminator, boolean value)
  {
    verifyabsent (discriminator);
    __discriminator = discriminator;
    ___absent = value;
    __uninitialized = false;
  }

  private void verifyabsent (int discriminator)
  {
    if (discriminator != org.omg.CSI.ITTAbsent.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public boolean anonymous ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifyanonymous (__discriminator);
    return ___anonymous;
  }

  public void anonymous (boolean value)
  {
    __discriminator = org.omg.CSI.ITTAnonymous.value;
    ___anonymous = value;
    __uninitialized = false;
  }

  public void anonymous (int discriminator, boolean value)
  {
    verifyanonymous (discriminator);
    __discriminator = discriminator;
    ___anonymous = value;
    __uninitialized = false;
  }

  private void verifyanonymous (int discriminator)
  {
    if (discriminator != org.omg.CSI.ITTAnonymous.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public byte[] principal_name ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifyprincipal_name (__discriminator);
    return ___principal_name;
  }

  public void principal_name (byte[] value)
  {
    __discriminator = org.omg.CSI.ITTPrincipalName.value;
    ___principal_name = value;
    __uninitialized = false;
  }

  public void principal_name (int discriminator, byte[] value)
  {
    verifyprincipal_name (discriminator);
    __discriminator = discriminator;
    ___principal_name = value;
    __uninitialized = false;
  }

  private void verifyprincipal_name (int discriminator)
  {
    if (discriminator != org.omg.CSI.ITTPrincipalName.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public byte[] certificate_chain ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifycertificate_chain (__discriminator);
    return ___certificate_chain;
  }

  public void certificate_chain (byte[] value)
  {
    __discriminator = org.omg.CSI.ITTX509CertChain.value;
    ___certificate_chain = value;
    __uninitialized = false;
  }

  public void certificate_chain (int discriminator, byte[] value)
  {
    verifycertificate_chain (discriminator);
    __discriminator = discriminator;
    ___certificate_chain = value;
    __uninitialized = false;
  }

  private void verifycertificate_chain (int discriminator)
  {
    if (discriminator != org.omg.CSI.ITTX509CertChain.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public byte[] dn ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifydn (__discriminator);
    return ___dn;
  }

  public void dn (byte[] value)
  {
    __discriminator = org.omg.CSI.ITTDistinguishedName.value;
    ___dn = value;
    __uninitialized = false;
  }

  public void dn (int discriminator, byte[] value)
  {
    verifydn (discriminator);
    __discriminator = discriminator;
    ___dn = value;
    __uninitialized = false;
  }

  private void verifydn (int discriminator)
  {
    if (discriminator != org.omg.CSI.ITTDistinguishedName.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public byte[] id ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifyid (__discriminator);
    return ___id;
  }

  public void id (byte[] value)
  {
    __discriminator = 0;
    ___id = value;
    __uninitialized = false;
  }

  public void id (int discriminator, byte[] value)
  {
    verifyid (discriminator);
    __discriminator = discriminator;
    ___id = value;
    __uninitialized = false;
  }

  private void verifyid (int discriminator)
  {
    if (discriminator == org.omg.CSI.ITTAbsent.value || discriminator == org.omg.CSI.ITTAnonymous.value || discriminator == org.omg.CSI.ITTPrincipalName.value || discriminator == org.omg.CSI.ITTX509CertChain.value || discriminator == org.omg.CSI.ITTDistinguishedName.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

} // class IdentityToken

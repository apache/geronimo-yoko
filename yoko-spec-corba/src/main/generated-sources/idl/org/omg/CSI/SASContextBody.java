package org.omg.CSI;


/**
* org/omg/CSI/SASContextBody.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SASContextBody implements org.omg.CORBA.portable.IDLEntity
{
  private org.omg.CSI.EstablishContext ___establish_msg;
  private org.omg.CSI.CompleteEstablishContext ___complete_msg;
  private org.omg.CSI.ContextError ___error_msg;
  private org.omg.CSI.MessageInContext ___in_context_msg;
  private short __discriminator;
  private boolean __uninitialized = true;

  public SASContextBody ()
  {
  }

  public short discriminator ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    return __discriminator;
  }

  public org.omg.CSI.EstablishContext establish_msg ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifyestablish_msg (__discriminator);
    return ___establish_msg;
  }

  public void establish_msg (org.omg.CSI.EstablishContext value)
  {
    __discriminator = org.omg.CSI.MTEstablishContext.value;
    ___establish_msg = value;
    __uninitialized = false;
  }

  public void establish_msg (short discriminator, org.omg.CSI.EstablishContext value)
  {
    verifyestablish_msg (discriminator);
    __discriminator = discriminator;
    ___establish_msg = value;
    __uninitialized = false;
  }

  private void verifyestablish_msg (short discriminator)
  {
    if (discriminator != org.omg.CSI.MTEstablishContext.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public org.omg.CSI.CompleteEstablishContext complete_msg ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifycomplete_msg (__discriminator);
    return ___complete_msg;
  }

  public void complete_msg (org.omg.CSI.CompleteEstablishContext value)
  {
    __discriminator = org.omg.CSI.MTCompleteEstablishContext.value;
    ___complete_msg = value;
    __uninitialized = false;
  }

  public void complete_msg (short discriminator, org.omg.CSI.CompleteEstablishContext value)
  {
    verifycomplete_msg (discriminator);
    __discriminator = discriminator;
    ___complete_msg = value;
    __uninitialized = false;
  }

  private void verifycomplete_msg (short discriminator)
  {
    if (discriminator != org.omg.CSI.MTCompleteEstablishContext.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public org.omg.CSI.ContextError error_msg ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifyerror_msg (__discriminator);
    return ___error_msg;
  }

  public void error_msg (org.omg.CSI.ContextError value)
  {
    __discriminator = org.omg.CSI.MTContextError.value;
    ___error_msg = value;
    __uninitialized = false;
  }

  public void error_msg (short discriminator, org.omg.CSI.ContextError value)
  {
    verifyerror_msg (discriminator);
    __discriminator = discriminator;
    ___error_msg = value;
    __uninitialized = false;
  }

  private void verifyerror_msg (short discriminator)
  {
    if (discriminator != org.omg.CSI.MTContextError.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public org.omg.CSI.MessageInContext in_context_msg ()
  {
    if (__uninitialized)
      throw new org.omg.CORBA.BAD_OPERATION ();
    verifyin_context_msg (__discriminator);
    return ___in_context_msg;
  }

  public void in_context_msg (org.omg.CSI.MessageInContext value)
  {
    __discriminator = org.omg.CSI.MTMessageInContext.value;
    ___in_context_msg = value;
    __uninitialized = false;
  }

  public void in_context_msg (short discriminator, org.omg.CSI.MessageInContext value)
  {
    verifyin_context_msg (discriminator);
    __discriminator = discriminator;
    ___in_context_msg = value;
    __uninitialized = false;
  }

  private void verifyin_context_msg (short discriminator)
  {
    if (discriminator != org.omg.CSI.MTMessageInContext.value)
      throw new org.omg.CORBA.BAD_OPERATION ();
  }

  public void _default ()
  {
    __discriminator = -32768;
    __uninitialized = false;
  }

  public void _default (short discriminator)
  {
    verifyDefault( discriminator ) ;
    __discriminator = discriminator ;
    __uninitialized = false;
  }

  private void verifyDefault( short value )
  {
    switch (value) {
      case org.omg.CSI.MTEstablishContext.value:
      case org.omg.CSI.MTCompleteEstablishContext.value:
      case org.omg.CSI.MTContextError.value:
      case org.omg.CSI.MTMessageInContext.value:
        throw new org.omg.CORBA.BAD_OPERATION() ;

      default:
        return;
    }
  }

} // class SASContextBody

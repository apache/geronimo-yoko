package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/SAS_ContextSec.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class SAS_ContextSec implements org.omg.CORBA.portable.IDLEntity
{
  public short target_supports = (short)0;
  public short target_requires = (short)0;
  public org.omg.CSIIOP.ServiceConfiguration privilege_authorities[] = null;
  public byte supported_naming_mechanisms[][] = null;
  public int supported_identity_types = (int)0;

  public SAS_ContextSec ()
  {
  } // ctor

  public SAS_ContextSec (short _target_supports, short _target_requires, org.omg.CSIIOP.ServiceConfiguration[] _privilege_authorities, byte[][] _supported_naming_mechanisms, int _supported_identity_types)
  {
    target_supports = _target_supports;
    target_requires = _target_requires;
    privilege_authorities = _privilege_authorities;
    supported_naming_mechanisms = _supported_naming_mechanisms;
    supported_identity_types = _supported_identity_types;
  } // ctor

} // class SAS_ContextSec

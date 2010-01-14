package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/CompoundSecMech.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class CompoundSecMech implements org.omg.CORBA.portable.IDLEntity
{
  public short target_requires = (short)0;
  public org.omg.IOP.TaggedComponent transport_mech = null;
  public org.omg.CSIIOP.AS_ContextSec as_context_mech = null;
  public org.omg.CSIIOP.SAS_ContextSec sas_context_mech = null;

  public CompoundSecMech ()
  {
  } // ctor

  public CompoundSecMech (short _target_requires, org.omg.IOP.TaggedComponent _transport_mech, org.omg.CSIIOP.AS_ContextSec _as_context_mech, org.omg.CSIIOP.SAS_ContextSec _sas_context_mech)
  {
    target_requires = _target_requires;
    transport_mech = _transport_mech;
    as_context_mech = _as_context_mech;
    sas_context_mech = _sas_context_mech;
  } // ctor

} // class CompoundSecMech

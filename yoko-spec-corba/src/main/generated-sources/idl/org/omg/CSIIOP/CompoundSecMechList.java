package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/CompoundSecMechList.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class CompoundSecMechList implements org.omg.CORBA.portable.IDLEntity
{
  public boolean stateful = false;
  public org.omg.CSIIOP.CompoundSecMech mechanism_list[] = null;

  public CompoundSecMechList ()
  {
  } // ctor

  public CompoundSecMechList (boolean _stateful, org.omg.CSIIOP.CompoundSecMech[] _mechanism_list)
  {
    stateful = _stateful;
    mechanism_list = _mechanism_list;
  } // ctor

} // class CompoundSecMechList

package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/CompoundSecMechanismsHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class CompoundSecMechanismsHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.CompoundSecMech value[] = null;

  public CompoundSecMechanismsHolder ()
  {
  }

  public CompoundSecMechanismsHolder (org.omg.CSIIOP.CompoundSecMech[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.CompoundSecMechanismsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.CompoundSecMechanismsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.CompoundSecMechanismsHelper.type ();
  }

}

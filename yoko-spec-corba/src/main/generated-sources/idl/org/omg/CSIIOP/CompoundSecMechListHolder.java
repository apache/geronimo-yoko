package org.omg.CSIIOP;

/**
* org/omg/CSIIOP/CompoundSecMechListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class CompoundSecMechListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.CompoundSecMechList value = null;

  public CompoundSecMechListHolder ()
  {
  }

  public CompoundSecMechListHolder (org.omg.CSIIOP.CompoundSecMechList initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.CompoundSecMechListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.CompoundSecMechListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.CompoundSecMechListHelper.type ();
  }

}

package org.omg.Security;

/**
* org/omg/Security/AssociationStatusHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Association return status
public final class AssociationStatusHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.AssociationStatus value = null;

  public AssociationStatusHolder ()
  {
  }

  public AssociationStatusHolder (org.omg.Security.AssociationStatus initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AssociationStatusHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AssociationStatusHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AssociationStatusHelper.type ();
  }

}

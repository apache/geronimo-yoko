package org.omg.Security;

/**
* org/omg/Security/AuditCombinatorHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class AuditCombinatorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.AuditCombinator value = null;

  public AuditCombinatorHolder ()
  {
  }

  public AuditCombinatorHolder (org.omg.Security.AuditCombinator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.AuditCombinatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.AuditCombinatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.AuditCombinatorHelper.type ();
  }

}

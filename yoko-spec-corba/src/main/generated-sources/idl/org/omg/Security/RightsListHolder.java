package org.omg.Security;


/**
* org/omg/Security/RightsListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class RightsListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.Right value[] = null;

  public RightsListHolder ()
  {
  }

  public RightsListHolder (org.omg.Security.Right[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.RightsListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.RightsListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.RightsListHelper.type ();
  }

}

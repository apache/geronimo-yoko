package org.omg.Security;


/**
* org/omg/Security/SelectorValueListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class SelectorValueListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.SelectorValue value[] = null;

  public SelectorValueListHolder ()
  {
  }

  public SelectorValueListHolder (org.omg.Security.SelectorValue[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.SelectorValueListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.SelectorValueListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.SelectorValueListHelper.type ();
  }

}

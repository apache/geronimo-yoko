package org.omg.Security;


/**
* org/omg/Security/OptionsDirectionPairListHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class OptionsDirectionPairListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.OptionsDirectionPair value[] = null;

  public OptionsDirectionPairListHolder ()
  {
  }

  public OptionsDirectionPairListHolder (org.omg.Security.OptionsDirectionPair[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.OptionsDirectionPairListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.OptionsDirectionPairListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.OptionsDirectionPairListHelper.type ();
  }

}

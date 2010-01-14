package org.omg.Security;

/**
* org/omg/Security/OptionsDirectionPairHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class OptionsDirectionPairHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.OptionsDirectionPair value = null;

  public OptionsDirectionPairHolder ()
  {
  }

  public OptionsDirectionPairHolder (org.omg.Security.OptionsDirectionPair initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.OptionsDirectionPairHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.OptionsDirectionPairHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.OptionsDirectionPairHelper.type ();
  }

}

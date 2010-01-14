package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/OTSPolicyHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/

public final class OTSPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.OTSPolicy value = null;

  public OTSPolicyHolder ()
  {
  }

  public OTSPolicyHolder (org.omg.CosTransactions.OTSPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.OTSPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.OTSPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.OTSPolicyHelper.type ();
  }

}

package org.omg.CSI;

/**
* org/omg/CSI/CompleteEstablishContextHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class CompleteEstablishContextHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.CompleteEstablishContext value = null;

  public CompleteEstablishContextHolder ()
  {
  }

  public CompleteEstablishContextHolder (org.omg.CSI.CompleteEstablishContext initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.CompleteEstablishContextHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.CompleteEstablishContextHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.CompleteEstablishContextHelper.type ();
  }

}

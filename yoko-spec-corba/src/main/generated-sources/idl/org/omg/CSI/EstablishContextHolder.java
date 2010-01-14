package org.omg.CSI;

/**
* org/omg/CSI/EstablishContextHolder.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class EstablishContextHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.EstablishContext value = null;

  public EstablishContextHolder ()
  {
  }

  public EstablishContextHolder (org.omg.CSI.EstablishContext initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.EstablishContextHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.EstablishContextHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.EstablishContextHelper.type ();
  }

}

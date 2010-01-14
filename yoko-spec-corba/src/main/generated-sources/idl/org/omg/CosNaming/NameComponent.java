package org.omg.CosNaming;


/**
* org/omg/CosNaming/NameComponent.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/

public final class NameComponent implements org.omg.CORBA.portable.IDLEntity
{
  public String id = null;
  public String kind = null;

  public NameComponent ()
  {
  } // ctor

  public NameComponent (String _id, String _kind)
  {
    id = _id;
    kind = _kind;
  } // ctor

} // class NameComponent

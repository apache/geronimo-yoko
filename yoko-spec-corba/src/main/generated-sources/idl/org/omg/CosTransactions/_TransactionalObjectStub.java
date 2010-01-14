package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/_TransactionalObjectStub.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


// TransactionalObject has been deprecated. See 10.3.10.
public class _TransactionalObjectStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.CosTransactions.TransactionalObject
{

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CosTransactions/TransactionalObject:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _TransactionalObjectStub

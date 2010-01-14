package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/CurrentPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:59 AM PST
*/


/* */
public abstract class CurrentPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.CurrentOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("_get_received_credentials", new java.lang.Integer (0));
    _methods.put ("get_attributes", new java.lang.Integer (1));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {

  // Thread specific
       case 0:  // SecurityLevel2/Current/_get_received_credentials
       {
         org.omg.SecurityLevel2.ReceivedCredentials $result = null;
         $result = this.received_credentials ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.ReceivedCredentialsHelper.write (out, $result);
         break;
       }


  // thread specific operations
       case 1:  // SecurityLevel1/Current/get_attributes
       {
         org.omg.Security.AttributeType ttributes[] = org.omg.Security.AttributeTypeListHelper.read (in);
         org.omg.Security.SecAttribute $result[] = null;
         $result = this.get_attributes (ttributes);
         out = $rh.createReply();
         org.omg.Security.AttributeListHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/Current:1.0", 
    "IDL:omg.org/SecurityLevel1/Current:1.0", 
    "IDL:CORBA/Current:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Current _this() 
  {
    return CurrentHelper.narrow(
    super._this_object());
  }

  public Current _this(org.omg.CORBA.ORB orb) 
  {
    return CurrentHelper.narrow(
    super._this_object(orb));
  }


} // class CurrentPOA

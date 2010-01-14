package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/RequiredRightsPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// RequiredRights Interface
public abstract class RequiredRightsPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.RequiredRightsOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("get_required_rights", new java.lang.Integer (0));
    _methods.put ("set_required_rights", new java.lang.Integer (1));
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
       case 0:  // SecurityLevel2/RequiredRights/get_required_rights
       {
         org.omg.CORBA.Object obj = org.omg.CORBA.ObjectHelper.read (in);
         String operation_name = org.omg.CORBA.IdentifierHelper.read (in);
         String interface_name = org.omg.CORBA.RepositoryIdHelper.read (in);
         org.omg.Security.RightsListHolder rights = new org.omg.Security.RightsListHolder ();
         org.omg.Security.RightsCombinatorHolder rights_combinator = new org.omg.Security.RightsCombinatorHolder ();
         this.get_required_rights (obj, operation_name, interface_name, rights, rights_combinator);
         out = $rh.createReply();
         org.omg.Security.RightsListHelper.write (out, rights.value);
         org.omg.Security.RightsCombinatorHelper.write (out, rights_combinator.value);
         break;
       }

       case 1:  // SecurityLevel2/RequiredRights/set_required_rights
       {
         String operation_name = org.omg.CORBA.IdentifierHelper.read (in);
         String interface_name = org.omg.CORBA.RepositoryIdHelper.read (in);
         org.omg.Security.Right rights[] = org.omg.Security.RightsListHelper.read (in);
         org.omg.Security.RightsCombinator rights_combinator = org.omg.Security.RightsCombinatorHelper.read (in);
         this.set_required_rights (operation_name, interface_name, rights, rights_combinator);
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/RequiredRights:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public RequiredRights _this() 
  {
    return RequiredRightsHelper.narrow(
    super._this_object());
  }

  public RequiredRights _this(org.omg.CORBA.ORB orb) 
  {
    return RequiredRightsHelper.narrow(
    super._this_object(orb));
  }


} // class RequiredRightsPOA

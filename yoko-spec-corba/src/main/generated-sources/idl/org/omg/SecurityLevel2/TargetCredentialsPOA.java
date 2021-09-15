package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/TargetCredentialsPOA.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/* */
public abstract class TargetCredentialsPOA extends org.omg.PortableServer.Servant
 implements org.omg.SecurityLevel2.TargetCredentialsOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("_get_initiating_credentials", 0);
    _methods.put ("_get_association_options_used", 1);
    _methods.put ("copy", 2);
    _methods.put ("destroy", 3);
    _methods.put ("_get_credentials_type", 4);
    _methods.put ("_get_authentication_state", 5);
    _methods.put ("_get_mechanism", 6);
    _methods.put ("_get_accepting_options_supported", 7);
    _methods.put ("_set_accepting_options_supported", 8);
    _methods.put ("_get_accepting_options_required", 9);
    _methods.put ("_set_accepting_options_required", 10);
    _methods.put ("_get_invocation_options_supported", 11);
    _methods.put ("_set_invocation_options_supported", 12);
    _methods.put ("_get_invocation_options_required", 13);
    _methods.put ("_set_invocation_options_required", 14);
    _methods.put ("get_security_feature", 15);
    _methods.put ("set_attributes", 16);
    _methods.put ("get_attributes", 17);
    _methods.put ("is_valid", 18);
    _methods.put ("refresh", 19);
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
       case 0:  // SecurityLevel2/TargetCredentials/_get_initiating_credentials
       {
         org.omg.SecurityLevel2.Credentials $result = null;
         $result = this.initiating_credentials ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.CredentialsHelper.write (out, $result);
         break;
       }

       case 1:  // SecurityLevel2/TargetCredentials/_get_association_options_used
       {
         short $result = (short)0;
         $result = this.association_options_used ();
         out = $rh.createReply();
         out.write_ushort ($result);
         break;
       }

       case 2:  // SecurityLevel2/Credentials/copy
       {
         org.omg.SecurityLevel2.Credentials $result = null;
         $result = this.copy ();
         out = $rh.createReply();
         org.omg.SecurityLevel2.CredentialsHelper.write (out, $result);
         break;
       }

       case 3:  // SecurityLevel2/Credentials/destroy
       {
         this.destroy ();
         out = $rh.createReply();
         break;
       }

       case 4:  // SecurityLevel2/Credentials/_get_credentials_type
       {
         org.omg.Security.InvocationCredentialsType $result = null;
         $result = this.credentials_type ();
         out = $rh.createReply();
         org.omg.Security.InvocationCredentialsTypeHelper.write (out, $result);
         break;
       }

       case 5:  // SecurityLevel2/Credentials/_get_authentication_state
       {
         org.omg.Security.AuthenticationStatus $result = null;
         $result = this.authentication_state ();
         out = $rh.createReply();
         org.omg.Security.AuthenticationStatusHelper.write (out, $result);
         break;
       }

       case 6:  // SecurityLevel2/Credentials/_get_mechanism
       {
         String $result = null;
         $result = this.mechanism ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 7:  // SecurityLevel2/Credentials/_get_accepting_options_supported
       {
         short $result = (short)0;
         $result = this.accepting_options_supported ();
         out = $rh.createReply();
         out.write_ushort ($result);
         break;
       }

       case 8:  // SecurityLevel2/Credentials/_set_accepting_options_supported
       {
         short newAccepting_options_supported = org.omg.Security.AssociationOptionsHelper.read (in);
         this.accepting_options_supported (newAccepting_options_supported);
         out = $rh.createReply();
         break;
       }

       case 9:  // SecurityLevel2/Credentials/_get_accepting_options_required
       {
         short $result = (short)0;
         $result = this.accepting_options_required ();
         out = $rh.createReply();
         out.write_ushort ($result);
         break;
       }

       case 10:  // SecurityLevel2/Credentials/_set_accepting_options_required
       {
         short newAccepting_options_required = org.omg.Security.AssociationOptionsHelper.read (in);
         this.accepting_options_required (newAccepting_options_required);
         out = $rh.createReply();
         break;
       }

       case 11:  // SecurityLevel2/Credentials/_get_invocation_options_supported
       {
         short $result = (short)0;
         $result = this.invocation_options_supported ();
         out = $rh.createReply();
         out.write_ushort ($result);
         break;
       }

       case 12:  // SecurityLevel2/Credentials/_set_invocation_options_supported
       {
         short newInvocation_options_supported = org.omg.Security.AssociationOptionsHelper.read (in);
         this.invocation_options_supported (newInvocation_options_supported);
         out = $rh.createReply();
         break;
       }

       case 13:  // SecurityLevel2/Credentials/_get_invocation_options_required
       {
         short $result = (short)0;
         $result = this.invocation_options_required ();
         out = $rh.createReply();
         out.write_ushort ($result);
         break;
       }

       case 14:  // SecurityLevel2/Credentials/_set_invocation_options_required
       {
         short newInvocation_options_required = org.omg.Security.AssociationOptionsHelper.read (in);
         this.invocation_options_required (newInvocation_options_required);
         out = $rh.createReply();
         break;
       }

       case 15:  // SecurityLevel2/Credentials/get_security_feature
       {
         org.omg.Security.CommunicationDirection direction = org.omg.Security.CommunicationDirectionHelper.read (in);
         org.omg.Security.SecurityFeature feature = org.omg.Security.SecurityFeatureHelper.read (in);
         boolean $result = false;
         $result = this.get_security_feature (direction, feature);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 16:  // SecurityLevel2/Credentials/set_attributes
       {
         org.omg.Security.SecAttribute requested_attributes[] = org.omg.Security.AttributeListHelper.read (in);
         org.omg.Security.AttributeListHolder actual_attributes = new org.omg.Security.AttributeListHolder ();
         boolean $result = false;
         $result = this.set_attributes (requested_attributes, actual_attributes);
         out = $rh.createReply();
         out.write_boolean ($result);
         org.omg.Security.AttributeListHelper.write (out, actual_attributes.value);
         break;
       }

       case 17:  // SecurityLevel2/Credentials/get_attributes
       {
         org.omg.Security.AttributeType attributes[] = org.omg.Security.AttributeTypeListHelper.read (in);
         org.omg.Security.SecAttribute $result[] = null;
         $result = this.get_attributes (attributes);
         out = $rh.createReply();
         org.omg.Security.AttributeListHelper.write (out, $result);
         break;
       }

       case 18:  // SecurityLevel2/Credentials/is_valid
       {
         org.omg.TimeBase.UtcTHolder expiry_time = new org.omg.TimeBase.UtcTHolder ();
         boolean $result = false;
         $result = this.is_valid (expiry_time);
         out = $rh.createReply();
         out.write_boolean ($result);
         org.omg.Security.UtcTHelper.write (out, expiry_time.value);
         break;
       }

       case 19:  // SecurityLevel2/Credentials/refresh
       {
         org.omg.CORBA.Any refresh_data = in.read_any ();
         boolean $result = false;
         $result = this.refresh (refresh_data);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:SecurityLevel2/TargetCredentials:1.0", 
    "IDL:SecurityLevel2/Credentials:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public TargetCredentials _this() 
  {
    return TargetCredentialsHelper.narrow(
    super._this_object());
  }

  public TargetCredentials _this(org.omg.CORBA.ORB orb) 
  {
    return TargetCredentialsHelper.narrow(
    super._this_object(orb));
  }


} // class TargetCredentialsPOA

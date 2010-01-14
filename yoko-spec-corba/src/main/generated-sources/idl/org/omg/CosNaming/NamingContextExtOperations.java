package org.omg.CosNaming;


/**
* org/omg/CosNaming/NamingContextExtOperations.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


/**
   * A naming context extension is an extension to naming context that contains a set of name bindings in 
   * which each name is unique. Different names can be bound to an object 
   * in the same or different contexts at the same time. <p>
   * 
   * See <a href=" http://www.omg.org/corba/sectrans.htm#nam">CORBA COS 
   * Naming Specification.</a>
   */
public interface NamingContextExtOperations  extends org.omg.CosNaming.NamingContextOperations
{

  /** 
       * The to_string operation is the process of retrieving a stringified name 
       * from a name object. 
       * 
       * @param n String Name of the object <p>
       * 
       * @exception org.omg.CosNaming.NamingContextPackage.InvalidName Indicates that the name is invalid. <p>
       */
  String to_string (org.omg.CosNaming.NameComponent[] n) throws org.omg.CosNaming.NamingContextPackage.InvalidName;

  /** 
       * The to_name operation is the process of retrieving a name object
       * to a stringified name. 
       * 
       * @param n String Name of the object <p>
       * 
       * @exception org.omg.CosNaming.NamingContextPackage.InvalidName Indicates that the name is invalid. <p>
       */
  org.omg.CosNaming.NameComponent[] to_name (String sn) throws org.omg.CosNaming.NamingContextPackage.InvalidName;

  /** 
       * The to_url operation is the process of retrieving a url representation from a stringified name and
       * address.
       * 
       * @param addr Address of the object <p>
       * 
       * @param sn String Name of the object <p>
       * 
       * @exception org.omg.CosNaming.NamingContextPackage.InvalidName Indicates that the name is invalid. <p>
       * 
       * @exception org.omg.CosNaming.NamingContextPackage.InvalidAddress Indicates that the Address is invalid. <p>
       */
  String to_url (String addr, String sn) throws org.omg.CosNaming.NamingContextExtPackage.InvalidAddress, org.omg.CosNaming.NamingContextPackage.InvalidName;

  /** 
       * The resolve_str operation is the process of retrieving an object
       * bound to a stringified name in a given context. The given name must exactly 
       * match the bound name. The naming service does not return the type 
       * of the object. Clients are responsible for "narrowing" the object 
       * to the appropriate type. That is, clients typically cast the returned 
       * object from Object to a more specialized interface.
       * 
       * @param n String Name of the object <p>
       * 
       * @exception org.omg.CosNaming.NamingContextPackage.NotFound Indicates the name does not identify a binding.<p>
       * 
       * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed Indicates that the implementation has
       * given up for some reason. The client, however, may be able to 
       * continue the operation at the returned naming context.<p>
       * 
       * @exception org.omg.CosNaming.NamingContextPackage.InvalidName Indicates that the name is invalid. <p>
       */
  org.omg.CORBA.Object resolve_str (String n) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName;
} // interface NamingContextExtOperations

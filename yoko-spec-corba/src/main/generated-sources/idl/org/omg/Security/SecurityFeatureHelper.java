package org.omg.Security;


/**
* org/omg/Security/SecurityFeatureHelper.java .
* Error reading Messages File.
* Error reading Messages File.
* Thursday, January 14, 2010 1:08:58 AM PST
*/


// Security features available on credentials.
abstract public class SecurityFeatureHelper
{
  private static String  _id = "IDL:omg.org/Security/SecurityFeature:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.SecurityFeature that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.SecurityFeature extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_enum_tc (org.omg.Security.SecurityFeatureHelper.id (), "SecurityFeature", new String[] { "SecNoDelegation", "SecSimpleDelegation", "SecCompositeDelegation", "SecNoProtection", "SecIntegrity", "SecConfidentiality", "SecIntegrityAndConfidentiality", "SecDetectReplay", "SecDetectMisordering", "SecEstablishTrustInTarget", "SecEstablishTrustInClient"} );
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.Security.SecurityFeature read (org.omg.CORBA.portable.InputStream istream)
  {
    return org.omg.Security.SecurityFeature.from_int (istream.read_long ());
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.SecurityFeature value)
  {
    ostream.write_long (value.value ());
  }

}

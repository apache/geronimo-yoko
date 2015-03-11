/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.yoko.orb.OB;
import org.omg.CSIIOP.TransportAddress;
import org.omg.IOP.TaggedComponent;

public final class IORUtil {
    
    private static void describeCSISecMechList(org.omg.IOP.TaggedComponent component, StringBuilder sb) {
        byte[] coct = component.component_data;
        int len = component.component_data.length;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, len);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        org.omg.CSIIOP.CompoundSecMechList info = org.omg.CSIIOP.CompoundSecMechListHelper.read(in);
        
        sb.append("CSI Security Mechanism List Components:\n"); 
        sb.append("    stateful: " + info.stateful + "\n"); 
        sb.append("    mechanism_list:\n"); 
        for (org.omg.CSIIOP.CompoundSecMech mech: info.mechanism_list) {
            sb.append("        target_requires: "); describeTransportFlags(mech.target_requires, sb); sb.append("\n"); 
            if (mech.transport_mech != null) {
                if (mech.transport_mech.tag == org.omg.CSIIOP.TAG_NULL_TAG.value) {
                    sb.append("            Null Transport\n"); 
                } else if (mech.transport_mech.tag == org.omg.CSIIOP.TAG_TLS_SEC_TRANS.value) {
                    describeTLS_SEC_TRANS(mech.transport_mech, sb); 
                } else if (mech.transport_mech.tag == org.omg.CSIIOP.TAG_SECIOP_SEC_TRANS.value) {
                    describeSECIOP_SEC_TRANS(mech.transport_mech, sb); 
                }
            }
            
            if (mech.as_context_mech != null) {
                sb.append("            as_context_mech:\n"); 
                sb.append("                supports: "); describeTransportFlags(mech.as_context_mech.target_supports, sb); sb.append("\n"); 
                sb.append("                requires: "); describeTransportFlags(mech.as_context_mech.target_requires, sb); sb.append("\n"); 
                sb.append("                client_authentication_mech: "); format_octets(mech.as_context_mech.client_authentication_mech, sb); sb.append("\n"); 
                sb.append("                target_name: "); format_octets(mech.as_context_mech.target_name, sb); sb.append("\n"); 
            }
            
            if (mech.sas_context_mech != null) {
                sb.append("            sas_context_mech:\n"); 
                sb.append("                supports: "); describeTransportFlags(mech.sas_context_mech.target_supports, sb); sb.append("\n"); 
                sb.append("                requires: "); describeTransportFlags(mech.sas_context_mech.target_requires, sb); sb.append("\n"); 
                sb.append("                privilege_authorities:\n");
                for (org.omg.CSIIOP.ServiceConfiguration auth: mech.sas_context_mech.privilege_authorities) {
                    sb.append("                    syntax: " + auth.syntax + "\n"); 
                    sb.append("                    name: "); format_octets(auth.name, sb); sb.append("\n"); 
                }
                sb.append("                supported_naming_mechanisms:\n");
                for (byte[] namingMech: mech.sas_context_mech.supported_naming_mechanisms) {
                    sb.append("                    "); format_octets(namingMech, sb); sb.append("\n"); 
                }
                sb.append("                supported_identity_type: "); describeIdentityToken(mech.sas_context_mech.supported_identity_types, sb); sb.append("\n");
            }
        }
    }
    
    
    private static void describeTransportFlags(int flag, StringBuilder sb) {

        if ((org.omg.CSIIOP.NoProtection.value & flag) != 0) {
            sb.append("NoProtection ");
        }
        if ((org.omg.CSIIOP.Integrity.value & flag) != 0) {
            sb.append("Integrity ");
        }
        if ((org.omg.CSIIOP.Confidentiality.value & flag) != 0) {
            sb.append("Confidentiality ");
        }
        if ((org.omg.CSIIOP.DetectReplay.value & flag) != 0) {
            sb.append("DetectReplay ");
        }
        if ((org.omg.CSIIOP.DetectMisordering.value & flag) != 0) {
            sb.append("DetectMisordering ");
        }
        if ((org.omg.CSIIOP.EstablishTrustInTarget.value & flag) != 0) {
            sb.append("EstablishTrustInTarget ");
        }
        if ((org.omg.CSIIOP.EstablishTrustInClient.value & flag) != 0) {
            sb.append("EstablishTrustInClient ");
        }
        if ((org.omg.CSIIOP.NoDelegation.value & flag) != 0) {
            sb.append("NoDelegation ");
        }
        if ((org.omg.CSIIOP.SimpleDelegation.value & flag) != 0) {
            sb.append("SimpleDelegation ");
        }
        if ((org.omg.CSIIOP.CompositeDelegation.value & flag) != 0) {
            sb.append("CompositeDelegation ");
        }
        if ((org.omg.CSIIOP.IdentityAssertion.value & flag) != 0) {
            sb.append("IdentityAssertion ");
        }
        if ((org.omg.CSIIOP.DelegationByClient.value & flag) != 0) {
            sb.append("DelegationByClient ");
        }
        
    }
    
    
    private static void describeIdentityToken(int flag, StringBuilder sb) {
        
        if (flag == org.omg.CSI.ITTAbsent.value) {
            sb.append("Absent"); 
            return;
        }
        

        if ((org.omg.CSI.ITTAnonymous.value & flag) != 0) {
            sb.append("Anonymous ");
        }
        if ((org.omg.CSI.ITTPrincipalName.value & flag) != 0) {
            sb.append("PrincipalName ");
        }
        if ((org.omg.CSI.ITTX509CertChain.value & flag) != 0) {
            sb.append("X509CertChain ");
        }
        if ((org.omg.CSI.ITTDistinguishedName.value & flag) != 0) {
            sb.append("DistinguishedName ");
        }
        
    }
    
    private static void describeTLS_SEC_TRANS(org.omg.IOP.TaggedComponent component, StringBuilder sb) {
        byte[] coct = component.component_data;
        int len = component.component_data.length;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, len);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        org.omg.CSIIOP.TLS_SEC_TRANS info = org.omg.CSIIOP.TLS_SEC_TRANSHelper.read(in);
        
        sb.append("        TLS_SEC_TRANS component:\n"); 
        sb.append("            target_supports: "); describeTransportFlags(info.target_supports, sb); sb.append("\n"); 
        sb.append("            target_requires: "); describeTransportFlags(info.target_requires, sb); sb.append("\n"); 
        sb.append("            addresses:\n"); 
        for (TransportAddress address: info.addresses) {
            sb.append("                host_name: ").append(address.host_name).append("\n"); 
            sb.append("                port: ").append(address.port).append("\n"); 
        }
        
    }
    
    private static void describeSECIOP_SEC_TRANS(org.omg.IOP.TaggedComponent component, StringBuilder sb) {
        byte[] coct = component.component_data;
        int len = component.component_data.length;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, len);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        org.omg.CSIIOP.SECIOP_SEC_TRANS info = org.omg.CSIIOP.SECIOP_SEC_TRANSHelper.read(in);
        
        sb.append("        SECIOP_SEC_TRANS component:\n"); 
        sb.append("            target_supports: "); describeTransportFlags(info.target_supports, sb); sb.append("\n"); 
        sb.append("            target_requires: "); describeTransportFlags(info.target_requires, sb); sb.append("\n"); 
        sb.append("            mech_oid: "); format_octets(info.mech_oid, sb); sb.append("\n"); 
        sb.append("            target_name: "); format_octets(info.target_name, sb); sb.append("\n"); 
        sb.append("            addresses:\n"); 
        for (TransportAddress address: info.addresses) {
            sb.append("                host_name: ").append(address.host_name).append("\n"); 
            sb.append("                port: ").append(address.port).append("\n"); 
        }
        
    }
    
    
    
    private static void describeCodeSets(org.omg.IOP.TaggedComponent component, StringBuilder sb) {
        byte[] coct = component.component_data;
        int len = component.component_data.length;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, len);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        org.omg.CONV_FRAME.CodeSetComponentInfo info = org.omg.CONV_FRAME.CodeSetComponentInfoHelper
                .read(in);

        CodeSetDatabase db = CodeSetDatabase.instance();
        CodeSetInfo charInfo;

        //
        // Print char codeset information
        //
        sb.append("Native char codeset: \n");
        charInfo = db.getCodeSetInfo(info.ForCharData.native_code_set);
        if (charInfo != null) {
            sb.append("  \"");
            sb.append(charInfo.description);
            sb.append("\"\n");
        } else if (info.ForCharData.native_code_set == 0)
            sb.append("  [No codeset information]\n");
        else {
            sb.append("  [Unknown codeset id: ");
            sb.append(info.ForCharData.native_code_set);
            sb.append("]\n");
        }

        for (int i = 0; i < info.ForCharData.conversion_code_sets.length; i++) {
            if (i == 0)
                sb.append("Char conversion codesets:\n");

            charInfo = db
                    .getCodeSetInfo(info.ForCharData.conversion_code_sets[i]);
            if (charInfo != null) {
                sb.append("  \"");
                sb.append(charInfo.description);
                sb.append("\"\n");
            } else {
                sb.append("  [Unknown codeset id: ");
                sb.append(info.ForCharData.conversion_code_sets[i]);
                sb.append("]\n");
            }
        }

        //
        // Print wchar codeset information
        //
        sb.append("Native wchar codeset: \n");
        charInfo = db.getCodeSetInfo(info.ForWcharData.native_code_set);
        if (charInfo != null) {
            sb.append("  \"");
            sb.append(charInfo.description);
            sb.append("\"\n");
        } else if (info.ForWcharData.native_code_set == 0)
            sb.append("  [No codeset information]\n");
        else {
            sb.append("  [Unknown codeset id: ");
            sb.append(info.ForWcharData.native_code_set);
            sb.append("]\n");
        }

        for (int i = 0; i < info.ForWcharData.conversion_code_sets.length; i++) {
            if (i == 0)
                sb.append("Wchar conversion codesets:\n");

            charInfo = db
                    .getCodeSetInfo(info.ForWcharData.conversion_code_sets[i]);
            if (charInfo != null) {
                sb.append("  \"");
                sb.append(charInfo.description);
                sb.append("\"\n");
            } else {
                sb.append("  [Unknown codeset id: ");
                sb.append(info.ForWcharData.conversion_code_sets[i]);
                sb.append("]\n");
            }
        }

    }

    private static void describeGenericComponent(
            org.omg.IOP.TaggedComponent component, String name, StringBuilder sb) {
        sb.append("Component: ");
        sb.append(name);
        sb.append('\n');
        sb.append("Component data: (");
        sb.append(component.component_data.length);
        sb.append(")\n");
        dump_octets(component.component_data, 0,
                component.component_data.length, sb);
    }

    //
    // Convert an octet buffer into human-friendly data dump
    //
    public static String dump_octets(byte[] oct) {
        StringBuilder sb = new StringBuilder();
        dump_octets(oct, 0, oct.length, sb); 
        return sb.toString();
    }
     
    public static void dump_octets(byte[] oct, StringBuilder sb) {
         dump_octets(oct, 0, oct.length, sb); 
    }

    //
    // Convert an octet buffer into human-friendly data dump
    //
    public static void dump_octets(byte[] oct, int offset, int count, StringBuilder sb) {
        final int inc = 16;
        
        if (count <= 0) {
            return; 
        }

        for (int i = offset; i < offset + count; i += inc) {
            for (int j = i; j - i < inc; j++) {
                if (j < offset + count) {
                    int n = ((int) oct[j]) & 0xff;
                    String hex = Integer.toHexString(n); 
                    if (hex.length() == 1) {
                        sb.append('0'); 
                    }
                    sb.append(hex); 
                    sb.append(' ');
                } else {
                    sb.append("   ");
                }
            }

            sb.append('"');

            for (int j = i; j < offset + count && j - i < inc; j++) {
                if (oct[j] >= (byte) 32 && oct[j] < (byte) 127) {
                    sb.append((char) oct[j]);
                }
                else {
                    sb.append('.');
                }
            }
            sb.append('"');
            sb.append('\n');
        }
    }

    //
    // Convert an octet buffer into a single-line readable data dump. 
    //
    public static void format_octets(byte[] oct, StringBuilder sb) {
        format_octets(oct, 0, oct.length, sb); 
    }

    //
    // Convert an octet buffer into a single-line readable data dump. 
    //
    public static void format_octets(byte[] oct, int offset, int count, StringBuilder sb) {
        if (count <= 0) {
            return; 
        }

        sb.append('"'); 

        for (int i = offset; i < offset + count; i++) {
            int n = (int) oct[i] & 0xff;
            if (n >= 32 && n <= 127) {
                sb.append((char)n); 
            }
            else {
                sb.append('?'); 
            }
        }

        sb.append('"'); 

    }

    //
    // Produce a human-friendly description of an IOR tagged component
    //
    public static void describe_component(
            org.omg.IOP.TaggedComponent component, StringBuilder sb) {

        switch (component.tag) {
        case org.omg.IOP.TAG_ORB_TYPE.value: {
            byte[] coct = component.component_data;
            int len = component.component_data.length;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    coct, len);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            in._OB_readEndian();
            int id = in.read_ulong();
            sb.append("Component: TAG_ORB_TYPE = ");
            sb.append("0x");
            sb.append(Integer.toHexString(id));
            sb.append('\n');
            break;
        }

        case org.omg.IOP.TAG_CODE_SETS.value:
            describeCodeSets(component, sb);
            break;

        case org.omg.IOP.TAG_POLICIES.value:
            describeGenericComponent(component, "TAG_POLICIES", sb);
            break;

        case org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS.value: {
            byte[] coct = component.component_data;
            int len = component.component_data.length;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    coct, len);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            in._OB_readEndian();
            String host = in.read_string();
            short port = in.read_ushort();
            sb.append("Alternate IIOP address:\n");
            sb.append("  host: ");
            sb.append(host);
            sb.append('\n');
            sb.append("  port: ");
            sb.append(port < 0 ? 0xffff + (int) port + 1 : port);
            sb.append('\n');
            break;
        }

        case org.omg.IOP.TAG_ASSOCIATION_OPTIONS.value:
            describeGenericComponent(component,
                    "TAG_ASSOCIATION_OPTIONS", sb);
            break;

        case org.omg.IOP.TAG_SEC_NAME.value:
            describeGenericComponent(component, "TAG_SEC_NAME", sb);
            break;

        case org.omg.IOP.TAG_SPKM_1_SEC_MECH.value:
            describeGenericComponent(component, "TAG_SPKM_1_SEC_MECH", sb);
            break;

        case org.omg.IOP.TAG_SPKM_2_SEC_MECH.value:
            describeGenericComponent(component, "TAG_SPKM_2_SEC_MECH", sb);
            break;

        case org.omg.IOP.TAG_KerberosV5_SEC_MECH.value:
            describeGenericComponent(component,
                    "TAG_KerberosV5_SEC_MECH", sb);
            break;

        case org.omg.IOP.TAG_CSI_ECMA_Secret_SEC_MECH.value:
            describeGenericComponent(component,
                    "TAG_CSI_ECMA_Secret_SEC_MECH", sb);
            break;

        case org.omg.IOP.TAG_CSI_ECMA_Hybrid_SEC_MECH.value:
            describeGenericComponent(component,
                    "TAG_CSI_ECMA_Hybrid_SEC_MECH", sb);
            break;

        case org.omg.IOP.TAG_CSI_SEC_MECH_LIST.value:
            describeCSISecMechList(component, sb);
            break;

        case org.omg.IOP.TAG_OTS_POLICY.value:
            describeGenericComponent(component,
                    "TAG_OTS_POLICY", sb);
            break;

        case org.omg.CosTSInteroperation.TAG_INV_POLICY.value:
            describeGenericComponent(component,
                    "TAG_INV_POLICY", sb);
            break;

        case org.omg.CSIIOP.TAG_SECIOP_SEC_TRANS.value:
            describeGenericComponent(component,
                    "TAG_SECIOP_SEC_TRANS", sb);
            break;

        case org.omg.CSIIOP.TAG_NULL_TAG.value:
            describeGenericComponent(component,
                    "TAG_NULL_TAG", sb);
            break;

        case org.omg.CSIIOP.TAG_TLS_SEC_TRANS.value:
            describeGenericComponent(component,
                    "TAG_TLS_SEC_TRANS", sb);
            break;

        case org.omg.IOP.TAG_SSL_SEC_TRANS.value:
            describeGenericComponent(component, "TAG_SSL_SEC_TRANS", sb);
            break;

        case org.omg.IOP.TAG_CSI_ECMA_Public_SEC_MECH.value:
            describeGenericComponent(component,
                    "TAG_CSI_ECMA_Public_SEC_MECH", sb);
            break;

        case org.omg.IOP.TAG_GENERIC_SEC_MECH.value:
            describeGenericComponent(component, "TAG_GENERIC_SEC_MECH", sb);
            break;

        case org.omg.IOP.TAG_JAVA_CODEBASE.value: {
            byte[] coct = component.component_data;
            int len = component.component_data.length;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    coct, len);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            in._OB_readEndian();
            String codebase = in.read_string();
            sb.append("Component: TAG_JAVA_CODEBASE = `");
            sb.append(codebase);
            sb.append("'\n");
            break;
        }

        case org.omg.IOP.TAG_COMPLETE_OBJECT_KEY.value:
            describeGenericComponent(component,
                    "TAG_COMPLETE_OBJECT_KEY", sb);
            break;

        case org.omg.IOP.TAG_ENDPOINT_ID_POSITION.value:
            describeGenericComponent(component,
                    "TAG_ENDPOINT_ID_POSITION", sb);
            break;

        case org.omg.IOP.TAG_LOCATION_POLICY.value:
            describeGenericComponent(component, "TAG_LOCATION_POLICY", sb);
            break;

        case org.omg.IOP.TAG_DCE_STRING_BINDING.value:
            describeGenericComponent(component,
                    "TAG_DCE_STRING_BINDING", sb);
            break;

        case org.omg.IOP.TAG_DCE_BINDING_NAME.value:
            describeGenericComponent(component, "TAG_DCE_BINDING_NAME", sb);
            break;

        case org.omg.IOP.TAG_DCE_NO_PIPES.value:
            describeGenericComponent(component, "TAG_DCE_NO_PIPES", sb);
            break;

        case org.omg.IOP.TAG_DCE_SEC_MECH.value:
            describeGenericComponent(component, "TAG_DCE_SEC_MECH", sb);
            break;

        default: {
            String name = "unknown (tag = ";
            name += component.tag;
            name += ")";
            describeGenericComponent(component, name, sb);
            break;
        }
        }

    }
}

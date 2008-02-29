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
import org.omg.IOP.TaggedComponent;

public final class IORUtil {
    private static String describeCSISecMechList(org.omg.IOP.TaggedComponent component) {
        String result = "";
        byte[] coct = component.component_data;
        int len = component.component_data.length;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, len);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        org.omg.CSIIOP.CompoundSecMechList info = org.omg.CSIIOP.CompoundSecMechListHelper.read(in);
        
        result += "CSI Security Mechanism List Components:\n"; 
        result += "    stateful: " + info.stateful + "\n"; 
        result += "    mechanism_list:\n"; 
        for (int i = 0; i < info.mechanism_list.length; i++) {
            org.omg.CSIIOP.CompoundSecMech mech = info.mechanism_list[i]; 
            result += "        target_requires: " + describeTransportFlags(mech.target_requires) + "\n"; 
            if (mech.transport_mech != null) {
                if (mech.transport_mech.tag == org.omg.CSIIOP.TAG_NULL_TAG.value) {
                    result += "            Null Transport\n"; 
                } else if (mech.transport_mech.tag == org.omg.CSIIOP.TAG_TLS_SEC_TRANS.value) {
                    result += describeTLS_SEC_TRANS(mech.transport_mech); 
                } else if (mech.transport_mech.tag == org.omg.CSIIOP.TAG_SECIOP_SEC_TRANS.value) {
                    result += describeSECIOP_SEC_TRANS(mech.transport_mech); 
                }
            }
            
            if (mech.as_context_mech != null) {
                result += "            as_context_mech:\n"; 
                result += "                supports: " + describeTransportFlags(mech.as_context_mech.target_supports) + "\n"; 
                result += "                requires: " + describeTransportFlags(mech.as_context_mech.target_requires) + "\n"; 
                result += "                client_authentication_mech: " + format_octets(mech.as_context_mech.client_authentication_mech) + "\n"; 
                result += "                target_name: " + format_octets(mech.as_context_mech.target_name) + "\n"; 
            }
            
            if (mech.sas_context_mech != null) {
                result += "            sas_context_mech:\n"; 
                result += "                supports: " + describeTransportFlags(mech.sas_context_mech.target_supports) + "\n"; 
                result += "                requires: " + describeTransportFlags(mech.sas_context_mech.target_requires) + "\n"; 
                result += "                privilege_authorities:\n";
                for (i = 0; i < mech.sas_context_mech.privilege_authorities.length; i++) {
                    org.omg.CSIIOP.ServiceConfiguration auth = mech.sas_context_mech.privilege_authorities[i];
                    result += "                    syntax: " + auth.syntax + "\n"; 
                    result += "                    name: " + format_octets(auth.name) + "\n"; 
                }
                result += "                supported_naming_mechanisms:\n";
                for (i = 0; i < mech.sas_context_mech.supported_naming_mechanisms.length; i++) {
                    result += "                    " + format_octets(mech.sas_context_mech.supported_naming_mechanisms[i]) + "\n"; 
                }
                result += "                supported_identity_type: " + describeIdentityToken(mech.sas_context_mech.supported_identity_types) + "\n";
            }
        }
        return result; 
    }
    
    
    private static String describeTransportFlags(int flag) {
        String result = "";

        if ((org.omg.CSIIOP.NoProtection.value & flag) != 0) {
            result += "NoProtection ";
        }
        if ((org.omg.CSIIOP.Integrity.value & flag) != 0) {
            result += "Integrity ";
        }
        if ((org.omg.CSIIOP.Confidentiality.value & flag) != 0) {
            result += "Confidentiality ";
        }
        if ((org.omg.CSIIOP.DetectReplay.value & flag) != 0) {
            result += "DetectReplay ";
        }
        if ((org.omg.CSIIOP.DetectMisordering.value & flag) != 0) {
            result += "DetectMisordering ";
        }
        if ((org.omg.CSIIOP.EstablishTrustInTarget.value & flag) != 0) {
            result += "EstablishTrustInTarget ";
        }
        if ((org.omg.CSIIOP.EstablishTrustInClient.value & flag) != 0) {
            result += "EstablishTrustInClient ";
        }
        if ((org.omg.CSIIOP.NoDelegation.value & flag) != 0) {
            result += "NoDelegation ";
        }
        if ((org.omg.CSIIOP.SimpleDelegation.value & flag) != 0) {
            result += "SimpleDelegation ";
        }
        if ((org.omg.CSIIOP.CompositeDelegation.value & flag) != 0) {
            result += "CompositeDelegation ";
        }
        if ((org.omg.CSIIOP.IdentityAssertion.value & flag) != 0) {
            result += "IdentityAssertion ";
        }
        if ((org.omg.CSIIOP.DelegationByClient.value & flag) != 0) {
            result += "DelegationByClient ";
        }
        
        return result; 
    }
    
    
    private static String describeIdentityToken(int flag) {
        
        if (flag == org.omg.CSI.ITTAbsent.value) {
            return "Absent"; 
        }
        
        String result = "";

        if ((org.omg.CSI.ITTAnonymous.value & flag) != 0) {
            result += "Anonymous ";
        }
        if ((org.omg.CSI.ITTPrincipalName.value & flag) != 0) {
            result += "PrincipalName ";
        }
        if ((org.omg.CSI.ITTX509CertChain.value & flag) != 0) {
            result += "X509CertChain ";
        }
        if ((org.omg.CSI.ITTDistinguishedName.value & flag) != 0) {
            result += "DistinguishedName ";
        }
        
        return result; 
    }
    
    private static String describeTLS_SEC_TRANS(org.omg.IOP.TaggedComponent component) {
        String result = "";
        byte[] coct = component.component_data;
        int len = component.component_data.length;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, len);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        org.omg.CSIIOP.TLS_SEC_TRANS info = org.omg.CSIIOP.TLS_SEC_TRANSHelper.read(in);
        
        result += "        TLS_SEC_TRANS component:\n"; 
        result += "            target_supports: " + describeTransportFlags(info.target_supports) + "\n"; 
        result += "            target_requires: " + describeTransportFlags(info.target_requires) + "\n"; 
        result += "            addresses:\n"; 
        for (int i = 0; i < info.addresses.length; i++) {
            result += "                host_name: " + info.addresses[i].host_name + "\n"; 
            result += "                port: " + info.addresses[i].port + "\n"; 
        }
        
        return result; 
    }
    
    private static String describeSECIOP_SEC_TRANS(org.omg.IOP.TaggedComponent component) {
        String result = "";
        byte[] coct = component.component_data;
        int len = component.component_data.length;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, len);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        org.omg.CSIIOP.SECIOP_SEC_TRANS info = org.omg.CSIIOP.SECIOP_SEC_TRANSHelper.read(in);
        
        result += "        SECIOP_SEC_TRANS component:\n"; 
        result += "            target_supports: " + describeTransportFlags(info.target_supports) + "\n"; 
        result += "            target_requires: " + describeTransportFlags(info.target_requires) + "\n"; 
        result += "            mech_oid: " + format_octets(info.mech_oid) + "\n"; 
        result += "            target_name: " + format_octets(info.target_name) + "\n"; 
        result += "            addresses:\n"; 
        for (int i = 0; i < info.addresses.length; i++) {
            result += "                host_name: " + info.addresses[i].host_name + "\n"; 
            result += "                port: " + info.addresses[i].port + "\n"; 
        }
        
        return result; 
    }
    
    
    
    private static String describeCodeSets(org.omg.IOP.TaggedComponent component) {
        String result = "";
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
        result += "Native char codeset: \n";
        charInfo = db.getCodeSetInfo(info.ForCharData.native_code_set);
        if (charInfo != null) {
            result += "  \"";
            result += charInfo.description;
            result += "\"\n";
        } else if (info.ForCharData.native_code_set == 0)
            result += "  [No codeset information]\n";
        else {
            result += "  [Unknown codeset id: ";
            result += info.ForCharData.native_code_set;
            result += "]\n";
        }

        for (int i = 0; i < info.ForCharData.conversion_code_sets.length; i++) {
            if (i == 0)
                result += "Char conversion codesets:\n";

            charInfo = db
                    .getCodeSetInfo(info.ForCharData.conversion_code_sets[i]);
            if (charInfo != null) {
                result += "  \"";
                result += charInfo.description;
                result += "\"\n";
            } else {
                result += "  [Unknown codeset id: ";
                result += info.ForCharData.conversion_code_sets[i];
                result += "]\n";
            }
        }

        //
        // Print wchar codeset information
        //
        result += "Native wchar codeset: \n";
        charInfo = db.getCodeSetInfo(info.ForWcharData.native_code_set);
        if (charInfo != null) {
            result += "  \"";
            result += charInfo.description;
            result += "\"\n";
        } else if (info.ForWcharData.native_code_set == 0)
            result += "  [No codeset information]\n";
        else {
            result += "  [Unknown codeset id: ";
            result += info.ForWcharData.native_code_set;
            result += "]\n";
        }

        for (int i = 0; i < info.ForWcharData.conversion_code_sets.length; i++) {
            if (i == 0)
                result += "Wchar conversion codesets:\n";

            charInfo = db
                    .getCodeSetInfo(info.ForWcharData.conversion_code_sets[i]);
            if (charInfo != null) {
                result += "  \"";
                result += charInfo.description;
                result += "\"\n";
            } else {
                result += "  [Unknown codeset id: ";
                result += info.ForWcharData.conversion_code_sets[i];
                result += "]\n";
            }
        }

        return result;
    }

    private static String describeGenericComponent(
            org.omg.IOP.TaggedComponent component, String name) {
        String result = "Component: ";
        result += name;
        result += '\n';
        result += "Component data: (";
        result += component.component_data.length;
        result += ")\n";
        String data = dump_octets(component.component_data, 0,
                component.component_data.length);
        result += data;
        return result;
    }

    //
    // Convert an octet buffer into human-friendly data dump
    //
    public static String dump_octets(byte[] oct) {
        return dump_octets(oct, 0, oct.length); 
    }

    //
    // Convert an octet buffer into human-friendly data dump
    //
    public static String dump_octets(byte[] oct, int offset, int count) {
        final int inc = 16;
        
        if (count <= 0) {
            return ""; 
        }

        StringBuffer result = new StringBuffer(count * 8);

        for (int i = offset; i < offset + count; i += inc) {
            for (int j = i; j - i < inc; j++) {
                if (j < offset + count) {
                    int n = ((int) oct[j]) & 0xff;
                    String hex = Integer.toHexString(n); 
                    if (hex.length() == 1) {
                        result.append('0'); 
                    }
                    result.append(hex); 
                    result.append(' ');
                } else {
                    result.append("   ");
                }
            }

            result.append('"');

            for (int j = i; j < offset + count && j - i < inc; j++) {
                if (oct[j] >= (byte) 32 && oct[j] < (byte) 127) {
                    result.append((char) oct[j]);
                }
                else {
                    result.append('.');
                }
            }
            result.append('"');
            result.append('\n');
        }
        return result.toString();
    }

    //
    // Convert an octet buffer into a single-line readable data dump. 
    //
    public static String format_octets(byte[] oct) {
        return format_octets(oct, 0, oct.length); 
    }

    //
    // Convert an octet buffer into a single-line readable data dump. 
    //
    public static String format_octets(byte[] oct, int offset, int count) {
        if (count <= 0) {
            return ""; 
        }

        StringBuffer result = new StringBuffer(count * 8);
        result.append('"'); 

        for (int i = offset; i < offset + count; i++) {
            int n = (int) oct[i] & 0xff;
            if (n >= 32 && n <= 127) {
                result.append((char)n); 
            }
            else {
                result.append('?'); 
            }
        }

        result.append('"'); 

        return result.toString();
    }

    //
    // Produce a human-friendly description of an IOR tagged component
    //
    public static String describe_component(
            org.omg.IOP.TaggedComponent component) {
        String result;

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
            result = "Component: TAG_ORB_TYPE = ";
            result += "0x";
            result += Integer.toHexString(id);
            result += '\n';
            break;
        }

        case org.omg.IOP.TAG_CODE_SETS.value:
            result = describeCodeSets(component);
            break;

        case org.omg.IOP.TAG_POLICIES.value:
            result = describeGenericComponent(component, "TAG_POLICIES");
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
            result = "Alternate IIOP address:\n";
            result += "  host: ";
            result += host;
            result += '\n';
            result += "  port: ";
            result += (port < 0 ? 0xffff + (int) port + 1 : port);
            result += '\n';
            break;
        }

        case org.omg.IOP.TAG_ASSOCIATION_OPTIONS.value:
            result = describeGenericComponent(component,
                    "TAG_ASSOCIATION_OPTIONS");
            break;

        case org.omg.IOP.TAG_SEC_NAME.value:
            result = describeGenericComponent(component, "TAG_SEC_NAME");
            break;

        case org.omg.IOP.TAG_SPKM_1_SEC_MECH.value:
            result = describeGenericComponent(component, "TAG_SPKM_1_SEC_MECH");
            break;

        case org.omg.IOP.TAG_SPKM_2_SEC_MECH.value:
            result = describeGenericComponent(component, "TAG_SPKM_2_SEC_MECH");
            break;

        case org.omg.IOP.TAG_KerberosV5_SEC_MECH.value:
            result = describeGenericComponent(component,
                    "TAG_KerberosV5_SEC_MECH");
            break;

        case org.omg.IOP.TAG_CSI_ECMA_Secret_SEC_MECH.value:
            result = describeGenericComponent(component,
                    "TAG_CSI_ECMA_Secret_SEC_MECH");
            break;

        case org.omg.IOP.TAG_CSI_ECMA_Hybrid_SEC_MECH.value:
            result = describeGenericComponent(component,
                    "TAG_CSI_ECMA_Hybrid_SEC_MECH");
            break;

        case org.omg.IOP.TAG_CSI_SEC_MECH_LIST.value:
            result = describeCSISecMechList(component);
            break;

        case org.omg.IOP.TAG_OTS_POLICY.value:
            result = describeGenericComponent(component,
                    "TAG_OTS_POLICY");
            break;

        case org.omg.CosTSInteroperation.TAG_INV_POLICY.value:
            result = describeGenericComponent(component,
                    "TAG_INV_POLICY");
            break;

        case org.omg.CSIIOP.TAG_SECIOP_SEC_TRANS.value:
            result = describeGenericComponent(component,
                    "TAG_SECIOP_SEC_TRANS");
            break;

        case org.omg.CSIIOP.TAG_NULL_TAG.value:
            result = describeGenericComponent(component,
                    "TAG_NULL_TAG");
            break;

        case org.omg.CSIIOP.TAG_TLS_SEC_TRANS.value:
            result = describeGenericComponent(component,
                    "TAG_TLS_SEC_TRANS");
            break;

        case org.omg.IOP.TAG_SSL_SEC_TRANS.value:
            result = describeGenericComponent(component, "TAG_SSL_SEC_TRANS");
            break;

        case org.omg.IOP.TAG_CSI_ECMA_Public_SEC_MECH.value:
            result = describeGenericComponent(component,
                    "TAG_CSI_ECMA_Public_SEC_MECH");
            break;

        case org.omg.IOP.TAG_GENERIC_SEC_MECH.value:
            result = describeGenericComponent(component, "TAG_GENERIC_SEC_MECH");
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
            result = "Component: TAG_JAVA_CODEBASE = `";
            result += codebase;
            result += "'\n";
            break;
        }

        case org.omg.IOP.TAG_COMPLETE_OBJECT_KEY.value:
            result = describeGenericComponent(component,
                    "TAG_COMPLETE_OBJECT_KEY");
            break;

        case org.omg.IOP.TAG_ENDPOINT_ID_POSITION.value:
            result = describeGenericComponent(component,
                    "TAG_ENDPOINT_ID_POSITION");
            break;

        case org.omg.IOP.TAG_LOCATION_POLICY.value:
            result = describeGenericComponent(component, "TAG_LOCATION_POLICY");
            break;

        case org.omg.IOP.TAG_DCE_STRING_BINDING.value:
            result = describeGenericComponent(component,
                    "TAG_DCE_STRING_BINDING");
            break;

        case org.omg.IOP.TAG_DCE_BINDING_NAME.value:
            result = describeGenericComponent(component, "TAG_DCE_BINDING_NAME");
            break;

        case org.omg.IOP.TAG_DCE_NO_PIPES.value:
            result = describeGenericComponent(component, "TAG_DCE_NO_PIPES");
            break;

        case org.omg.IOP.TAG_DCE_SEC_MECH.value:
            result = describeGenericComponent(component, "TAG_DCE_SEC_MECH");
            break;

        default: {
            String name = "unknown (tag = ";
            name += component.tag;
            name += ")";
            result = describeGenericComponent(component, name);
            break;
        }
        }

        return result;
    }
}

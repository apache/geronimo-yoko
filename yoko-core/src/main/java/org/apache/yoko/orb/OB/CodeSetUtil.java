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

final public class CodeSetUtil {
    //
    // The supported codesets in the preferred order
    //
    private static java.util.Vector supportedCharCodeSets_ = new java.util.Vector();

    private static java.util.Vector supportedWcharCodeSets_ = new java.util.Vector();

    static void addCharCodeSet(int id) {
        supportedCharCodeSets_.addElement(new Integer(id));
    }

    static void addWcharCodeSet(int id) {
        supportedWcharCodeSets_.addElement(new Integer(id));
    }

    static org.omg.CONV_FRAME.CodeSetComponent createCodeSetComponent(int id,
            boolean wChar) {
        CodeSetDatabase.instance();

        org.omg.CONV_FRAME.CodeSetComponent codeSetComponent = new org.omg.CONV_FRAME.CodeSetComponent();

        codeSetComponent.native_code_set = id;

        java.util.Vector conversion_code_sets = new java.util.Vector();

        //
        // Add conversion codesets, filter native codeset
        //
        java.util.Enumeration e = wChar ? supportedWcharCodeSets_.elements()
                : supportedCharCodeSets_.elements();
        while (e.hasMoreElements()) {
            Integer cs = (Integer) e.nextElement();
            if (cs.intValue() != id)
                conversion_code_sets.addElement(cs);
        }

        codeSetComponent.conversion_code_sets = new int[conversion_code_sets
                .size()];
        e = conversion_code_sets.elements();
        int i = 0;
        while (e.hasMoreElements())
            codeSetComponent.conversion_code_sets[i++] = ((Integer) e
                    .nextElement()).intValue();

        return codeSetComponent;
    }

    //
    // Extract codeset information
    //
    static boolean getCodeSetInfoFromComponents(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.omg.CONV_FRAME.CodeSetComponentInfoHolder info) {
        //
        // Only IIOP 1.1 or newer has codeset information
        //
        if (profileInfo.major == 1 && profileInfo.minor > 0) {
            for (int i = 0; i < profileInfo.components.length; i++) {
                if (checkForCodeSetInfo(profileInfo.components[i], info))
                    return true;
            }
        }
        //
        // For IIOP 1.0 use proprietary mechanism (ISOLATIN1 and UCS2),
        // if configured.
        //
        else {
            if (orbInstance.extendedWchar()) {
                info.value = new org.omg.CONV_FRAME.CodeSetComponentInfo(
                        new org.omg.CONV_FRAME.CodeSetComponent(
                                CodeSetDatabase.ISOLATIN1, new int[0]),
                        new org.omg.CONV_FRAME.CodeSetComponent(
                                CodeSetDatabase.UCS2, new int[0]));

                return true;
            }
        }

        return false;
    }

    //
    // Get code converters from ProfileInfo and/or IOR
    //
    static CodeConverters getCodeConverters(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo) {
        //
        // Set codeset defaults: ISO 8859-1 for char, no default for wchar
        // (13.7.2.4) but ORBacus uses default_wcs, which is initially 0
        //
        org.omg.CONV_FRAME.CodeSetComponentInfoHolder serverInfo = new org.omg.CONV_FRAME.CodeSetComponentInfoHolder();
        serverInfo.value = new org.omg.CONV_FRAME.CodeSetComponentInfo();
        serverInfo.value.ForCharData = new org.omg.CONV_FRAME.CodeSetComponent();
        serverInfo.value.ForWcharData = new org.omg.CONV_FRAME.CodeSetComponent();
        serverInfo.value.ForCharData.native_code_set = CodeSetDatabase.ISOLATIN1;
        serverInfo.value.ForCharData.conversion_code_sets = new int[0];
        serverInfo.value.ForWcharData.native_code_set = orbInstance
                .getDefaultWcs();
        serverInfo.value.ForWcharData.conversion_code_sets = new int[0];

        //
        // Set up code converters
        //
        int nativeCs = orbInstance.getNativeCs();
        org.omg.CONV_FRAME.CodeSetComponent client_cs = createCodeSetComponent(
                nativeCs, false);
        int tcs_c = CodeSetDatabase.ISOLATIN1;
        int nativeWcs = orbInstance.getNativeWcs();
        org.omg.CONV_FRAME.CodeSetComponent client_wcs = createCodeSetComponent(
                nativeWcs, true);
        int tcs_wc = orbInstance.getDefaultWcs();

        CodeSetDatabase db = CodeSetDatabase.instance();

        //
        // Other transmission codesets than the defaults can only be
        // determined if a codeset profile was present in the IOR.
        // The fallbacks in this case according to the specification
        // are UTF-8 (not ISOLATIN1!) and UTF-16 (not UCS2!).
        //
        if (getCodeSetInfoFromComponents(orbInstance, profileInfo, serverInfo)) {
            tcs_c = db.determineTCS(client_cs, serverInfo.value.ForCharData,
                    CodeSetDatabase.UTF8);
            tcs_wc = db.determineTCS(client_wcs, serverInfo.value.ForWcharData,
                    CodeSetDatabase.UTF16);
        }

        CodeConverters conv = new CodeConverters();
        conv.inputCharConverter = db.getConverter(nativeCs, tcs_c);
        conv.outputCharConverter = db.getConverter(tcs_c, nativeCs);
        conv.inputWcharConverter = db.getConverter(nativeWcs, tcs_wc);
        conv.outputWcharConverter = db.getConverter(tcs_wc, nativeWcs);

        return conv;
    }

    //
    // Check for codeset information in a tagged component
    //
    static boolean checkForCodeSetInfo(org.omg.IOP.TaggedComponent comp,
            org.omg.CONV_FRAME.CodeSetComponentInfoHolder info) {
        if (comp.tag == org.omg.IOP.TAG_CODE_SETS.value) {
            byte[] coct = comp.component_data;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    coct, coct.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf, 0, false);
            in._OB_readEndian();
            info.value = org.omg.CONV_FRAME.CodeSetComponentInfoHelper.read(in);
            return true;
        }

        return false;
    }

    //
    // Extract codeset context from service context
    //
    static void extractCodeSetContext(org.omg.IOP.ServiceContext context,
            org.omg.CONV_FRAME.CodeSetContextHolder ctx) {
        byte[] coct = context.context_data;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                coct, coct.length);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);
        in._OB_readEndian();
        ctx.value = org.omg.CONV_FRAME.CodeSetContextHelper.read(in);
    }
}

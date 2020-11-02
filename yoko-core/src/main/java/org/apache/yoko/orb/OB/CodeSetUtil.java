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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.omg.CONV_FRAME.CodeSetComponent;
import org.omg.CONV_FRAME.CodeSetComponentInfo;
import org.omg.CONV_FRAME.CodeSetComponentInfoHelper;
import org.omg.CONV_FRAME.CodeSetComponentInfoHolder;
import org.omg.CONV_FRAME.CodeSetContextHelper;
import org.omg.CONV_FRAME.CodeSetContextHolder;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TAG_CODE_SETS;
import org.omg.IOP.TaggedComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static org.apache.yoko.orb.OB.CodeSetDatabase.determineTCS;
import static org.apache.yoko.orb.OB.CodeSetDatabase.getConverter;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_646_IRV;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_LATIN_1;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_LATIN_2;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_LATIN_3;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_LATIN_4;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_8859_5;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_8859_7;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_8859_9;
import static org.apache.yoko.orb.OB.CodeSetInfo.UCS_2;
import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_16;
import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_8;

final public class CodeSetUtil {
    //
    // The supported codesets in the preferred order
    //
    private static final List<CodeSetInfo> SUPPORTED_CHAR_CODESETS = unmodifiableList(getSupportedCharCodeSets());
    private static final List<CodeSetInfo> SUPPORTED_WCHAR_CODESETS = unmodifiableList(asList(UTF_16));

    private static List<CodeSetInfo> getLocaleSpecificCodeSets() {
        String language = Locale.getDefault().getLanguage();
        switch (language) {
        case "C":
        case "POSIX":
            return asList(ISO_LATIN_1);
        }

        switch (language.substring(0, 2)) {
        case "de": // German
        case "en": // English
        case "fr": // French
        case "nl": // Dutch
        case "pt": // Portuguese
            return asList(ISO_LATIN_1);
        case "da": // Danish
        case "fi": // Finnish
        case "is": // Icelandic
        case "no": // Norwegian
        case "sv": // Swedish
            return asList(ISO_LATIN_4);
        case "it": // Italian
            return asList(ISO_LATIN_3);
        case "cs": // Czech
        case "hu": // Hungarian
        case "pl": // Polish
        case "sk": // Slovakian
        case "sl": // Slovenia
            return asList(ISO_LATIN_2);
        case "el": // Greek
            return asList(ISO_8859_7);
        case "ru": // Russian
            return asList(ISO_8859_5);
        case "tr": // Turkish
            return asList(ISO_8859_9);
        default: // unsupported locale
            return emptyList();
        }
    }

    private static List<CodeSetInfo> getSupportedCharCodeSets() {
        List<CodeSetInfo> result = new ArrayList<>();
        result.addAll(getLocaleSpecificCodeSets());
        result.add(ISO_646_IRV); // Always supported
        result.add(UTF_8); // Always supported, but only as transmission codeset
        return result;
    }

    static CodeSetComponent createCodeSetComponent(int id, boolean wChar) {
        CodeSetComponent codeSetComponent = new CodeSetComponent();

        codeSetComponent.native_code_set = id;

        List<Integer> ids = new ArrayList<>();

        //
        // Add conversion codesets, filter native codeset
        //
        for (CodeSetInfo csi: wChar ? SUPPORTED_WCHAR_CODESETS : SUPPORTED_CHAR_CODESETS) {
            if (id != csi.id) ids.add(csi.id);
        }

        codeSetComponent.conversion_code_sets = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            codeSetComponent.conversion_code_sets[i] = ids.get(i);
        }

        return codeSetComponent;
    }

    //
    // Extract codeset information
    //
    static boolean getCodeSetInfoFromComponents(ORBInstance orbInstance,
            ProfileInfo profileInfo,
            CodeSetComponentInfoHolder info) {
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
                info.value = new CodeSetComponentInfo(
                        new CodeSetComponent(ISO_LATIN_1.id, new int[0]),
                        new CodeSetComponent(UCS_2.id, new int[0]));

                return true;
            }
        }

        return false;
    }

    //
    // Get code converters from ProfileInfo and/or IOR
    //
    static CodeConverters getCodeConverters(ORBInstance orbInstance, ProfileInfo profileInfo) {
        //
        // Set codeset defaults: ISO 8859-1 for char, no default for wchar
        // (13.7.2.4) but ORBacus uses default_wcs, which is initially 0
        //
        CodeSetComponentInfoHolder serverInfo = new CodeSetComponentInfoHolder();
        serverInfo.value = new CodeSetComponentInfo();
        serverInfo.value.ForCharData = new CodeSetComponent();
        serverInfo.value.ForWcharData = new CodeSetComponent();
        serverInfo.value.ForCharData.native_code_set = ISO_LATIN_1.id;
        serverInfo.value.ForCharData.conversion_code_sets = new int[0];
        serverInfo.value.ForWcharData.native_code_set = orbInstance.getDefaultWcs();
        serverInfo.value.ForWcharData.conversion_code_sets = new int[0];

        //
        // Set up code converters
        //
        int nativeCs = orbInstance.getNativeCs();
        CodeSetComponent client_cs = createCodeSetComponent(nativeCs, false);
        int tcs_c = ISO_LATIN_1.id;
        int nativeWcs = orbInstance.getNativeWcs();
        CodeSetComponent client_wcs = createCodeSetComponent(nativeWcs, true);
        int tcs_wc = orbInstance.getDefaultWcs();

        //
        // Other transmission codesets than the defaults can only be
        // determined if a codeset profile was present in the IOR.
        // The fallbacks in this case according to the specification
        // are UTF-8 (not ISOLATIN1!) and UTF-16 (not UCS2!).
        //
        if (getCodeSetInfoFromComponents(orbInstance, profileInfo, serverInfo)) {
            tcs_c = determineTCS(client_cs, serverInfo.value.ForCharData, UTF_8.id);
            tcs_wc = determineTCS(client_wcs, serverInfo.value.ForWcharData, UTF_16.id);
        }

        final CodeConverterBase inputCharConverter = getConverter(nativeCs, tcs_c);
        final CodeConverterBase outputCharConverter = getConverter(tcs_c, nativeCs);
        final CodeConverterBase inputWcharConverter = getConverter(nativeWcs, tcs_wc);
        final CodeConverterBase outputWcharConverter = getConverter(tcs_wc, nativeWcs);

        return CodeConverters.create(inputCharConverter, outputCharConverter, inputWcharConverter, outputWcharConverter);
    }

    //
    // Check for codeset information in a tagged component
    //
    static boolean checkForCodeSetInfo(TaggedComponent comp, CodeSetComponentInfoHolder info) {
        if (comp.tag == TAG_CODE_SETS.value) {
            InputStream in = new InputStream(comp.component_data);
            in._OB_readEndian();
            info.value = CodeSetComponentInfoHelper.read(in);
            return true;
        }

        return false;
    }

    //
    // Extract codeset context from service context
    //
    static void extractCodeSetContext(ServiceContext context, CodeSetContextHolder ctx) {
        InputStream in = new InputStream(context.context_data);
        in._OB_readEndian();
        ctx.value = CodeSetContextHelper.read(in);
    }
}

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
import org.omg.CONV_FRAME.CodeSetContext;
import org.omg.CONV_FRAME.CodeSetContextHelper;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TAG_CODE_SETS;

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
    static CodeSetComponentInfo getCodeSetInfoFromComponents(ORBInstance orbInstance, ProfileInfo profileInfo) {
        //
        // Only IIOP 1.1 or newer has codeset information
        //
        if (profileInfo.major == 1 && profileInfo.minor > 0) {
            for (int i = 0; i < profileInfo.components.length; i++) {
                if (profileInfo.components[i].tag == TAG_CODE_SETS.value) {
                    InputStream in = new InputStream(profileInfo.components[i].component_data);
                    in._OB_readEndian();
                    return CodeSetComponentInfoHelper.read(in);
                }
            }
        }
        //
        // For IIOP 1.0 use proprietary mechanism (ISOLATIN1 and UCS2),
        // if configured.
        //
        else {
            if (orbInstance.extendedWchar()) {
                return new CodeSetComponentInfo(
                        new CodeSetComponent(ISO_LATIN_1.id, new int[0]),
                        new CodeSetComponent(UCS_2.id, new int[0]));
            }
        }

        return null;
    }

    //
    // Get code converters from ProfileInfo and/or IOR
    //
    static CodeConverters getCodeConverters(ORBInstance orbInstance, ProfileInfo profileInfo) {
        //
        // Set up code converters
        //
        //
        // Other transmission codesets than the defaults can only be
        // determined if a codeset profile was present in the IOR.
        // The fallbacks in this case according to the specification
        // are UTF-8 (not ISOLATIN1!) and UTF-16 (not UCS2!).
        //

        final CodeSetComponentInfo info = getCodeSetInfoFromComponents(orbInstance, profileInfo);

        if (info == null) return CodeConverters.create(orbInstance, ISO_LATIN_1.id, orbInstance.getDefaultWcs());

        CodeSetComponent client_cs = createCodeSetComponent(orbInstance.getNativeCs(), false);
        CodeSetComponent client_wcs = createCodeSetComponent(orbInstance.getNativeWcs(), true);
        final int tcs_c = determineTCS(client_cs, info.ForCharData, UTF_8.id);
        final int tcs_wc = determineTCS(client_wcs, info.ForWcharData, UTF_16.id);
        return CodeConverters.create(orbInstance, tcs_c, tcs_wc);
    }

    static CodeSetContext extractCodeSetContext(ServiceContext csSC) {
        InputStream in = new InputStream(csSC.context_data);
        in._OB_readEndian();
        return CodeSetContextHelper.read(in);
    }
}

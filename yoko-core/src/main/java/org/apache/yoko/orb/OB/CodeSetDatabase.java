/*
 * Copyright 2020 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OB;

import org.omg.CONV_FRAME.CodeSetComponent;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

enum CodeSetDatabase {
    ;

    static CodeConverterBase getConverter(int from, int to) {
        CodeSetInfo fromSet = CodeSetInfo.forRegistryId(from);
        CodeSetInfo toSet = CodeSetInfo.forRegistryId(to);
        return getConverter(fromSet, toSet);
    }

    static CodeConverterBase getConverter(CodeSetInfo fromSet, CodeSetInfo toSet) {
        // Optimization: don't use converter for identical narrow codesets
        if (toSet != null && toSet == fromSet && toSet.max_bytes == 1) return null;

        if (fromSet == null || toSet == null) return new CodeConverterNone(fromSet, toSet);

        // the unsupported codesets should have been filtered out by the initial handshake
        return new CodeConverterImpl(fromSet, toSet);
    }

    static int determineTCS(CodeSetComponent clientCS, CodeSetComponent serverCS, int fallback) {
        // Check if native codesets are present
        if (clientCS.native_code_set != 0 && serverCS.native_code_set != 0) {
            // Check if the native codesets are identical
            // If they are then no conversion is required
            if (clientCS.native_code_set == serverCS.native_code_set)
                return serverCS.native_code_set;

            // Check if client can convert
            if (checkCodeSetId(clientCS, serverCS.native_code_set))
                return serverCS.native_code_set;

            // Check if server can convert
            if (checkCodeSetId(serverCS, clientCS.native_code_set))
                return clientCS.native_code_set;
        }

        // Check for common codeset that can be used for transmission
        // The server supported codesets have preference
        for (int conversionCodeSet : serverCS.conversion_code_sets) {
            if (checkCodeSetId(clientCS, conversionCodeSet)) return conversionCodeSet;
        }

        if (clientCS.native_code_set != 0 && serverCS.native_code_set != 0) {
            // Check compatibility by using the OSF registry,
            // use fallback codeset if compatible
            if (isCompatible(clientCS.native_code_set, serverCS.native_code_set))
                return fallback;
        }

        throw new CODESET_INCOMPATIBLE();
    }

    private static boolean isCompatible(int id1, int id2) {
        CodeSetInfo cs1 = CodeSetInfo.forRegistryId(id1);
        if (cs1 == null) return false;

        CodeSetInfo cs2 = CodeSetInfo.forRegistryId(id2);

        return cs1.isCompatibleWith(cs2);
    }

    private static boolean checkCodeSetId(CodeSetComponent csc, int id) {
        for (int cs : csc.conversion_code_sets) {
            if (cs == id) return true;
        }

        //
        // ID not found
        //
        return false;
    }
}

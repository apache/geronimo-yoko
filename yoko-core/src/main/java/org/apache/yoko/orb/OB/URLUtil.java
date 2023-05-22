/*
 * Copyright 2021 IBM Corporation and others.
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

import org.apache.yoko.util.MinorCodes;

class URLUtil {
    // 
    // Remove URL escape sequences from a string
    // 
    public static String unescapeURL(final String str) {
        final int len = str.length();
        char[] buf = new char[len];
        int bpos = 0, pos = 0;

        while (pos < len) {
            char ch = str.charAt(pos);

            //
            // Escape sequence '%' must be followed by
            // 2 valid characters 0-9, A-F, a-f
            //
            if (ch == '%') {
                if (pos + 2 >= len)
                    throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                            .describeBadParam(MinorCodes.MinorBadAddress)
                            + ": bad escape sequence length",
                            MinorCodes.MinorBadAddress,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                int c1 = Character.digit(str.charAt(++pos), 16);
                int c2 = Character.digit(str.charAt(++pos), 16);

                if (c1 == -1 || c2 == -1)
                    throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                            .describeBadParam(MinorCodes.MinorOther)
                            + ": escape sequence contains invalid characters",
                            MinorCodes.MinorOther,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                ch = (char) ((c1 << 4) | c2);
            }
            buf[bpos++] = ch;
            ++pos;
        }

        return new String(buf, 0, bpos);
    }
}

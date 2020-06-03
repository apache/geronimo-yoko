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

package org.apache.yoko.util;

import java.util.Arrays;

public final class HexConverter {
    private final static char[] NYBBLE_TO_HEX_CHAR = "0123456789abcdef".toCharArray();
    private final static byte[] HEX_CHAR_TO_NYBBLE = new byte['f' + 1];
    static {
        Arrays.fill(HEX_CHAR_TO_NYBBLE, (byte)-1);
        for (byte i = 0; i < NYBBLE_TO_HEX_CHAR.length; i++) {
            char c = NYBBLE_TO_HEX_CHAR[i];
            HEX_CHAR_TO_NYBBLE[c] = i;
            HEX_CHAR_TO_NYBBLE[Character.toUpperCase(c)] = i;
        }
    }

    private static char[] octetsToAsciiChars(byte[] oct, int count) {
        assert count <= oct.length;

        char[] result = new char[count * 2];

        for (int i = 0, pos = 0; i < count; i++) {
            result[pos++] = NYBBLE_TO_HEX_CHAR[oct[i] >> 4 & 0x0f];
            result[pos++] = NYBBLE_TO_HEX_CHAR[oct[i] >> 0 & 0x0f];
        }

        return result;
    }

    public static String octetsToAscii(byte[] oct) {
        if (oct == null) return null;
        return new String(octetsToAsciiChars(oct, oct.length));
    }

    public static String octetsToAscii(byte[] oct, int count) {
        if (oct == null) return null;
        return new String(octetsToAsciiChars(oct, count));
    }

    public static byte[] asciiToOctets(String str, int offset) {
        int slen = str.length() - offset;

        //
        // Two ASCII characters for each octet
        //
        if ((slen & 1) != 0) return null;

        byte[] oct = new byte[slen/2];

        try {

            for (int i = 0, j = offset; i < oct.length; i++) {
                char highChar = str.charAt(j++);
                char lowChar = str.charAt(j++);
                int high = HEX_CHAR_TO_NYBBLE[highChar];
                if (high < 0) return null;
                int low = HEX_CHAR_TO_NYBBLE[lowChar];
                if (low < 0) return null;


                oct[i] = (byte) ((high << 4) | low);
            }

            return oct;

        } catch (ArrayIndexOutOfBoundsException swallowed) {
            return null;
        }
    }

    public static byte[] asciiToOctets(String str) {
        return asciiToOctets(str, 0);
    }
}

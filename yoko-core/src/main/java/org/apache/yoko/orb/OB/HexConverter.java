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

public final class HexConverter {
    final static private char[] asciiToHex = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static byte[] octetsToAsciiBytes(byte[] oct, int count) {
        Assert._OB_assert(count <= oct.length);

        byte[] result = new byte[count * 2];

        for (int i = 0, pos = 0; i < count; i++) {
            byte b = oct[i];
            result[pos++] = (byte) asciiToHex[(b >> 4) & 0x0f];
            result[pos++] = (byte) asciiToHex[b & 0x0f];
        }

        return result;
    }

    private static char[] octetsToAsciiChars(byte[] oct, int count) {
        Assert._OB_assert(count <= oct.length);

        char[] result = new char[count * 2];

        for (int i = 0, pos = 0; i < count; i++) {
            byte b = oct[i];
            result[pos++] = asciiToHex[(b >> 4) & 0x0f];
            result[pos++] = asciiToHex[b & 0x0f];
        }

        return result;
    }

    public static String octetsToAscii(byte[] oct, int count) {
        StringBuffer buf = new StringBuffer(count * 2);
        buf.append(octetsToAsciiChars(oct, count));
        return buf.toString();
    }

    public static byte[] asciiToOctets(String str, int offset) {
        int slen = str.length() - offset;

        //
        // Two ASCII characters for each octet
        //
        if ((slen & 1) != 0)
            return null;

        int len = slen >> 1;
        byte[] oct = new byte[len];

        for (int i = 0, j = offset; i < len; i++) {
            char highChar = str.charAt(j++);
            char lowChar = str.charAt(j++);
            int high, low;

            if (highChar >= '0' && highChar <= '9')
                high = highChar - '0';
            else if (highChar >= 'a' && highChar <= 'f')
                high = 10 + highChar - 'a';
            else if (highChar >= 'A' && highChar <= 'F')
                high = 10 + highChar - 'A';
            else
                return null;

            if (lowChar >= '0' && lowChar <= '9')
                low = lowChar - '0';
            else if (lowChar >= 'a' && lowChar <= 'f')
                low = 10 + lowChar - 'a';
            else if (lowChar >= 'A' && lowChar <= 'F')
                low = 10 + lowChar - 'A';
            else
                return null;

            oct[i] = (byte) (16 * high + low);
        }

        return oct;
    }

    public static byte[] asciiToOctets(String str) {
        return asciiToOctets(str, 0);
    }
}

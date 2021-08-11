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

public enum Hex {
    ;
    private static final char[] HEX_DIGIT = "0123456789abcdef".toCharArray();
    private static final int PRINTABLE_CHAR_LOW = 31;
    private static final int PRINTABLE_CHAR_HIGH = 127;

    /* Convert an octet buffer into human-friendly data dump */
    public static String formatHexPara(byte[] oct) {
        return formatHexPara(oct, 0, oct.length, new StringBuilder()).toString();
    }

    public static StringBuilder formatHexPara(byte[] oct, StringBuilder sb) {
        return formatHexPara(oct, 0, oct.length, sb);
    }

    public static StringBuilder formatHexPara(final byte[] oct, final int offset, final int count, final StringBuilder sb) {
        return formatHexPara("", oct, offset, count, sb);
    }

        /* Convert an octet buffer into a human-friendly data dump */
    public static StringBuilder formatHexPara(String indent, final byte[] oct, final int offset, final int count, final StringBuilder sb) {
        if (count <= 0) return sb;

        int endIndex = offset + count;
        // calculate the width of index needed for this dump
        final String indexFormat;
        final String indexSpaces;
        {
            int indexWidth = Math.max(4, Integer.toHexString(endIndex).length());
            String unindentedIndexFormat = "%0" + indexWidth + "X:  ";
            indexFormat = indent + unindentedIndexFormat;
            indexSpaces = indent + String.format(unindentedIndexFormat, 0).replaceAll(".", " ");
        }

        sb.append(String.format(indexFormat, offset));

        final StringBuilder ascii = new StringBuilder(18);
        switch (offset % 0x10) {
            case 0xf: sb.append("  ");  ascii.append(" ");
            case 0xe: sb.append("  ");  ascii.append(" ");
            case 0xd: sb.append("  ");  ascii.append(" ");
            case 0xc: sb.append("   "); ascii.append(" ");
            case 0xb: sb.append("  ");  ascii.append(" ");
            case 0xa: sb.append("  ");  ascii.append(" ");
            case 0x9: sb.append("  ");  ascii.append(" ");
            case 0x8: sb.append("   "); ascii.append(" ");
            case 0x7: sb.append("  ");  ascii.append(" ");
            case 0x6: sb.append("  ");  ascii.append(" ");
            case 0x5: sb.append("  ");  ascii.append(" ");
            case 0x4: sb.append("   "); ascii.append(" ");
            case 0x3: sb.append("  ");  ascii.append(" ");
            case 0x2: sb.append("  ");  ascii.append(" ");
            case 0x1: sb.append("  ");  ascii.append(" ");
            case 0x0:
        }

        ascii.append(" \"");

        for (int i = offset; i < endIndex; i++) {
            final int b = oct[i] & 0xff;

            // build up the ascii string for the end of the line
            ascii.append((PRINTABLE_CHAR_LOW < b && b < PRINTABLE_CHAR_HIGH) ? (char) b : '.');

            // print the high hex nybble and the low hex nybble
            sb.append(HEX_DIGIT[b >> 4]).append(HEX_DIGIT[b & 0xf]);

            if (i % 0x4 == (0x4 - 1)) {
                // space the columns on every 4-byte boundary
                sb.append(' ');
                if (i % 0x10 == (0x10 - 1)) {
                    // write the ascii interpretation on the end of every line
                    sb.append(ascii).append("\"");
                    ascii.setLength(0);
                    ascii.append(" \"");
                    // put in a separator line every 0x100 bytes
                    if (i % 0x100 == (0x100 - 1))
                        sb.append("\n").append(indexSpaces).append("-----------------------------------");
                    // add a newline and a new index if the dump continues onto the next line
                    if (i + 1 < endIndex) sb.append("\n").append(String.format(indexFormat, i + 1));
                }
            }
        }

        switch (endIndex % 0x10) {
            case 0x0: break;
            case 0x1: sb.append("  ");
            case 0x2: sb.append("  ");
            case 0x3: sb.append("   ");
            case 0x4: sb.append("  ");
            case 0x5: sb.append("  ");
            case 0x6: sb.append("  ");
            case 0x7: sb.append("   ");
            case 0x8: sb.append("  ");
            case 0x9: sb.append("  ");
            case 0xa: sb.append("  ");
            case 0xb: sb.append("   ");
            case 0xc: sb.append("  ");
            case 0xd: sb.append("  ");
            case 0xe: sb.append("  ");
            case 0xf: sb.append("   ")
                    .append(ascii)
                    .append("\"");
        }

        return sb;
    }

    /* Convert an octet buffer into a single-line readable data dump. */
    public static void formatHexLine(byte[] oct, StringBuilder sb) {
        formatHexLine(oct, 0, oct.length, sb);
    }

    /* Convert an octet buffer into a single-line readable data dump. */
    public static void formatHexLine(byte[] oct, int offset, int count, StringBuilder sb) {
        if (count <= 0) return;
        sb.append('"');
        for (int i = offset; i < offset + count; i++) {
            int n = oct[i] & 0xff;
            sb.append(n >= 32 && n <= 127 ? (char) n : '?');
        }
        sb.append('"');
    }
}

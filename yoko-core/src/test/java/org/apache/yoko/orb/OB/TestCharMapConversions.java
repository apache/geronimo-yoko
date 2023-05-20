/*
 * Copyright 2019 IBM Corporation and others.
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.BitSet;
import java.util.EnumMap;

import static org.apache.yoko.orb.OB.CharMapInfo.CM_IDENTITY;
import static org.apache.yoko.orb.OB.CharMapInfo.CM_8859_2;
import static org.apache.yoko.orb.OB.CharMapInfo.CM_8859_3;
import static org.apache.yoko.orb.OB.CharMapInfo.CM_8859_4;
import static org.apache.yoko.orb.OB.CharMapInfo.CM_8859_5;
import static org.apache.yoko.orb.OB.CharMapInfo.CM_8859_7;
import static org.apache.yoko.orb.OB.CharMapInfo.CM_8859_9;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * This test was developed from an older implementation of CharMapInfo and its now deleted adjunct "database" class.
 * This was done to ensure that none of the character conversions was changed during refactoring.
 *
 * The values tested may be incorrect, since this test was developed to preserve behaviour.
 * The reader is encouraged to correct any errors found and document them here.
 */
public class TestCharMapConversions {
    private static final EnumMap<CharMapInfo, BitSet> auditLog = new EnumMap<>(CharMapInfo.class);

    @BeforeClass
    public static void clearAudit() {
        for (CharMapInfo cmi: CharMapInfo.values()) auditLog.put(cmi, new BitSet());
    }

    private static void logCharTested(CharMapInfo cmi, int otherChar) {
        auditLog.get(cmi).set(otherChar);
    }

    @AfterClass
    public static void checkAllCasesWereCovered() {
        // every enum member should have been tested with every possible character
        BitSet expected = new BitSet();
        expected.set(Character.MIN_VALUE, Character.MAX_VALUE);
        expected.set(Character.MAX_VALUE);
        for (CharMapInfo cmi : CharMapInfo.values()) {
            BitSet actual = auditLog.get(cmi);
            actual.xor(expected);
            assertThat(cmi + " should have had all its char conversions tested", actual, is(new BitSet()));
        }
    }

    private void assertConversion(CharMapInfo cmi, int otherChar, int javaChar) {
        logCharTested(cmi, otherChar);
        String msg = String.format("%s char %x should map to Java char %x", cmi, otherChar, javaChar);
        assertThat(msg, cmi.convertToJava((char)otherChar), is((char)javaChar));
        if (javaChar == 0) return;
        msg = String.format("Java char %x should map to %s char %x", javaChar, cmi, otherChar);
        assertThat(msg, cmi.convertFromJava((char)javaChar), is((char)otherChar));
    }

    private void assertNoConversion(CharMapInfo cmi) {
        assertNoConversion(cmi, Character.MIN_VALUE, Character.MAX_VALUE);
    }

    private void assertNoConversionBefore(CharMapInfo cmi, int limit) {
        assertNoConversion(cmi, Character.MIN_VALUE, limit - 1);
    }

    private void assertNoConversionAfter(CharMapInfo cmi, int limit) {
        assertNoConversion(cmi, limit + 1, Character.MAX_VALUE);
    }

    private void assertNoConversion(CharMapInfo cmi, int startChar, int endChar) {
        for (int i = startChar; i <= endChar; i++) {
            logCharTested(cmi, i);
            String msg = String.format("%s char %x should map to Java char %x", cmi, i, i);
            assertThat(msg, cmi.convertToJava((char) i), is((char) i));
            final int i2 = cmi.convertFromJava((char) i);
            if (i2 < startChar) {
                // just check that javaChar2 converts to i again
                msg = String.format("%s char %x should map to Java char %x", cmi, i2, i);
                assertThat(msg, cmi.convertToJava((char) i2), is((char) i));
            } else {
                msg = String.format("Java char %x should map to %s char %x", i, cmi, i);
                assertThat(msg, i2, is(i));
            }
        }
    }


    @Test
    public void test1() {
        assertNoConversion(CM_IDENTITY);
    }

    @Test
    public void test2() {
        assertNoConversionBefore(CM_8859_2, 0x00a1);
        assertConversion(CM_8859_2, 0x00a1, 0x0104);
        assertConversion(CM_8859_2, 0x00a2, 0x02d8);
        assertConversion(CM_8859_2, 0x00a3, 0x0141);
        assertConversion(CM_8859_2, 0x00a4, 0x00a4);
        assertConversion(CM_8859_2, 0x00a5, 0x013d);
        assertConversion(CM_8859_2, 0x00a6, 0x015a);
        assertConversion(CM_8859_2, 0x00a7, 0x00a7);
        assertConversion(CM_8859_2, 0x00a8, 0x00a8);
        assertConversion(CM_8859_2, 0x00a9, 0x0160);
        assertConversion(CM_8859_2, 0x00aa, 0x015e);
        assertConversion(CM_8859_2, 0x00ab, 0x0164);
        assertConversion(CM_8859_2, 0x00ac, 0x0179);
        assertConversion(CM_8859_2, 0x00ad, 0x00ad);
        assertConversion(CM_8859_2, 0x00ae, 0x017d);
        assertConversion(CM_8859_2, 0x00af, 0x017b);
        assertConversion(CM_8859_2, 0x00b0, 0x00b0);
        assertConversion(CM_8859_2, 0x00b1, 0x0105);
        assertConversion(CM_8859_2, 0x00b2, 0x02db);
        assertConversion(CM_8859_2, 0x00b3, 0x0142);
        assertConversion(CM_8859_2, 0x00b4, 0x00b4);
        assertConversion(CM_8859_2, 0x00b5, 0x013e);
        assertConversion(CM_8859_2, 0x00b6, 0x015b);
        assertConversion(CM_8859_2, 0x00b7, 0x02c7);
        assertConversion(CM_8859_2, 0x00b8, 0x00b8);
        assertConversion(CM_8859_2, 0x00b9, 0x0161);
        assertConversion(CM_8859_2, 0x00ba, 0x015f);
        assertConversion(CM_8859_2, 0x00bb, 0x0165);
        assertConversion(CM_8859_2, 0x00bc, 0x017a);
        assertConversion(CM_8859_2, 0x00bd, 0x02dd);
        assertConversion(CM_8859_2, 0x00be, 0x017e);
        assertConversion(CM_8859_2, 0x00bf, 0x017c);
        assertConversion(CM_8859_2, 0x00c0, 0x0154);
        assertConversion(CM_8859_2, 0x00c1, 0x00c1);
        assertConversion(CM_8859_2, 0x00c2, 0x00c2);
        assertConversion(CM_8859_2, 0x00c3, 0x0102);
        assertConversion(CM_8859_2, 0x00c4, 0x00c4);
        assertConversion(CM_8859_2, 0x00c5, 0x0139);
        assertConversion(CM_8859_2, 0x00c6, 0x0106);
        assertConversion(CM_8859_2, 0x00c7, 0x00c7);
        assertConversion(CM_8859_2, 0x00c8, 0x010c);
        assertConversion(CM_8859_2, 0x00c9, 0x00c9);
        assertConversion(CM_8859_2, 0x00ca, 0x0118);
        assertConversion(CM_8859_2, 0x00cb, 0x00cb);
        assertConversion(CM_8859_2, 0x00cc, 0x011a);
        assertConversion(CM_8859_2, 0x00cd, 0x00cd);
        assertConversion(CM_8859_2, 0x00ce, 0x00ce);
        assertConversion(CM_8859_2, 0x00cf, 0x010e);
        assertConversion(CM_8859_2, 0x00d0, 0x0110);
        assertConversion(CM_8859_2, 0x00d1, 0x0143);
        assertConversion(CM_8859_2, 0x00d2, 0x0147);
        assertConversion(CM_8859_2, 0x00d3, 0x00d3);
        assertConversion(CM_8859_2, 0x00d4, 0x00d4);
        assertConversion(CM_8859_2, 0x00d5, 0x0150);
        assertConversion(CM_8859_2, 0x00d6, 0x00d6);
        assertConversion(CM_8859_2, 0x00d7, 0x00d7);
        assertConversion(CM_8859_2, 0x00d8, 0x0158);
        assertConversion(CM_8859_2, 0x00d9, 0x016e);
        assertConversion(CM_8859_2, 0x00da, 0x00da);
        assertConversion(CM_8859_2, 0x00db, 0x0170);
        assertConversion(CM_8859_2, 0x00dc, 0x00dc);
        assertConversion(CM_8859_2, 0x00dd, 0x00dd);
        assertConversion(CM_8859_2, 0x00de, 0x0162);
        assertConversion(CM_8859_2, 0x00df, 0x00df);
        assertConversion(CM_8859_2, 0x00e0, 0x0155);
        assertConversion(CM_8859_2, 0x00e1, 0x00e1);
        assertConversion(CM_8859_2, 0x00e2, 0x00e2);
        assertConversion(CM_8859_2, 0x00e3, 0x0103);
        assertConversion(CM_8859_2, 0x00e4, 0x00e4);
        assertConversion(CM_8859_2, 0x00e5, 0x013a);
        assertConversion(CM_8859_2, 0x00e6, 0x0107);
        assertConversion(CM_8859_2, 0x00e7, 0x00e7);
        assertConversion(CM_8859_2, 0x00e8, 0x010d);
        assertConversion(CM_8859_2, 0x00e9, 0x00e9);
        assertConversion(CM_8859_2, 0x00ea, 0x0119);
        assertConversion(CM_8859_2, 0x00eb, 0x00eb);
        assertConversion(CM_8859_2, 0x00ec, 0x011b);
        assertConversion(CM_8859_2, 0x00ed, 0x00ed);
        assertConversion(CM_8859_2, 0x00ee, 0x00ee);
        assertConversion(CM_8859_2, 0x00ef, 0x010f);
        assertConversion(CM_8859_2, 0x00f0, 0x0111);
        assertConversion(CM_8859_2, 0x00f1, 0x0144);
        assertConversion(CM_8859_2, 0x00f2, 0x0148);
        assertConversion(CM_8859_2, 0x00f3, 0x00f3);
        assertConversion(CM_8859_2, 0x00f4, 0x00f4);
        assertConversion(CM_8859_2, 0x00f5, 0x0151);
        assertConversion(CM_8859_2, 0x00f6, 0x00f6);
        assertConversion(CM_8859_2, 0x00f7, 0x00f7);
        assertConversion(CM_8859_2, 0x00f8, 0x0159);
        assertConversion(CM_8859_2, 0x00f9, 0x016f);
        assertConversion(CM_8859_2, 0x00fa, 0x00fa);
        assertConversion(CM_8859_2, 0x00fb, 0x0171);
        assertConversion(CM_8859_2, 0x00fc, 0x00fc);
        assertConversion(CM_8859_2, 0x00fd, 0x00fd);
        assertConversion(CM_8859_2, 0x00fe, 0x0163);
        assertConversion(CM_8859_2, 0x00ff, 0x02d9);
        assertNoConversionAfter(CM_8859_2, 0x00ff);
    }

    @Test
    public void test3() {
        assertNoConversionBefore(CM_8859_3, 0x00a1);
        assertConversion(CM_8859_3, 0x00a1, 0x0126);
        assertConversion(CM_8859_3, 0x00a2, 0x02d8);
        assertConversion(CM_8859_3, 0x00a3, 0x00a3);
        assertConversion(CM_8859_3, 0x00a4, 0x00a4);
        assertConversion(CM_8859_3, 0x00a5, 0x0000);
        assertConversion(CM_8859_3, 0x00a6, 0x0124);
        assertConversion(CM_8859_3, 0x00a7, 0x00a7);
        assertConversion(CM_8859_3, 0x00a8, 0x00a8);
        assertConversion(CM_8859_3, 0x00a9, 0x0130);
        assertConversion(CM_8859_3, 0x00aa, 0x015e);
        assertConversion(CM_8859_3, 0x00ab, 0x011e);
        assertConversion(CM_8859_3, 0x00ac, 0x0134);
        assertConversion(CM_8859_3, 0x00ad, 0x00ad);
        assertConversion(CM_8859_3, 0x00ae, 0x0000);
        assertConversion(CM_8859_3, 0x00af, 0x017b);
        assertConversion(CM_8859_3, 0x00b0, 0x00b0);
        assertConversion(CM_8859_3, 0x00b1, 0x0127);
        assertConversion(CM_8859_3, 0x00b2, 0x00b2);
        assertConversion(CM_8859_3, 0x00b3, 0x00b3);
        assertConversion(CM_8859_3, 0x00b4, 0x00b4);
        assertConversion(CM_8859_3, 0x00b5, 0x00b5);
        assertConversion(CM_8859_3, 0x00b6, 0x0125);
        assertConversion(CM_8859_3, 0x00b7, 0x00b7);
        assertConversion(CM_8859_3, 0x00b8, 0x00b8);
        assertConversion(CM_8859_3, 0x00b9, 0x0131);
        assertConversion(CM_8859_3, 0x00ba, 0x015f);
        assertConversion(CM_8859_3, 0x00bb, 0x011f);
        assertConversion(CM_8859_3, 0x00bc, 0x0135);
        assertConversion(CM_8859_3, 0x00bd, 0x00bd);
        assertConversion(CM_8859_3, 0x00be, 0x0000);
        assertConversion(CM_8859_3, 0x00bf, 0x017c);
        assertConversion(CM_8859_3, 0x00c0, 0x00c0);
        assertConversion(CM_8859_3, 0x00c1, 0x00c1);
        assertConversion(CM_8859_3, 0x00c2, 0x00c2);
        assertConversion(CM_8859_3, 0x00c3, 0x0000);
        assertConversion(CM_8859_3, 0x00c4, 0x00c4);
        assertConversion(CM_8859_3, 0x00c5, 0x010a);
        assertConversion(CM_8859_3, 0x00c6, 0x0108);
        assertConversion(CM_8859_3, 0x00c7, 0x00c7);
        assertConversion(CM_8859_3, 0x00c8, 0x00c8);
        assertConversion(CM_8859_3, 0x00c9, 0x00c9);
        assertConversion(CM_8859_3, 0x00ca, 0x00ca);
        assertConversion(CM_8859_3, 0x00cb, 0x00cb);
        assertConversion(CM_8859_3, 0x00cc, 0x00cc);
        assertConversion(CM_8859_3, 0x00cd, 0x00cd);
        assertConversion(CM_8859_3, 0x00ce, 0x00ce);
        assertConversion(CM_8859_3, 0x00cf, 0x00cf);
        assertConversion(CM_8859_3, 0x00d0, 0x0000);
        assertConversion(CM_8859_3, 0x00d1, 0x00d1);
        assertConversion(CM_8859_3, 0x00d2, 0x00d2);
        assertConversion(CM_8859_3, 0x00d3, 0x00d3);
        assertConversion(CM_8859_3, 0x00d4, 0x00d4);
        assertConversion(CM_8859_3, 0x00d5, 0x0120);
        assertConversion(CM_8859_3, 0x00d6, 0x00d6);
        assertConversion(CM_8859_3, 0x00d7, 0x00d7);
        assertConversion(CM_8859_3, 0x00d8, 0x011c);
        assertConversion(CM_8859_3, 0x00d9, 0x00d9);
        assertConversion(CM_8859_3, 0x00da, 0x00da);
        assertConversion(CM_8859_3, 0x00db, 0x00db);
        assertConversion(CM_8859_3, 0x00dc, 0x00dc);
        assertConversion(CM_8859_3, 0x00dd, 0x016c);
        assertConversion(CM_8859_3, 0x00de, 0x015c);
        assertConversion(CM_8859_3, 0x00df, 0x00df);
        assertConversion(CM_8859_3, 0x00e0, 0x00e0);
        assertConversion(CM_8859_3, 0x00e1, 0x00e1);
        assertConversion(CM_8859_3, 0x00e2, 0x00e2);
        assertConversion(CM_8859_3, 0x00e3, 0x0000);
        assertConversion(CM_8859_3, 0x00e4, 0x00e4);
        assertConversion(CM_8859_3, 0x00e5, 0x010b);
        assertConversion(CM_8859_3, 0x00e6, 0x0109);
        assertConversion(CM_8859_3, 0x00e7, 0x00e7);
        assertConversion(CM_8859_3, 0x00e8, 0x00e8);
        assertConversion(CM_8859_3, 0x00e9, 0x00e9);
        assertConversion(CM_8859_3, 0x00ea, 0x00ea);
        assertConversion(CM_8859_3, 0x00eb, 0x00eb);
        assertConversion(CM_8859_3, 0x00ec, 0x00ec);
        assertConversion(CM_8859_3, 0x00ed, 0x00ed);
        assertConversion(CM_8859_3, 0x00ee, 0x00ee);
        assertConversion(CM_8859_3, 0x00ef, 0x00ef);
        assertConversion(CM_8859_3, 0x00f0, 0x0000);
        assertConversion(CM_8859_3, 0x00f1, 0x00f1);
        assertConversion(CM_8859_3, 0x00f2, 0x00f2);
        assertConversion(CM_8859_3, 0x00f3, 0x00f3);
        assertConversion(CM_8859_3, 0x00f4, 0x00f4);
        assertConversion(CM_8859_3, 0x00f5, 0x0121);
        assertConversion(CM_8859_3, 0x00f6, 0x00f6);
        assertConversion(CM_8859_3, 0x00f7, 0x00f7);
        assertConversion(CM_8859_3, 0x00f8, 0x011d);
        assertConversion(CM_8859_3, 0x00f9, 0x00f9);
        assertConversion(CM_8859_3, 0x00fa, 0x00fa);
        assertConversion(CM_8859_3, 0x00fb, 0x00fb);
        assertConversion(CM_8859_3, 0x00fc, 0x00fc);
        assertConversion(CM_8859_3, 0x00fd, 0x016d);
        assertConversion(CM_8859_3, 0x00fe, 0x015d);
        assertConversion(CM_8859_3, 0x00ff, 0x02d9);
        assertNoConversionAfter(CM_8859_3, 0x00ff);
    }

    @Test
    public void test4() {
        assertNoConversionBefore(CM_8859_4, 0x00a1);
        assertConversion(CM_8859_4, 0x00a1, 0x0104);
        assertConversion(CM_8859_4, 0x00a2, 0x0138);
        assertConversion(CM_8859_4, 0x00a3, 0x0156);
        assertConversion(CM_8859_4, 0x00a4, 0x00a4);
        assertConversion(CM_8859_4, 0x00a5, 0x0128);
        assertConversion(CM_8859_4, 0x00a6, 0x013b);
        assertConversion(CM_8859_4, 0x00a7, 0x00a7);
        assertConversion(CM_8859_4, 0x00a8, 0x00a8);
        assertConversion(CM_8859_4, 0x00a9, 0x0160);
        assertConversion(CM_8859_4, 0x00aa, 0x0112);
        assertConversion(CM_8859_4, 0x00ab, 0x0122);
        assertConversion(CM_8859_4, 0x00ac, 0x0166);
        assertConversion(CM_8859_4, 0x00ad, 0x00ad);
        assertConversion(CM_8859_4, 0x00ae, 0x017d);
        assertConversion(CM_8859_4, 0x00af, 0x00af);
        assertConversion(CM_8859_4, 0x00b0, 0x00b0);
        assertConversion(CM_8859_4, 0x00b1, 0x0105);
        assertConversion(CM_8859_4, 0x00b2, 0x02db);
        assertConversion(CM_8859_4, 0x00b3, 0x0157);
        assertConversion(CM_8859_4, 0x00b4, 0x00b4);
        assertConversion(CM_8859_4, 0x00b5, 0x0129);
        assertConversion(CM_8859_4, 0x00b6, 0x013c);
        assertConversion(CM_8859_4, 0x00b7, 0x02c7);
        assertConversion(CM_8859_4, 0x00b8, 0x00b8);
        assertConversion(CM_8859_4, 0x00b9, 0x0161);
        assertConversion(CM_8859_4, 0x00ba, 0x0113);
        assertConversion(CM_8859_4, 0x00bb, 0x0123);
        assertConversion(CM_8859_4, 0x00bc, 0x0167);
        assertConversion(CM_8859_4, 0x00bd, 0x014a);
        assertConversion(CM_8859_4, 0x00be, 0x017e);
        assertConversion(CM_8859_4, 0x00bf, 0x014b);
        assertConversion(CM_8859_4, 0x00c0, 0x0100);
        assertConversion(CM_8859_4, 0x00c1, 0x00c1);
        assertConversion(CM_8859_4, 0x00c2, 0x00c2);
        assertConversion(CM_8859_4, 0x00c3, 0x00c3);
        assertConversion(CM_8859_4, 0x00c4, 0x00c4);
        assertConversion(CM_8859_4, 0x00c5, 0x00c5);
        assertConversion(CM_8859_4, 0x00c6, 0x00c6);
        assertConversion(CM_8859_4, 0x00c7, 0x012e);
        assertConversion(CM_8859_4, 0x00c8, 0x010c);
        assertConversion(CM_8859_4, 0x00c9, 0x00c9);
        assertConversion(CM_8859_4, 0x00ca, 0x0118);
        assertConversion(CM_8859_4, 0x00cb, 0x00cb);
        assertConversion(CM_8859_4, 0x00cc, 0x0116);
        assertConversion(CM_8859_4, 0x00cd, 0x00cd);
        assertConversion(CM_8859_4, 0x00ce, 0x00ce);
        assertConversion(CM_8859_4, 0x00cf, 0x012a);
        assertConversion(CM_8859_4, 0x00d0, 0x0110);
        assertConversion(CM_8859_4, 0x00d1, 0x0145);
        assertConversion(CM_8859_4, 0x00d2, 0x014c);
        assertConversion(CM_8859_4, 0x00d3, 0x0136);
        assertConversion(CM_8859_4, 0x00d4, 0x00d4);
        assertConversion(CM_8859_4, 0x00d5, 0x00d5);
        assertConversion(CM_8859_4, 0x00d6, 0x00d6);
        assertConversion(CM_8859_4, 0x00d7, 0x00d7);
        assertConversion(CM_8859_4, 0x00d8, 0x00d8);
        assertConversion(CM_8859_4, 0x00d9, 0x0172);
        assertConversion(CM_8859_4, 0x00da, 0x00da);
        assertConversion(CM_8859_4, 0x00db, 0x00db);
        assertConversion(CM_8859_4, 0x00dc, 0x00dc);
        assertConversion(CM_8859_4, 0x00dd, 0x0168);
        assertConversion(CM_8859_4, 0x00de, 0x016a);
        assertConversion(CM_8859_4, 0x00df, 0x00df);
        assertConversion(CM_8859_4, 0x00e0, 0x0101);
        assertConversion(CM_8859_4, 0x00e1, 0x00e1);
        assertConversion(CM_8859_4, 0x00e2, 0x00e2);
        assertConversion(CM_8859_4, 0x00e3, 0x00e3);
        assertConversion(CM_8859_4, 0x00e4, 0x00e4);
        assertConversion(CM_8859_4, 0x00e5, 0x00e5);
        assertConversion(CM_8859_4, 0x00e6, 0x00e6);
        assertConversion(CM_8859_4, 0x00e7, 0x012f);
        assertConversion(CM_8859_4, 0x00e8, 0x010d);
        assertConversion(CM_8859_4, 0x00e9, 0x00e9);
        assertConversion(CM_8859_4, 0x00ea, 0x0119);
        assertConversion(CM_8859_4, 0x00eb, 0x00eb);
        assertConversion(CM_8859_4, 0x00ec, 0x0117);
        assertConversion(CM_8859_4, 0x00ed, 0x00ed);
        assertConversion(CM_8859_4, 0x00ee, 0x00ee);
        assertConversion(CM_8859_4, 0x00ef, 0x012b);
        assertConversion(CM_8859_4, 0x00f0, 0x0111);
        assertConversion(CM_8859_4, 0x00f1, 0x0146);
        assertConversion(CM_8859_4, 0x00f2, 0x014d);
        assertConversion(CM_8859_4, 0x00f3, 0x0137);
        assertConversion(CM_8859_4, 0x00f4, 0x00f4);
        assertConversion(CM_8859_4, 0x00f5, 0x00f5);
        assertConversion(CM_8859_4, 0x00f6, 0x00f6);
        assertConversion(CM_8859_4, 0x00f7, 0x00f7);
        assertConversion(CM_8859_4, 0x00f8, 0x00f8);
        assertConversion(CM_8859_4, 0x00f9, 0x0173);
        assertConversion(CM_8859_4, 0x00fa, 0x00fa);
        assertConversion(CM_8859_4, 0x00fb, 0x00fb);
        assertConversion(CM_8859_4, 0x00fc, 0x00fc);
        assertConversion(CM_8859_4, 0x00fd, 0x0169);
        assertConversion(CM_8859_4, 0x00fe, 0x016b);
        assertConversion(CM_8859_4, 0x00ff, 0x02d9);
        assertNoConversionAfter(CM_8859_4, 0x00ff);
    }

    @Test
    public void test5() {
        assertNoConversionBefore(CM_8859_5, 0x00a1);
        assertConversion(CM_8859_5, 0x00a1, 0x0401);
        assertConversion(CM_8859_5, 0x00a2, 0x0402);
        assertConversion(CM_8859_5, 0x00a3, 0x0403);
        assertConversion(CM_8859_5, 0x00a4, 0x0404);
        assertConversion(CM_8859_5, 0x00a5, 0x0405);
        assertConversion(CM_8859_5, 0x00a6, 0x0406);
        assertConversion(CM_8859_5, 0x00a7, 0x0407);
        assertConversion(CM_8859_5, 0x00a8, 0x0408);
        assertConversion(CM_8859_5, 0x00a9, 0x0409);
        assertConversion(CM_8859_5, 0x00aa, 0x040a);
        assertConversion(CM_8859_5, 0x00ab, 0x040b);
        assertConversion(CM_8859_5, 0x00ac, 0x040c);
        assertConversion(CM_8859_5, 0x00ad, 0x00ad);
        assertConversion(CM_8859_5, 0x00ae, 0x040e);
        assertConversion(CM_8859_5, 0x00af, 0x040f);
        assertConversion(CM_8859_5, 0x00b0, 0x0410);
        assertConversion(CM_8859_5, 0x00b1, 0x0411);
        assertConversion(CM_8859_5, 0x00b2, 0x0412);
        assertConversion(CM_8859_5, 0x00b3, 0x0413);
        assertConversion(CM_8859_5, 0x00b4, 0x0414);
        assertConversion(CM_8859_5, 0x00b5, 0x0415);
        assertConversion(CM_8859_5, 0x00b6, 0x0416);
        assertConversion(CM_8859_5, 0x00b7, 0x0417);
        assertConversion(CM_8859_5, 0x00b8, 0x0418);
        assertConversion(CM_8859_5, 0x00b9, 0x0419);
        assertConversion(CM_8859_5, 0x00ba, 0x041a);
        assertConversion(CM_8859_5, 0x00bb, 0x041b);
        assertConversion(CM_8859_5, 0x00bc, 0x041c);
        assertConversion(CM_8859_5, 0x00bd, 0x041d);
        assertConversion(CM_8859_5, 0x00be, 0x041e);
        assertConversion(CM_8859_5, 0x00bf, 0x041f);
        assertConversion(CM_8859_5, 0x00c0, 0x0420);
        assertConversion(CM_8859_5, 0x00c1, 0x0421);
        assertConversion(CM_8859_5, 0x00c2, 0x0422);
        assertConversion(CM_8859_5, 0x00c3, 0x0423);
        assertConversion(CM_8859_5, 0x00c4, 0x0424);
        assertConversion(CM_8859_5, 0x00c5, 0x0425);
        assertConversion(CM_8859_5, 0x00c6, 0x0426);
        assertConversion(CM_8859_5, 0x00c7, 0x0427);
        assertConversion(CM_8859_5, 0x00c8, 0x0428);
        assertConversion(CM_8859_5, 0x00c9, 0x0429);
        assertConversion(CM_8859_5, 0x00ca, 0x042a);
        assertConversion(CM_8859_5, 0x00cb, 0x042b);
        assertConversion(CM_8859_5, 0x00cc, 0x042c);
        assertConversion(CM_8859_5, 0x00cd, 0x042d);
        assertConversion(CM_8859_5, 0x00ce, 0x042e);
        assertConversion(CM_8859_5, 0x00cf, 0x042f);
        assertConversion(CM_8859_5, 0x00d0, 0x0430);
        assertConversion(CM_8859_5, 0x00d1, 0x0431);
        assertConversion(CM_8859_5, 0x00d2, 0x0432);
        assertConversion(CM_8859_5, 0x00d3, 0x0433);
        assertConversion(CM_8859_5, 0x00d4, 0x0434);
        assertConversion(CM_8859_5, 0x00d5, 0x0435);
        assertConversion(CM_8859_5, 0x00d6, 0x0436);
        assertConversion(CM_8859_5, 0x00d7, 0x0437);
        assertConversion(CM_8859_5, 0x00d8, 0x0438);
        assertConversion(CM_8859_5, 0x00d9, 0x0439);
        assertConversion(CM_8859_5, 0x00da, 0x043a);
        assertConversion(CM_8859_5, 0x00db, 0x043b);
        assertConversion(CM_8859_5, 0x00dc, 0x043c);
        assertConversion(CM_8859_5, 0x00dd, 0x043d);
        assertConversion(CM_8859_5, 0x00de, 0x043e);
        assertConversion(CM_8859_5, 0x00df, 0x043f);
        assertConversion(CM_8859_5, 0x00e0, 0x0440);
        assertConversion(CM_8859_5, 0x00e1, 0x0441);
        assertConversion(CM_8859_5, 0x00e2, 0x0442);
        assertConversion(CM_8859_5, 0x00e3, 0x0443);
        assertConversion(CM_8859_5, 0x00e4, 0x0444);
        assertConversion(CM_8859_5, 0x00e5, 0x0445);
        assertConversion(CM_8859_5, 0x00e6, 0x0446);
        assertConversion(CM_8859_5, 0x00e7, 0x0447);
        assertConversion(CM_8859_5, 0x00e8, 0x0448);
        assertConversion(CM_8859_5, 0x00e9, 0x0449);
        assertConversion(CM_8859_5, 0x00ea, 0x044a);
        assertConversion(CM_8859_5, 0x00eb, 0x044b);
        assertConversion(CM_8859_5, 0x00ec, 0x044c);
        assertConversion(CM_8859_5, 0x00ed, 0x044d);
        assertConversion(CM_8859_5, 0x00ee, 0x044e);
        assertConversion(CM_8859_5, 0x00ef, 0x044f);
        assertConversion(CM_8859_5, 0x00f0, 0x2116);
        assertConversion(CM_8859_5, 0x00f1, 0x0451);
        assertConversion(CM_8859_5, 0x00f2, 0x0452);
        assertConversion(CM_8859_5, 0x00f3, 0x0453);
        assertConversion(CM_8859_5, 0x00f4, 0x0454);
        assertConversion(CM_8859_5, 0x00f5, 0x0455);
        assertConversion(CM_8859_5, 0x00f6, 0x0456);
        assertConversion(CM_8859_5, 0x00f7, 0x0457);
        assertConversion(CM_8859_5, 0x00f8, 0x0458);
        assertConversion(CM_8859_5, 0x00f9, 0x0459);
        assertConversion(CM_8859_5, 0x00fa, 0x045a);
        assertConversion(CM_8859_5, 0x00fb, 0x045b);
        assertConversion(CM_8859_5, 0x00fc, 0x045c);
        assertConversion(CM_8859_5, 0x00fd, 0x00a7);
        assertConversion(CM_8859_5, 0x00fe, 0x045e);
        assertConversion(CM_8859_5, 0x00ff, 0x045f);
        assertNoConversionAfter(CM_8859_5, 0x00ff);
    }

    @Test
    public void test7() {
        assertNoConversionBefore(CM_8859_7, 0x00a1);
        assertConversion(CM_8859_7, 0x00a1, 0x2018);
        assertConversion(CM_8859_7, 0x00a2, 0x2019);
        assertConversion(CM_8859_7, 0x00a3, 0x00a3);
        assertConversion(CM_8859_7, 0x00a4, 0x0000);
        assertConversion(CM_8859_7, 0x00a5, 0x0000);
        assertConversion(CM_8859_7, 0x00a6, 0x00a6);
        assertConversion(CM_8859_7, 0x00a7, 0x00a7);
        assertConversion(CM_8859_7, 0x00a8, 0x00a8);
        assertConversion(CM_8859_7, 0x00a9, 0x00a9);
        assertConversion(CM_8859_7, 0x00aa, 0x0000);
        assertConversion(CM_8859_7, 0x00ab, 0x00ab);
        assertConversion(CM_8859_7, 0x00ac, 0x00ac);
        assertConversion(CM_8859_7, 0x00ad, 0x00ad);
        assertConversion(CM_8859_7, 0x00ae, 0x0000);
        assertConversion(CM_8859_7, 0x00af, 0x2015);
        assertConversion(CM_8859_7, 0x00b0, 0x00b0);
        assertConversion(CM_8859_7, 0x00b1, 0x00b1);
        assertConversion(CM_8859_7, 0x00b2, 0x00b2);
        assertConversion(CM_8859_7, 0x00b3, 0x00b3);
        assertConversion(CM_8859_7, 0x00b4, 0x0384);
        assertConversion(CM_8859_7, 0x00b5, 0x0385);
        assertConversion(CM_8859_7, 0x00b6, 0x0386);
        assertConversion(CM_8859_7, 0x00b7, 0x00b7);
        assertConversion(CM_8859_7, 0x00b8, 0x0388);
        assertConversion(CM_8859_7, 0x00b9, 0x0389);
        assertConversion(CM_8859_7, 0x00ba, 0x038a);
        assertConversion(CM_8859_7, 0x00bb, 0x00bb);
        assertConversion(CM_8859_7, 0x00bc, 0x038c);
        assertConversion(CM_8859_7, 0x00bd, 0x00bd);
        assertConversion(CM_8859_7, 0x00be, 0x038e);
        assertConversion(CM_8859_7, 0x00bf, 0x038f);
        assertConversion(CM_8859_7, 0x00c0, 0x0390);
        assertConversion(CM_8859_7, 0x00c1, 0x0391);
        assertConversion(CM_8859_7, 0x00c2, 0x0392);
        assertConversion(CM_8859_7, 0x00c3, 0x0393);
        assertConversion(CM_8859_7, 0x00c4, 0x0394);
        assertConversion(CM_8859_7, 0x00c5, 0x0395);
        assertConversion(CM_8859_7, 0x00c6, 0x0396);
        assertConversion(CM_8859_7, 0x00c7, 0x0397);
        assertConversion(CM_8859_7, 0x00c8, 0x0398);
        assertConversion(CM_8859_7, 0x00c9, 0x0399);
        assertConversion(CM_8859_7, 0x00ca, 0x039a);
        assertConversion(CM_8859_7, 0x00cb, 0x039b);
        assertConversion(CM_8859_7, 0x00cc, 0x039c);
        assertConversion(CM_8859_7, 0x00cd, 0x039d);
        assertConversion(CM_8859_7, 0x00ce, 0x039e);
        assertConversion(CM_8859_7, 0x00cf, 0x039f);
        assertConversion(CM_8859_7, 0x00d0, 0x03a0);
        assertConversion(CM_8859_7, 0x00d1, 0x03a1);
        assertConversion(CM_8859_7, 0x00d2, 0x0000);
        assertConversion(CM_8859_7, 0x00d3, 0x03a3);
        assertConversion(CM_8859_7, 0x00d4, 0x03a4);
        assertConversion(CM_8859_7, 0x00d5, 0x03a5);
        assertConversion(CM_8859_7, 0x00d6, 0x03a6);
        assertConversion(CM_8859_7, 0x00d7, 0x03a7);
        assertConversion(CM_8859_7, 0x00d8, 0x03a8);
        assertConversion(CM_8859_7, 0x00d9, 0x03a9);
        assertConversion(CM_8859_7, 0x00da, 0x03aa);
        assertConversion(CM_8859_7, 0x00db, 0x03ab);
        assertConversion(CM_8859_7, 0x00dc, 0x03ac);
        assertConversion(CM_8859_7, 0x00dd, 0x03ad);
        assertConversion(CM_8859_7, 0x00de, 0x03ae);
        assertConversion(CM_8859_7, 0x00df, 0x03af);
        assertConversion(CM_8859_7, 0x00e0, 0x03b0);
        assertConversion(CM_8859_7, 0x00e1, 0x03b1);
        assertConversion(CM_8859_7, 0x00e2, 0x03b2);
        assertConversion(CM_8859_7, 0x00e3, 0x03b3);
        assertConversion(CM_8859_7, 0x00e4, 0x03b4);
        assertConversion(CM_8859_7, 0x00e5, 0x03b5);
        assertConversion(CM_8859_7, 0x00e6, 0x03b6);
        assertConversion(CM_8859_7, 0x00e7, 0x03b7);
        assertConversion(CM_8859_7, 0x00e8, 0x03b8);
        assertConversion(CM_8859_7, 0x00e9, 0x03b9);
        assertConversion(CM_8859_7, 0x00ea, 0x03ba);
        assertConversion(CM_8859_7, 0x00eb, 0x03bb);
        assertConversion(CM_8859_7, 0x00ec, 0x03bc);
        assertConversion(CM_8859_7, 0x00ed, 0x03bd);
        assertConversion(CM_8859_7, 0x00ee, 0x03be);
        assertConversion(CM_8859_7, 0x00ef, 0x03bf);
        assertConversion(CM_8859_7, 0x00f0, 0x03c0);
        assertConversion(CM_8859_7, 0x00f1, 0x03c1);
        assertConversion(CM_8859_7, 0x00f2, 0x03c2);
        assertConversion(CM_8859_7, 0x00f3, 0x03c3);
        assertConversion(CM_8859_7, 0x00f4, 0x03c4);
        assertConversion(CM_8859_7, 0x00f5, 0x03c5);
        assertConversion(CM_8859_7, 0x00f6, 0x03c6);
        assertConversion(CM_8859_7, 0x00f7, 0x03c7);
        assertConversion(CM_8859_7, 0x00f8, 0x03c8);
        assertConversion(CM_8859_7, 0x00f9, 0x03c9);
        assertConversion(CM_8859_7, 0x00fa, 0x03ca);
        assertConversion(CM_8859_7, 0x00fb, 0x03cb);
        assertConversion(CM_8859_7, 0x00fc, 0x03cc);
        assertConversion(CM_8859_7, 0x00fd, 0x03cd);
        assertConversion(CM_8859_7, 0x00fe, 0x03ce);
        assertNoConversionAfter(CM_8859_7, 0x00fe);
    }

    @Test
    public void test9() {
        assertNoConversionBefore(CM_8859_9, 0x00d0);
        assertConversion(CM_8859_9, 0x00d0, 0x011e);
        assertConversion(CM_8859_9, 0x00d1, 0x00d1);
        assertConversion(CM_8859_9, 0x00d2, 0x00d2);
        assertConversion(CM_8859_9, 0x00d3, 0x00d3);
        assertConversion(CM_8859_9, 0x00d4, 0x00d4);
        assertConversion(CM_8859_9, 0x00d5, 0x00d5);
        assertConversion(CM_8859_9, 0x00d6, 0x00d6);
        assertConversion(CM_8859_9, 0x00d7, 0x00d7);
        assertConversion(CM_8859_9, 0x00d8, 0x00d8);
        assertConversion(CM_8859_9, 0x00d9, 0x00d9);
        assertConversion(CM_8859_9, 0x00da, 0x00da);
        assertConversion(CM_8859_9, 0x00db, 0x00db);
        assertConversion(CM_8859_9, 0x00dc, 0x00dc);
        assertConversion(CM_8859_9, 0x00dd, 0x0130);
        assertConversion(CM_8859_9, 0x00de, 0x015e);
        assertConversion(CM_8859_9, 0x00df, 0x00df);
        assertConversion(CM_8859_9, 0x00e0, 0x00e0);
        assertConversion(CM_8859_9, 0x00e1, 0x00e1);
        assertConversion(CM_8859_9, 0x00e2, 0x00e2);
        assertConversion(CM_8859_9, 0x00e3, 0x00e3);
        assertConversion(CM_8859_9, 0x00e4, 0x00e4);
        assertConversion(CM_8859_9, 0x00e5, 0x00e5);
        assertConversion(CM_8859_9, 0x00e6, 0x00e6);
        assertConversion(CM_8859_9, 0x00e7, 0x00e7);
        assertConversion(CM_8859_9, 0x00e8, 0x00e8);
        assertConversion(CM_8859_9, 0x00e9, 0x00e9);
        assertConversion(CM_8859_9, 0x00ea, 0x00ea);
        assertConversion(CM_8859_9, 0x00eb, 0x00eb);
        assertConversion(CM_8859_9, 0x00ec, 0x00ec);
        assertConversion(CM_8859_9, 0x00ed, 0x00ed);
        assertConversion(CM_8859_9, 0x00ee, 0x00ee);
        assertConversion(CM_8859_9, 0x00ef, 0x00ef);
        assertConversion(CM_8859_9, 0x00f0, 0x011f);
        assertConversion(CM_8859_9, 0x00f1, 0x00f1);
        assertConversion(CM_8859_9, 0x00f2, 0x00f2);
        assertConversion(CM_8859_9, 0x00f3, 0x00f3);
        assertConversion(CM_8859_9, 0x00f4, 0x00f4);
        assertConversion(CM_8859_9, 0x00f5, 0x00f5);
        assertConversion(CM_8859_9, 0x00f6, 0x00f6);
        assertConversion(CM_8859_9, 0x00f7, 0x00f7);
        assertConversion(CM_8859_9, 0x00f8, 0x00f8);
        assertConversion(CM_8859_9, 0x00f9, 0x00f9);
        assertConversion(CM_8859_9, 0x00fa, 0x00fa);
        assertConversion(CM_8859_9, 0x00fb, 0x00fb);
        assertConversion(CM_8859_9, 0x00fc, 0x00fc);
        assertConversion(CM_8859_9, 0x00fd, 0x0131);
        assertConversion(CM_8859_9, 0x00fe, 0x015f);
        assertNoConversionAfter(CM_8859_9, 0x00fe);
    }
}

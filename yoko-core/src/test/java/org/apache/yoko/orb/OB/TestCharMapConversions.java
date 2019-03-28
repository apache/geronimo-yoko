/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package org.apache.yoko.orb.OB;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.BitSet;
import java.util.EnumMap;

import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_2;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_3;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_4;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_5;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_7;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_9;
import static org.apache.yoko.orb.OB.CharMapInfo.PCS;
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
    public void testLatin_2() {
        assertNoConversionBefore(LATIN_2, 0x00a1);
        assertConversion(LATIN_2, 0x00a1, 0x0104);
        assertConversion(LATIN_2, 0x00a2, 0x02d8);
        assertConversion(LATIN_2, 0x00a3, 0x0141);
        assertConversion(LATIN_2, 0x00a4, 0x00a4);
        assertConversion(LATIN_2, 0x00a5, 0x013d);
        assertConversion(LATIN_2, 0x00a6, 0x015a);
        assertConversion(LATIN_2, 0x00a7, 0x00a7);
        assertConversion(LATIN_2, 0x00a8, 0x00a8);
        assertConversion(LATIN_2, 0x00a9, 0x0160);
        assertConversion(LATIN_2, 0x00aa, 0x015e);
        assertConversion(LATIN_2, 0x00ab, 0x0164);
        assertConversion(LATIN_2, 0x00ac, 0x0179);
        assertConversion(LATIN_2, 0x00ad, 0x00ad);
        assertConversion(LATIN_2, 0x00ae, 0x017d);
        assertConversion(LATIN_2, 0x00af, 0x017b);
        assertConversion(LATIN_2, 0x00b0, 0x00b0);
        assertConversion(LATIN_2, 0x00b1, 0x0105);
        assertConversion(LATIN_2, 0x00b2, 0x02db);
        assertConversion(LATIN_2, 0x00b3, 0x0142);
        assertConversion(LATIN_2, 0x00b4, 0x00b4);
        assertConversion(LATIN_2, 0x00b5, 0x013e);
        assertConversion(LATIN_2, 0x00b6, 0x015b);
        assertConversion(LATIN_2, 0x00b7, 0x02c7);
        assertConversion(LATIN_2, 0x00b8, 0x00b8);
        assertConversion(LATIN_2, 0x00b9, 0x0161);
        assertConversion(LATIN_2, 0x00ba, 0x015f);
        assertConversion(LATIN_2, 0x00bb, 0x0165);
        assertConversion(LATIN_2, 0x00bc, 0x017a);
        assertConversion(LATIN_2, 0x00bd, 0x02dd);
        assertConversion(LATIN_2, 0x00be, 0x017e);
        assertConversion(LATIN_2, 0x00bf, 0x017c);
        assertConversion(LATIN_2, 0x00c0, 0x0154);
        assertConversion(LATIN_2, 0x00c1, 0x00c1);
        assertConversion(LATIN_2, 0x00c2, 0x00c2);
        assertConversion(LATIN_2, 0x00c3, 0x0102);
        assertConversion(LATIN_2, 0x00c4, 0x00c4);
        assertConversion(LATIN_2, 0x00c5, 0x0139);
        assertConversion(LATIN_2, 0x00c6, 0x0106);
        assertConversion(LATIN_2, 0x00c7, 0x00c7);
        assertConversion(LATIN_2, 0x00c8, 0x010c);
        assertConversion(LATIN_2, 0x00c9, 0x00c9);
        assertConversion(LATIN_2, 0x00ca, 0x0118);
        assertConversion(LATIN_2, 0x00cb, 0x00cb);
        assertConversion(LATIN_2, 0x00cc, 0x011a);
        assertConversion(LATIN_2, 0x00cd, 0x00cd);
        assertConversion(LATIN_2, 0x00ce, 0x00ce);
        assertConversion(LATIN_2, 0x00cf, 0x010e);
        assertConversion(LATIN_2, 0x00d0, 0x0110);
        assertConversion(LATIN_2, 0x00d1, 0x0143);
        assertConversion(LATIN_2, 0x00d2, 0x0147);
        assertConversion(LATIN_2, 0x00d3, 0x00d3);
        assertConversion(LATIN_2, 0x00d4, 0x00d4);
        assertConversion(LATIN_2, 0x00d5, 0x0150);
        assertConversion(LATIN_2, 0x00d6, 0x00d6);
        assertConversion(LATIN_2, 0x00d7, 0x00d7);
        assertConversion(LATIN_2, 0x00d8, 0x0158);
        assertConversion(LATIN_2, 0x00d9, 0x016e);
        assertConversion(LATIN_2, 0x00da, 0x00da);
        assertConversion(LATIN_2, 0x00db, 0x0170);
        assertConversion(LATIN_2, 0x00dc, 0x00dc);
        assertConversion(LATIN_2, 0x00dd, 0x00dd);
        assertConversion(LATIN_2, 0x00de, 0x0162);
        assertConversion(LATIN_2, 0x00df, 0x00df);
        assertConversion(LATIN_2, 0x00e0, 0x0155);
        assertConversion(LATIN_2, 0x00e1, 0x00e1);
        assertConversion(LATIN_2, 0x00e2, 0x00e2);
        assertConversion(LATIN_2, 0x00e3, 0x0103);
        assertConversion(LATIN_2, 0x00e4, 0x00e4);
        assertConversion(LATIN_2, 0x00e5, 0x013a);
        assertConversion(LATIN_2, 0x00e6, 0x0107);
        assertConversion(LATIN_2, 0x00e7, 0x00e7);
        assertConversion(LATIN_2, 0x00e8, 0x010d);
        assertConversion(LATIN_2, 0x00e9, 0x00e9);
        assertConversion(LATIN_2, 0x00ea, 0x0119);
        assertConversion(LATIN_2, 0x00eb, 0x00eb);
        assertConversion(LATIN_2, 0x00ec, 0x011b);
        assertConversion(LATIN_2, 0x00ed, 0x00ed);
        assertConversion(LATIN_2, 0x00ee, 0x00ee);
        assertConversion(LATIN_2, 0x00ef, 0x010f);
        assertConversion(LATIN_2, 0x00f0, 0x0111);
        assertConversion(LATIN_2, 0x00f1, 0x0144);
        assertConversion(LATIN_2, 0x00f2, 0x0148);
        assertConversion(LATIN_2, 0x00f3, 0x00f3);
        assertConversion(LATIN_2, 0x00f4, 0x00f4);
        assertConversion(LATIN_2, 0x00f5, 0x0151);
        assertConversion(LATIN_2, 0x00f6, 0x00f6);
        assertConversion(LATIN_2, 0x00f7, 0x00f7);
        assertConversion(LATIN_2, 0x00f8, 0x0159);
        assertConversion(LATIN_2, 0x00f9, 0x016f);
        assertConversion(LATIN_2, 0x00fa, 0x00fa);
        assertConversion(LATIN_2, 0x00fb, 0x0171);
        assertConversion(LATIN_2, 0x00fc, 0x00fc);
        assertConversion(LATIN_2, 0x00fd, 0x00fd);
        assertConversion(LATIN_2, 0x00fe, 0x0163);
        assertConversion(LATIN_2, 0x00ff, 0x02d9);
        assertNoConversionAfter(LATIN_2, 0x00ff);
    }

    @Test
    public void testLatin_3() {
        assertNoConversionBefore(LATIN_3, 0x00a1);
        assertConversion(LATIN_3, 0x00a1, 0x0126);
        assertConversion(LATIN_3, 0x00a2, 0x02d8);
        assertConversion(LATIN_3, 0x00a3, 0x00a3);
        assertConversion(LATIN_3, 0x00a4, 0x00a4);
        assertConversion(LATIN_3, 0x00a5, 0x0000);
        assertConversion(LATIN_3, 0x00a6, 0x0124);
        assertConversion(LATIN_3, 0x00a7, 0x00a7);
        assertConversion(LATIN_3, 0x00a8, 0x00a8);
        assertConversion(LATIN_3, 0x00a9, 0x0130);
        assertConversion(LATIN_3, 0x00aa, 0x015e);
        assertConversion(LATIN_3, 0x00ab, 0x011e);
        assertConversion(LATIN_3, 0x00ac, 0x0134);
        assertConversion(LATIN_3, 0x00ad, 0x00ad);
        assertConversion(LATIN_3, 0x00ae, 0x0000);
        assertConversion(LATIN_3, 0x00af, 0x017b);
        assertConversion(LATIN_3, 0x00b0, 0x00b0);
        assertConversion(LATIN_3, 0x00b1, 0x0127);
        assertConversion(LATIN_3, 0x00b2, 0x00b2);
        assertConversion(LATIN_3, 0x00b3, 0x00b3);
        assertConversion(LATIN_3, 0x00b4, 0x00b4);
        assertConversion(LATIN_3, 0x00b5, 0x00b5);
        assertConversion(LATIN_3, 0x00b6, 0x0125);
        assertConversion(LATIN_3, 0x00b7, 0x00b7);
        assertConversion(LATIN_3, 0x00b8, 0x00b8);
        assertConversion(LATIN_3, 0x00b9, 0x0131);
        assertConversion(LATIN_3, 0x00ba, 0x015f);
        assertConversion(LATIN_3, 0x00bb, 0x011f);
        assertConversion(LATIN_3, 0x00bc, 0x0135);
        assertConversion(LATIN_3, 0x00bd, 0x00bd);
        assertConversion(LATIN_3, 0x00be, 0x0000);
        assertConversion(LATIN_3, 0x00bf, 0x017c);
        assertConversion(LATIN_3, 0x00c0, 0x00c0);
        assertConversion(LATIN_3, 0x00c1, 0x00c1);
        assertConversion(LATIN_3, 0x00c2, 0x00c2);
        assertConversion(LATIN_3, 0x00c3, 0x0000);
        assertConversion(LATIN_3, 0x00c4, 0x00c4);
        assertConversion(LATIN_3, 0x00c5, 0x010a);
        assertConversion(LATIN_3, 0x00c6, 0x0108);
        assertConversion(LATIN_3, 0x00c7, 0x00c7);
        assertConversion(LATIN_3, 0x00c8, 0x00c8);
        assertConversion(LATIN_3, 0x00c9, 0x00c9);
        assertConversion(LATIN_3, 0x00ca, 0x00ca);
        assertConversion(LATIN_3, 0x00cb, 0x00cb);
        assertConversion(LATIN_3, 0x00cc, 0x00cc);
        assertConversion(LATIN_3, 0x00cd, 0x00cd);
        assertConversion(LATIN_3, 0x00ce, 0x00ce);
        assertConversion(LATIN_3, 0x00cf, 0x00cf);
        assertConversion(LATIN_3, 0x00d0, 0x0000);
        assertConversion(LATIN_3, 0x00d1, 0x00d1);
        assertConversion(LATIN_3, 0x00d2, 0x00d2);
        assertConversion(LATIN_3, 0x00d3, 0x00d3);
        assertConversion(LATIN_3, 0x00d4, 0x00d4);
        assertConversion(LATIN_3, 0x00d5, 0x0120);
        assertConversion(LATIN_3, 0x00d6, 0x00d6);
        assertConversion(LATIN_3, 0x00d7, 0x00d7);
        assertConversion(LATIN_3, 0x00d8, 0x011c);
        assertConversion(LATIN_3, 0x00d9, 0x00d9);
        assertConversion(LATIN_3, 0x00da, 0x00da);
        assertConversion(LATIN_3, 0x00db, 0x00db);
        assertConversion(LATIN_3, 0x00dc, 0x00dc);
        assertConversion(LATIN_3, 0x00dd, 0x016c);
        assertConversion(LATIN_3, 0x00de, 0x015c);
        assertConversion(LATIN_3, 0x00df, 0x00df);
        assertConversion(LATIN_3, 0x00e0, 0x00e0);
        assertConversion(LATIN_3, 0x00e1, 0x00e1);
        assertConversion(LATIN_3, 0x00e2, 0x00e2);
        assertConversion(LATIN_3, 0x00e3, 0x0000);
        assertConversion(LATIN_3, 0x00e4, 0x00e4);
        assertConversion(LATIN_3, 0x00e5, 0x010b);
        assertConversion(LATIN_3, 0x00e6, 0x0109);
        assertConversion(LATIN_3, 0x00e7, 0x00e7);
        assertConversion(LATIN_3, 0x00e8, 0x00e8);
        assertConversion(LATIN_3, 0x00e9, 0x00e9);
        assertConversion(LATIN_3, 0x00ea, 0x00ea);
        assertConversion(LATIN_3, 0x00eb, 0x00eb);
        assertConversion(LATIN_3, 0x00ec, 0x00ec);
        assertConversion(LATIN_3, 0x00ed, 0x00ed);
        assertConversion(LATIN_3, 0x00ee, 0x00ee);
        assertConversion(LATIN_3, 0x00ef, 0x00ef);
        assertConversion(LATIN_3, 0x00f0, 0x0000);
        assertConversion(LATIN_3, 0x00f1, 0x00f1);
        assertConversion(LATIN_3, 0x00f2, 0x00f2);
        assertConversion(LATIN_3, 0x00f3, 0x00f3);
        assertConversion(LATIN_3, 0x00f4, 0x00f4);
        assertConversion(LATIN_3, 0x00f5, 0x0121);
        assertConversion(LATIN_3, 0x00f6, 0x00f6);
        assertConversion(LATIN_3, 0x00f7, 0x00f7);
        assertConversion(LATIN_3, 0x00f8, 0x011d);
        assertConversion(LATIN_3, 0x00f9, 0x00f9);
        assertConversion(LATIN_3, 0x00fa, 0x00fa);
        assertConversion(LATIN_3, 0x00fb, 0x00fb);
        assertConversion(LATIN_3, 0x00fc, 0x00fc);
        assertConversion(LATIN_3, 0x00fd, 0x016d);
        assertConversion(LATIN_3, 0x00fe, 0x015d);
        assertConversion(LATIN_3, 0x00ff, 0x02d9);
        assertNoConversionAfter(LATIN_3, 0x00ff);
    }

    @Test
    public void testLatin_4() {
        assertNoConversionBefore(LATIN_4, 0x00a1);
        assertConversion(LATIN_4, 0x00a1, 0x0104);
        assertConversion(LATIN_4, 0x00a2, 0x0138);
        assertConversion(LATIN_4, 0x00a3, 0x0156);
        assertConversion(LATIN_4, 0x00a4, 0x00a4);
        assertConversion(LATIN_4, 0x00a5, 0x0128);
        assertConversion(LATIN_4, 0x00a6, 0x013b);
        assertConversion(LATIN_4, 0x00a7, 0x00a7);
        assertConversion(LATIN_4, 0x00a8, 0x00a8);
        assertConversion(LATIN_4, 0x00a9, 0x0160);
        assertConversion(LATIN_4, 0x00aa, 0x0112);
        assertConversion(LATIN_4, 0x00ab, 0x0122);
        assertConversion(LATIN_4, 0x00ac, 0x0166);
        assertConversion(LATIN_4, 0x00ad, 0x00ad);
        assertConversion(LATIN_4, 0x00ae, 0x017d);
        assertConversion(LATIN_4, 0x00af, 0x00af);
        assertConversion(LATIN_4, 0x00b0, 0x00b0);
        assertConversion(LATIN_4, 0x00b1, 0x0105);
        assertConversion(LATIN_4, 0x00b2, 0x02db);
        assertConversion(LATIN_4, 0x00b3, 0x0157);
        assertConversion(LATIN_4, 0x00b4, 0x00b4);
        assertConversion(LATIN_4, 0x00b5, 0x0129);
        assertConversion(LATIN_4, 0x00b6, 0x013c);
        assertConversion(LATIN_4, 0x00b7, 0x02c7);
        assertConversion(LATIN_4, 0x00b8, 0x00b8);
        assertConversion(LATIN_4, 0x00b9, 0x0161);
        assertConversion(LATIN_4, 0x00ba, 0x0113);
        assertConversion(LATIN_4, 0x00bb, 0x0123);
        assertConversion(LATIN_4, 0x00bc, 0x0167);
        assertConversion(LATIN_4, 0x00bd, 0x014a);
        assertConversion(LATIN_4, 0x00be, 0x017e);
        assertConversion(LATIN_4, 0x00bf, 0x014b);
        assertConversion(LATIN_4, 0x00c0, 0x0100);
        assertConversion(LATIN_4, 0x00c1, 0x00c1);
        assertConversion(LATIN_4, 0x00c2, 0x00c2);
        assertConversion(LATIN_4, 0x00c3, 0x00c3);
        assertConversion(LATIN_4, 0x00c4, 0x00c4);
        assertConversion(LATIN_4, 0x00c5, 0x00c5);
        assertConversion(LATIN_4, 0x00c6, 0x00c6);
        assertConversion(LATIN_4, 0x00c7, 0x012e);
        assertConversion(LATIN_4, 0x00c8, 0x010c);
        assertConversion(LATIN_4, 0x00c9, 0x00c9);
        assertConversion(LATIN_4, 0x00ca, 0x0118);
        assertConversion(LATIN_4, 0x00cb, 0x00cb);
        assertConversion(LATIN_4, 0x00cc, 0x0116);
        assertConversion(LATIN_4, 0x00cd, 0x00cd);
        assertConversion(LATIN_4, 0x00ce, 0x00ce);
        assertConversion(LATIN_4, 0x00cf, 0x012a);
        assertConversion(LATIN_4, 0x00d0, 0x0110);
        assertConversion(LATIN_4, 0x00d1, 0x0145);
        assertConversion(LATIN_4, 0x00d2, 0x014c);
        assertConversion(LATIN_4, 0x00d3, 0x0136);
        assertConversion(LATIN_4, 0x00d4, 0x00d4);
        assertConversion(LATIN_4, 0x00d5, 0x00d5);
        assertConversion(LATIN_4, 0x00d6, 0x00d6);
        assertConversion(LATIN_4, 0x00d7, 0x00d7);
        assertConversion(LATIN_4, 0x00d8, 0x00d8);
        assertConversion(LATIN_4, 0x00d9, 0x0172);
        assertConversion(LATIN_4, 0x00da, 0x00da);
        assertConversion(LATIN_4, 0x00db, 0x00db);
        assertConversion(LATIN_4, 0x00dc, 0x00dc);
        assertConversion(LATIN_4, 0x00dd, 0x0168);
        assertConversion(LATIN_4, 0x00de, 0x016a);
        assertConversion(LATIN_4, 0x00df, 0x00df);
        assertConversion(LATIN_4, 0x00e0, 0x0101);
        assertConversion(LATIN_4, 0x00e1, 0x00e1);
        assertConversion(LATIN_4, 0x00e2, 0x00e2);
        assertConversion(LATIN_4, 0x00e3, 0x00e3);
        assertConversion(LATIN_4, 0x00e4, 0x00e4);
        assertConversion(LATIN_4, 0x00e5, 0x00e5);
        assertConversion(LATIN_4, 0x00e6, 0x00e6);
        assertConversion(LATIN_4, 0x00e7, 0x012f);
        assertConversion(LATIN_4, 0x00e8, 0x010d);
        assertConversion(LATIN_4, 0x00e9, 0x00e9);
        assertConversion(LATIN_4, 0x00ea, 0x0119);
        assertConversion(LATIN_4, 0x00eb, 0x00eb);
        assertConversion(LATIN_4, 0x00ec, 0x0117);
        assertConversion(LATIN_4, 0x00ed, 0x00ed);
        assertConversion(LATIN_4, 0x00ee, 0x00ee);
        assertConversion(LATIN_4, 0x00ef, 0x012b);
        assertConversion(LATIN_4, 0x00f0, 0x0111);
        assertConversion(LATIN_4, 0x00f1, 0x0146);
        assertConversion(LATIN_4, 0x00f2, 0x014d);
        assertConversion(LATIN_4, 0x00f3, 0x0137);
        assertConversion(LATIN_4, 0x00f4, 0x00f4);
        assertConversion(LATIN_4, 0x00f5, 0x00f5);
        assertConversion(LATIN_4, 0x00f6, 0x00f6);
        assertConversion(LATIN_4, 0x00f7, 0x00f7);
        assertConversion(LATIN_4, 0x00f8, 0x00f8);
        assertConversion(LATIN_4, 0x00f9, 0x0173);
        assertConversion(LATIN_4, 0x00fa, 0x00fa);
        assertConversion(LATIN_4, 0x00fb, 0x00fb);
        assertConversion(LATIN_4, 0x00fc, 0x00fc);
        assertConversion(LATIN_4, 0x00fd, 0x0169);
        assertConversion(LATIN_4, 0x00fe, 0x016b);
        assertConversion(LATIN_4, 0x00ff, 0x02d9);
        assertNoConversionAfter(LATIN_4, 0x00ff);
    }

    @Test
    public void testLatin_5() {
        assertNoConversionBefore(LATIN_5, 0x00a1);
        assertConversion(LATIN_5, 0x00a1, 0x0401);
        assertConversion(LATIN_5, 0x00a2, 0x0402);
        assertConversion(LATIN_5, 0x00a3, 0x0403);
        assertConversion(LATIN_5, 0x00a4, 0x0404);
        assertConversion(LATIN_5, 0x00a5, 0x0405);
        assertConversion(LATIN_5, 0x00a6, 0x0406);
        assertConversion(LATIN_5, 0x00a7, 0x0407);
        assertConversion(LATIN_5, 0x00a8, 0x0408);
        assertConversion(LATIN_5, 0x00a9, 0x0409);
        assertConversion(LATIN_5, 0x00aa, 0x040a);
        assertConversion(LATIN_5, 0x00ab, 0x040b);
        assertConversion(LATIN_5, 0x00ac, 0x040c);
        assertConversion(LATIN_5, 0x00ad, 0x00ad);
        assertConversion(LATIN_5, 0x00ae, 0x040e);
        assertConversion(LATIN_5, 0x00af, 0x040f);
        assertConversion(LATIN_5, 0x00b0, 0x0410);
        assertConversion(LATIN_5, 0x00b1, 0x0411);
        assertConversion(LATIN_5, 0x00b2, 0x0412);
        assertConversion(LATIN_5, 0x00b3, 0x0413);
        assertConversion(LATIN_5, 0x00b4, 0x0414);
        assertConversion(LATIN_5, 0x00b5, 0x0415);
        assertConversion(LATIN_5, 0x00b6, 0x0416);
        assertConversion(LATIN_5, 0x00b7, 0x0417);
        assertConversion(LATIN_5, 0x00b8, 0x0418);
        assertConversion(LATIN_5, 0x00b9, 0x0419);
        assertConversion(LATIN_5, 0x00ba, 0x041a);
        assertConversion(LATIN_5, 0x00bb, 0x041b);
        assertConversion(LATIN_5, 0x00bc, 0x041c);
        assertConversion(LATIN_5, 0x00bd, 0x041d);
        assertConversion(LATIN_5, 0x00be, 0x041e);
        assertConversion(LATIN_5, 0x00bf, 0x041f);
        assertConversion(LATIN_5, 0x00c0, 0x0420);
        assertConversion(LATIN_5, 0x00c1, 0x0421);
        assertConversion(LATIN_5, 0x00c2, 0x0422);
        assertConversion(LATIN_5, 0x00c3, 0x0423);
        assertConversion(LATIN_5, 0x00c4, 0x0424);
        assertConversion(LATIN_5, 0x00c5, 0x0425);
        assertConversion(LATIN_5, 0x00c6, 0x0426);
        assertConversion(LATIN_5, 0x00c7, 0x0427);
        assertConversion(LATIN_5, 0x00c8, 0x0428);
        assertConversion(LATIN_5, 0x00c9, 0x0429);
        assertConversion(LATIN_5, 0x00ca, 0x042a);
        assertConversion(LATIN_5, 0x00cb, 0x042b);
        assertConversion(LATIN_5, 0x00cc, 0x042c);
        assertConversion(LATIN_5, 0x00cd, 0x042d);
        assertConversion(LATIN_5, 0x00ce, 0x042e);
        assertConversion(LATIN_5, 0x00cf, 0x042f);
        assertConversion(LATIN_5, 0x00d0, 0x0430);
        assertConversion(LATIN_5, 0x00d1, 0x0431);
        assertConversion(LATIN_5, 0x00d2, 0x0432);
        assertConversion(LATIN_5, 0x00d3, 0x0433);
        assertConversion(LATIN_5, 0x00d4, 0x0434);
        assertConversion(LATIN_5, 0x00d5, 0x0435);
        assertConversion(LATIN_5, 0x00d6, 0x0436);
        assertConversion(LATIN_5, 0x00d7, 0x0437);
        assertConversion(LATIN_5, 0x00d8, 0x0438);
        assertConversion(LATIN_5, 0x00d9, 0x0439);
        assertConversion(LATIN_5, 0x00da, 0x043a);
        assertConversion(LATIN_5, 0x00db, 0x043b);
        assertConversion(LATIN_5, 0x00dc, 0x043c);
        assertConversion(LATIN_5, 0x00dd, 0x043d);
        assertConversion(LATIN_5, 0x00de, 0x043e);
        assertConversion(LATIN_5, 0x00df, 0x043f);
        assertConversion(LATIN_5, 0x00e0, 0x0440);
        assertConversion(LATIN_5, 0x00e1, 0x0441);
        assertConversion(LATIN_5, 0x00e2, 0x0442);
        assertConversion(LATIN_5, 0x00e3, 0x0443);
        assertConversion(LATIN_5, 0x00e4, 0x0444);
        assertConversion(LATIN_5, 0x00e5, 0x0445);
        assertConversion(LATIN_5, 0x00e6, 0x0446);
        assertConversion(LATIN_5, 0x00e7, 0x0447);
        assertConversion(LATIN_5, 0x00e8, 0x0448);
        assertConversion(LATIN_5, 0x00e9, 0x0449);
        assertConversion(LATIN_5, 0x00ea, 0x044a);
        assertConversion(LATIN_5, 0x00eb, 0x044b);
        assertConversion(LATIN_5, 0x00ec, 0x044c);
        assertConversion(LATIN_5, 0x00ed, 0x044d);
        assertConversion(LATIN_5, 0x00ee, 0x044e);
        assertConversion(LATIN_5, 0x00ef, 0x044f);
        assertConversion(LATIN_5, 0x00f0, 0x2116);
        assertConversion(LATIN_5, 0x00f1, 0x0451);
        assertConversion(LATIN_5, 0x00f2, 0x0452);
        assertConversion(LATIN_5, 0x00f3, 0x0453);
        assertConversion(LATIN_5, 0x00f4, 0x0454);
        assertConversion(LATIN_5, 0x00f5, 0x0455);
        assertConversion(LATIN_5, 0x00f6, 0x0456);
        assertConversion(LATIN_5, 0x00f7, 0x0457);
        assertConversion(LATIN_5, 0x00f8, 0x0458);
        assertConversion(LATIN_5, 0x00f9, 0x0459);
        assertConversion(LATIN_5, 0x00fa, 0x045a);
        assertConversion(LATIN_5, 0x00fb, 0x045b);
        assertConversion(LATIN_5, 0x00fc, 0x045c);
        assertConversion(LATIN_5, 0x00fd, 0x00a7);
        assertConversion(LATIN_5, 0x00fe, 0x045e);
        assertConversion(LATIN_5, 0x00ff, 0x045f);
        assertNoConversionAfter(LATIN_5, 0x00ff);
    }

    @Test
    public void testLatin_7() {
        assertNoConversionBefore(LATIN_7, 0x00a1);
        assertConversion(LATIN_7, 0x00a1, 0x2018);
        assertConversion(LATIN_7, 0x00a2, 0x2019);
        assertConversion(LATIN_7, 0x00a3, 0x00a3);
        assertConversion(LATIN_7, 0x00a4, 0x0000);
        assertConversion(LATIN_7, 0x00a5, 0x0000);
        assertConversion(LATIN_7, 0x00a6, 0x00a6);
        assertConversion(LATIN_7, 0x00a7, 0x00a7);
        assertConversion(LATIN_7, 0x00a8, 0x00a8);
        assertConversion(LATIN_7, 0x00a9, 0x00a9);
        assertConversion(LATIN_7, 0x00aa, 0x0000);
        assertConversion(LATIN_7, 0x00ab, 0x00ab);
        assertConversion(LATIN_7, 0x00ac, 0x00ac);
        assertConversion(LATIN_7, 0x00ad, 0x00ad);
        assertConversion(LATIN_7, 0x00ae, 0x0000);
        assertConversion(LATIN_7, 0x00af, 0x2015);
        assertConversion(LATIN_7, 0x00b0, 0x00b0);
        assertConversion(LATIN_7, 0x00b1, 0x00b1);
        assertConversion(LATIN_7, 0x00b2, 0x00b2);
        assertConversion(LATIN_7, 0x00b3, 0x00b3);
        assertConversion(LATIN_7, 0x00b4, 0x0384);
        assertConversion(LATIN_7, 0x00b5, 0x0385);
        assertConversion(LATIN_7, 0x00b6, 0x0386);
        assertConversion(LATIN_7, 0x00b7, 0x00b7);
        assertConversion(LATIN_7, 0x00b8, 0x0388);
        assertConversion(LATIN_7, 0x00b9, 0x0389);
        assertConversion(LATIN_7, 0x00ba, 0x038a);
        assertConversion(LATIN_7, 0x00bb, 0x00bb);
        assertConversion(LATIN_7, 0x00bc, 0x038c);
        assertConversion(LATIN_7, 0x00bd, 0x00bd);
        assertConversion(LATIN_7, 0x00be, 0x038e);
        assertConversion(LATIN_7, 0x00bf, 0x038f);
        assertConversion(LATIN_7, 0x00c0, 0x0390);
        assertConversion(LATIN_7, 0x00c1, 0x0391);
        assertConversion(LATIN_7, 0x00c2, 0x0392);
        assertConversion(LATIN_7, 0x00c3, 0x0393);
        assertConversion(LATIN_7, 0x00c4, 0x0394);
        assertConversion(LATIN_7, 0x00c5, 0x0395);
        assertConversion(LATIN_7, 0x00c6, 0x0396);
        assertConversion(LATIN_7, 0x00c7, 0x0397);
        assertConversion(LATIN_7, 0x00c8, 0x0398);
        assertConversion(LATIN_7, 0x00c9, 0x0399);
        assertConversion(LATIN_7, 0x00ca, 0x039a);
        assertConversion(LATIN_7, 0x00cb, 0x039b);
        assertConversion(LATIN_7, 0x00cc, 0x039c);
        assertConversion(LATIN_7, 0x00cd, 0x039d);
        assertConversion(LATIN_7, 0x00ce, 0x039e);
        assertConversion(LATIN_7, 0x00cf, 0x039f);
        assertConversion(LATIN_7, 0x00d0, 0x03a0);
        assertConversion(LATIN_7, 0x00d1, 0x03a1);
        assertConversion(LATIN_7, 0x00d2, 0x0000);
        assertConversion(LATIN_7, 0x00d3, 0x03a3);
        assertConversion(LATIN_7, 0x00d4, 0x03a4);
        assertConversion(LATIN_7, 0x00d5, 0x03a5);
        assertConversion(LATIN_7, 0x00d6, 0x03a6);
        assertConversion(LATIN_7, 0x00d7, 0x03a7);
        assertConversion(LATIN_7, 0x00d8, 0x03a8);
        assertConversion(LATIN_7, 0x00d9, 0x03a9);
        assertConversion(LATIN_7, 0x00da, 0x03aa);
        assertConversion(LATIN_7, 0x00db, 0x03ab);
        assertConversion(LATIN_7, 0x00dc, 0x03ac);
        assertConversion(LATIN_7, 0x00dd, 0x03ad);
        assertConversion(LATIN_7, 0x00de, 0x03ae);
        assertConversion(LATIN_7, 0x00df, 0x03af);
        assertConversion(LATIN_7, 0x00e0, 0x03b0);
        assertConversion(LATIN_7, 0x00e1, 0x03b1);
        assertConversion(LATIN_7, 0x00e2, 0x03b2);
        assertConversion(LATIN_7, 0x00e3, 0x03b3);
        assertConversion(LATIN_7, 0x00e4, 0x03b4);
        assertConversion(LATIN_7, 0x00e5, 0x03b5);
        assertConversion(LATIN_7, 0x00e6, 0x03b6);
        assertConversion(LATIN_7, 0x00e7, 0x03b7);
        assertConversion(LATIN_7, 0x00e8, 0x03b8);
        assertConversion(LATIN_7, 0x00e9, 0x03b9);
        assertConversion(LATIN_7, 0x00ea, 0x03ba);
        assertConversion(LATIN_7, 0x00eb, 0x03bb);
        assertConversion(LATIN_7, 0x00ec, 0x03bc);
        assertConversion(LATIN_7, 0x00ed, 0x03bd);
        assertConversion(LATIN_7, 0x00ee, 0x03be);
        assertConversion(LATIN_7, 0x00ef, 0x03bf);
        assertConversion(LATIN_7, 0x00f0, 0x03c0);
        assertConversion(LATIN_7, 0x00f1, 0x03c1);
        assertConversion(LATIN_7, 0x00f2, 0x03c2);
        assertConversion(LATIN_7, 0x00f3, 0x03c3);
        assertConversion(LATIN_7, 0x00f4, 0x03c4);
        assertConversion(LATIN_7, 0x00f5, 0x03c5);
        assertConversion(LATIN_7, 0x00f6, 0x03c6);
        assertConversion(LATIN_7, 0x00f7, 0x03c7);
        assertConversion(LATIN_7, 0x00f8, 0x03c8);
        assertConversion(LATIN_7, 0x00f9, 0x03c9);
        assertConversion(LATIN_7, 0x00fa, 0x03ca);
        assertConversion(LATIN_7, 0x00fb, 0x03cb);
        assertConversion(LATIN_7, 0x00fc, 0x03cc);
        assertConversion(LATIN_7, 0x00fd, 0x03cd);
        assertConversion(LATIN_7, 0x00fe, 0x03ce);
        assertNoConversionAfter(LATIN_7, 0x00fe);
    }

    @Test
    public void testLatin_9() {
        assertNoConversionBefore(LATIN_9, 0x00d0);
        assertConversion(LATIN_9, 0x00d0, 0x011e);
        assertConversion(LATIN_9, 0x00d1, 0x00d1);
        assertConversion(LATIN_9, 0x00d2, 0x00d2);
        assertConversion(LATIN_9, 0x00d3, 0x00d3);
        assertConversion(LATIN_9, 0x00d4, 0x00d4);
        assertConversion(LATIN_9, 0x00d5, 0x00d5);
        assertConversion(LATIN_9, 0x00d6, 0x00d6);
        assertConversion(LATIN_9, 0x00d7, 0x00d7);
        assertConversion(LATIN_9, 0x00d8, 0x00d8);
        assertConversion(LATIN_9, 0x00d9, 0x00d9);
        assertConversion(LATIN_9, 0x00da, 0x00da);
        assertConversion(LATIN_9, 0x00db, 0x00db);
        assertConversion(LATIN_9, 0x00dc, 0x00dc);
        assertConversion(LATIN_9, 0x00dd, 0x0130);
        assertConversion(LATIN_9, 0x00de, 0x015e);
        assertConversion(LATIN_9, 0x00df, 0x00df);
        assertConversion(LATIN_9, 0x00e0, 0x00e0);
        assertConversion(LATIN_9, 0x00e1, 0x00e1);
        assertConversion(LATIN_9, 0x00e2, 0x00e2);
        assertConversion(LATIN_9, 0x00e3, 0x00e3);
        assertConversion(LATIN_9, 0x00e4, 0x00e4);
        assertConversion(LATIN_9, 0x00e5, 0x00e5);
        assertConversion(LATIN_9, 0x00e6, 0x00e6);
        assertConversion(LATIN_9, 0x00e7, 0x00e7);
        assertConversion(LATIN_9, 0x00e8, 0x00e8);
        assertConversion(LATIN_9, 0x00e9, 0x00e9);
        assertConversion(LATIN_9, 0x00ea, 0x00ea);
        assertConversion(LATIN_9, 0x00eb, 0x00eb);
        assertConversion(LATIN_9, 0x00ec, 0x00ec);
        assertConversion(LATIN_9, 0x00ed, 0x00ed);
        assertConversion(LATIN_9, 0x00ee, 0x00ee);
        assertConversion(LATIN_9, 0x00ef, 0x00ef);
        assertConversion(LATIN_9, 0x00f0, 0x011f);
        assertConversion(LATIN_9, 0x00f1, 0x00f1);
        assertConversion(LATIN_9, 0x00f2, 0x00f2);
        assertConversion(LATIN_9, 0x00f3, 0x00f3);
        assertConversion(LATIN_9, 0x00f4, 0x00f4);
        assertConversion(LATIN_9, 0x00f5, 0x00f5);
        assertConversion(LATIN_9, 0x00f6, 0x00f6);
        assertConversion(LATIN_9, 0x00f7, 0x00f7);
        assertConversion(LATIN_9, 0x00f8, 0x00f8);
        assertConversion(LATIN_9, 0x00f9, 0x00f9);
        assertConversion(LATIN_9, 0x00fa, 0x00fa);
        assertConversion(LATIN_9, 0x00fb, 0x00fb);
        assertConversion(LATIN_9, 0x00fc, 0x00fc);
        assertConversion(LATIN_9, 0x00fd, 0x0131);
        assertConversion(LATIN_9, 0x00fe, 0x015f);
        assertNoConversionAfter(LATIN_9, 0x00fe);
    }

    @Test
    public void testPCS() {
        assertNoConversion(PCS);
    }
}

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

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversionWidthsTest {
    static final char[] testData = { '\u0000', '\u007f', '\u0080', '\u07ff', '\u0800', '\uffff' };

    @Test
    public void testUTF8ConversionWidths() {
        UTF8Writer writer = new UTF8Writer();
        for (char c: testData) {
            assertEquals(StandardCharsets.UTF_8.encode(Character.toString(c)).remaining(), writer.count_wchar(c),
                    () -> String.format("Mismatch width for character '\\u%04x'", (int)c));
        }
    }
}

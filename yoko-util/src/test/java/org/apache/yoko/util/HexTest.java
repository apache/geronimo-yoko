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
package org.apache.yoko.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_METHOD)
public class HexTest {
    static final byte[] bytes = new byte[32];

    final StringBuilder sb = new StringBuilder();

    @Test
    void formatSubArray0_16() {
        Hex.formatHexPara(bytes, 0, 16, sb);
        Assertions.assertEquals("0000:  00000000 00000000 00000000 00000000  \"................\"", sb.toString());
    }

    @Test
    void formatSubArray0_15() {
        Hex.formatHexPara(bytes, 0, 15, sb);
        Assertions.assertEquals("0000:  00000000 00000000 00000000 000000    \"...............\"", sb.toString());
    }

    @Test
    void formatSubArray1_14() {
        Hex.formatHexPara(bytes, 1, 14, sb);
        Assertions.assertEquals("0001:    000000 00000000 00000000 000000     \"..............\"", sb.toString());
    }

    @Test
    void formatSubArray0_31() {
        Hex.formatHexPara(bytes, 0, 31, sb);
        Assertions.assertEquals("" +
                "0000:  00000000 00000000 00000000 00000000  \"................\"\n" +
                "0010:  00000000 00000000 00000000 000000    \"...............\"", sb.toString());
    }

    @Test
    void formatSubArray0_32() {
        Hex.formatHexPara(bytes, 0, 32, sb);
        Assertions.assertEquals("" +
                "0000:  00000000 00000000 00000000 00000000  \"................\"\n" +
                "0010:  00000000 00000000 00000000 00000000  \"................\"", sb.toString());
    }
}

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

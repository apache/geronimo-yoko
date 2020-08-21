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

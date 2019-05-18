package test.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static test.util.HexParser.HEX_DUMP;
import static test.util.HexParser.HEX_STRING;
import static test.util.ByteArrayMatchers.*;

public class HexParserTest {

    @Test
    public void testHexDumpNullString() {
        Object actual = HEX_DUMP.parse(null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testHexDumpEmptyString() {
        byte[] actual = HEX_DUMP.parse("");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test
    public void testHexDumpBlankLines() {
        // lines starting with two dashes are ignored
        byte[] actual = HEX_DUMP.parse("\n\n\n");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test
    public void testHexDumpCommentLines() {
        // lines starting with two dashes are ignored
        byte[] actual = HEX_DUMP.parse("--\n--");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexDumpAlmostCommentLine() {
        // lines starting with two dashes are ignored
        HEX_DUMP.parse("--\n-");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexDumpInvalidHexLine() {
        byte[] actual = HEX_DUMP.parse("47494f5O 01020000 0000016c 00000007  \"GIOP.......l....\"");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexDumpInvalidAsciiLine() {
        byte[] actual = HEX_DUMP.parse("47494f50 01020000 0000016c 00000007  \"FIOP.......l....\"");
    }

    @Test
    public void testHexDumpValidLine() {
        byte[] actual = HEX_DUMP.parse("47494f50 01020000 0000016c 00000007  \"GIOP.......l....\"");
        assertThat(actual, matchesHex("47494f50 01020000 0000016c 00000007"));

    }

    @Test
    public void testHexDumpTruncatedLine() {
        byte[] actual = HEX_DUMP.parse("    776f726c 6400                        \"world.\"");
        assertThat(actual, matchesHex("776f726c 6400"));
    }

    @Test
    public void testHexDumpTruncatedLastLine() {
        String text = "" +
                "    7fffff02 00000046 524d493a 6f72672e  \".......FRMI:org.\"\n" +
                "    6f6d672e 436f734e 616d696e 672e4e61  \"omg.CosNaming.Na\"\n" +
                "    6d65436f 6d706f6e 656e743a 45303638  \"meComponent:E068\"\n" +
                "    41373543 39383933 30443636 3a463136  \"A75C98930D66:F16\"\n" +
                "    34413231 39344136 36323832 4100bdbd  \"4A2194A66282A...\"\n" +
                "    00000006 68656c6c 6f00bdbd 00000006  \"....hello.......\"\n" +
                "    776f726c 6400                        \"world.\"";
        byte[] actual = HEX_DUMP.parse(text);
        assertThat(actual, matchesHex(text));
    }

    @Test
    public void testHexStringNull() {
        byte[] actual = HEX_STRING.parse(null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testHexStringEmpty() {
        byte[] actual = HEX_STRING.parse("");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testHexString1Char() {
        HEX_STRING.parse("0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testHexString3Chars() {
        HEX_STRING.parse("000");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testHexStringBadChars() {
        HEX_STRING.parse("gg");
    }

    @Test
    public void testHexStringZeroes() {
        byte[] actual = HEX_STRING.parse("000000");
        assertThat(actual.length, is(3));
        assertThat(actual, matchesHex("000000"));
    }

    @Test
    public void testHexString16bytes() {
        byte[] actual = HEX_STRING.parse("47494f50010200000000016c00000007");
        assertThat(actual, matchesHex("47494f50 01020000 0000016c 00000007"));

    }

    @Test
    public void testHexString6bytes() {
        byte[] actual = HEX_STRING.parse("776f726c6400");
        assertThat(actual, matchesHex("776f726c 6400"));
    }

    @Test
    public void testHexString5LeadingZeroes() {
        byte[] actual = HEX_STRING.parse("0000010200000000016c00000007");
        assertThat(actual, matchesHex("00000102 00000000 016c0000 0007"));
    }

    @Test
    public void testHexString() {
        String hex = "" +
                "7fffff02 00000046 524d493a 6f72672e" +
                "6f6d672e 436f734e 616d696e 672e4e61" +
                "6d65436f 6d706f6e 656e743a 45303638" +
                "41373543 39383933 30443636 3a463136" +
                "34413231 39344136 36323832 4100bdbd" +
                "00000006 68656c6c 6f00bdbd 00000006" +
                "776f726c 6400";
        byte[] actual = HEX_STRING.parse(hex);
        assertThat(actual, matchesHex(hex));
    }
}

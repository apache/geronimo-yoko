package testify.hex;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testify.hex.HexParser.HEX_DUMP;
import static testify.hex.HexParser.HEX_STRING;
import static testify.matchers.ByteArrayMatchers.emptyByteArray;
import static testify.matchers.ByteArrayMatchers.matchesHex;

class HexParserTest {

    @Test
    void testHexDumpNullString() {
        Object actual = HEX_DUMP.parse(null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    void testHexDumpEmptyString() {
        byte[] actual = HEX_DUMP.parse("");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test
    void testHexDumpBlankLines() {
        // lines starting with two dashes are ignored
        byte[] actual = HEX_DUMP.parse("\n\n\n");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test
    void testHexDumpCommentLines() {
        // lines starting with two dashes are ignored
        byte[] actual = HEX_DUMP.parse("--\n--");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test
    void testHexDumpAlmostCommentLine() {
        // lines starting with two dashes are ignored
        assertThrows(IllegalArgumentException.class, () -> HEX_DUMP.parse("--\n-"));
    }

    @Test
    void testHexDumpInvalidHexLine() {
        assertThrows(IllegalArgumentException.class, () -> HEX_DUMP.parse("47494f5O 01020000 0000016c 00000007  \"GIOP.......l....\""));
    }

    @Test
    void testHexDumpInvalidAsciiLine() {
        assertThrows(IllegalArgumentException.class, () -> HEX_DUMP.parse("47494f50 01020000 0000016c 00000007  \"FIOP.......l....\""));
    }

    @Test
    void testHexDumpValidLine() {
        byte[] actual = HEX_DUMP.parse("47494f50 01020000 0000016c 00000007  \"GIOP.......l....\"");
        assertThat(actual, matchesHex("47494f50 01020000 0000016c 00000007"));
    }

    @Test
    void testHexDumpTruncatedLine() {
        byte[] actual = HEX_DUMP.parse("    776f726c 6400                        \"world.\"");
        assertThat(actual, matchesHex("776f726c 6400"));
    }

    @Test
    void testHexDumpTruncatedLastLine() {
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
    void testHexDumpWithIndices() {
        String text = "" +
                "0000:  7fffff02 0000005f 524d493a 6f72672e  \"......._RMI:org.\"\n" +
                "0010:  61706163 68652e79 6f6b6f2e 4a617661  \"apache.yoko.Java\"\n" +
                "0020:  56616c75 654e756c 6c466965 6c647354  \"ValueNullFieldsT\"\n" +
                "0030:  6573745c 55303032 344e6f64 6479486f  \"est\\U0024NoddyHo\"\n" +
                "0040:  6c646572 3a434635 34363237 33433542  \"lder:CF546273C5B\"\n" +
                "0050:  46364632 333a3442 37383142 32443244  \"F6F23:4B781B2D2D\"\n" +
                "0060:  30303133 383300bd 00000000 00000000  \"001383..........\"\n" +
                "0070:  00bdbdbd 00000000                    \"........\"";
        byte[] actual = HEX_DUMP.parse(text);
        assertThat(actual, matchesHex(text));
    }

    @Test
    void testHexStringNull() {
        byte[] actual = HEX_STRING.parse(null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    void testHexStringEmpty() {
        byte[] actual = HEX_STRING.parse("");
        assertThat(actual, is(emptyByteArray()));
    }

    @Test
    void testHexString1Char() {
        assertThrows(IllegalArgumentException.class, () -> HEX_STRING.parse("0"));
    }

    @Test
    void testHexString3Chars() {
        assertThrows(IllegalArgumentException.class, () -> HEX_STRING.parse("000"));
    }

    @Test
    void testHexStringBadChars() {
        assertThrows(IllegalArgumentException.class, () -> HEX_STRING.parse("gg"));
    }

    @Test
    void testHexStringZeroes() {
        byte[] actual = HEX_STRING.parse("000000");
        assertThat(actual.length, is(3));
        assertThat(actual, matchesHex("000000"));
    }

    @Test
    void testHexString16bytes() {
        byte[] actual = HEX_STRING.parse("47494f50010200000000016c00000007");
        assertThat(actual, matchesHex("47494f50 01020000 0000016c 00000007"));

    }

    @Test
    void testHexString6bytes() {
        byte[] actual = HEX_STRING.parse("776f726c6400");
        assertThat(actual, matchesHex("776f726c 6400"));
    }

    @Test
    void testHexString5LeadingZeroes() {
        byte[] actual = HEX_STRING.parse("0000010200000000016c00000007");
        assertThat(actual, matchesHex("00000102 00000000 016c0000 0007"));
    }

    @Test
    void testHexString() {
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

package test.util;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class HexParserTest {
    HexParser parser;

    @Before
    public void setup() {
        parser = new HexParser();
    }

    @Test
    public void testNullString() {
        Object actual = parser.parse((String)null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testEmptyString() {
        byte[] actual = parser.parse("");
        assertThat(actual.length, is(0));
    }

    @Test
    public void testBlankLines() {
        // lines starting with two dashes are ignored
        byte[] actual = parser.parse("\n\n\n");
    }

    @Test
    public void testCommentLines() {
        // lines starting with two dashes are ignored
        byte[] actual = parser.parse("--\n--");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlmostCommentLine() {
        // lines starting with two dashes are ignored
        byte[] actual = parser.parse("--\n-");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHexLine() {
        byte[] actual = parser.parse("47494f5O 01020000 0000016c 00000007  \"GIOP.......l....\"");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAsciiLine() {
        byte[] actual = parser.parse("47494f50 01020000 0000016c 00000007  \"FIOP.......l....\"");
    }

    @Test
    public void testValidLine() {
        byte[] actual = parser.parse("47494f50 01020000 0000016c 00000007  \"GIOP.......l....\"");
        assertThat(hex(actual), is("47494f50 01020000 0000016c 00000007"));

    }

    @Test
    public void testTruncatedLine() {
        byte[] actual = parser.parse("    776f726c 6400                        \"world.\"");
        assertThat(hex(actual), is("776f726c 6400"));
    }

    @Test
    public void testTruncatedLastLine() {
        byte[] actual = parser.parse("" +
                        "    7fffff02 00000046 524d493a 6f72672e  \".......FRMI:org.\"\n" +
                        "    6f6d672e 436f734e 616d696e 672e4e61  \"omg.CosNaming.Na\"\n" +
                        "    6d65436f 6d706f6e 656e743a 45303638  \"meComponent:E068\"\n" +
                        "    41373543 39383933 30443636 3a463136  \"A75C98930D66:F16\"\n" +
                        "    34413231 39344136 36323832 4100bdbd  \"4A2194A66282A...\"\n" +
                        "    00000006 68656c6c 6f00bdbd 00000006  \"....hello.......\"\n" +
                        "    776f726c 6400                        \"world.\"");

        assertThat(hex(actual), is("" +
                "7fffff02 00000046 524d493a 6f72672e\n" +
                "6f6d672e 436f734e 616d696e 672e4e61\n" +
                "6d65436f 6d706f6e 656e743a 45303638\n" +
                "41373543 39383933 30443636 3a463136\n" +
                "34413231 39344136 36323832 4100bdbd\n" +
                "00000006 68656c6c 6f00bdbd 00000006\n" +
                "776f726c 6400"
        ));

    }



    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static String hex(byte b) {
        return String.format("%02x", b);
    }
    private static String hex(byte[] data) {
        String result = "";
        for (int i = 0; i < data.length; i++) {
            if (i == 0); // do nothing
            else if (i%16 == 0) result += '\n';
            else if (i%4 == 0) result += ' ';
            result += hex(data[i]);
        }
        return result;
    }
}

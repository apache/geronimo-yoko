package testify.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Formatter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public enum ByteArrayMatchers {;
    private static final byte[] EMPTY_BYTE_ARRAY = {};

    public static Matcher<byte[]> emptyByteArray() { return equalTo(EMPTY_BYTE_ARRAY); }

    private static String hexChar(byte b) {
        return String.format("%02x", b);
    }

    private static String prettify(byte[] data) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : data) formatter.format("%02x", b);
            return prettify(formatter.toString());
        }
    }

    private static String prettify(String hex) {
        // remove indices
        hex = hex.replaceAll("[0-9A-F]{4,8}: +", "");
        // remove quoted ASCII representation
        hex = hex.replaceAll("\".{1,16}\"", "");
        // remove non-hex chars
        hex = hex.replaceAll("[^0-9A-Fa-f]", "");
        // add newlines after every 16 bytes
        hex = hex.replaceAll("(.{32})(?=.)","$1\n");
        // add spaces after every 4 bytes
        hex = hex.replaceAll("([0-9A-Fa-f]{8})(?=.)","$1 ");
        return hex;
    }

    private static String indent(String text, int depth) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) indent.append("\t");
        return indent + text.replace("\n", "\n" + indent);
    }

    /**
     * Matches a byte array to the supplied hex.
     * Accepts dump format with quoted ASCII columns.
     * Accepts plain hex format.
     * Ignores whitespace and any non-hex characters.
     * <br>
     *     <strong>
     *         Note: this matcher does not check the input
     *         text for whether it contains mixed hex and
     *         non-hex characters. As such it may accept
     *         invalid hex input without complaining.
     *         It is the caller's responsibility not to pass
     *         in complete rubbish.
     *     </strong>
     */
    public static Matcher<byte[]> matchesHex(String hex) {
        final String prettyHex = prettify(hex);
        final Matcher<String> hexMtchr = equalToIgnoringCase(prettyHex);
        return new BaseMatcher<byte[]>() {
            @Override
            public boolean matches(Object data) {
                return hexMtchr.matches(prettify((byte[])data));
            }

            @Override
            public void describeTo(Description description) {
                description
                        .appendText("\n")
                        .appendText(indent(prettyHex, 2));
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                description
                        .appendText("got:\n")
                        .appendText(indent(prettify((byte[]) item), 2))
                        .appendText("\n");
            }
        };
    }
}

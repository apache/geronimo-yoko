package test.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.regex.Pattern;

public class HexParser {
    private static final String COMMENT = "--";
    private static final Pattern
        HEX_LINE = Pattern.compile("([0-9 a-f]{8} ){4} \".{1,16}\"");

    public byte[] parse(final String text) {
        if (text == null) return null;
        StringReader sr = new StringReader(text);
        BufferedReader br = new BufferedReader(sr);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String line = null;
            int lineNo = 0;
            while (null != (line = br.readLine())) {
                line = line.trim();
                lineNo++;
                if (line.isEmpty()) {
                    // ignore blank lines
                } else if (line.startsWith(COMMENT)) {
                    // ignore comment lines
                } else if (HEX_LINE.matcher(line).matches()) {
                    InnerParser ip = new InnerParser(out, line.toCharArray());
                    while (ip.hasAnyBytesLeft()) ip.writeNextByte();
                } else {
                    throw new IllegalArgumentException("Could not parse line " + lineNo + " of text:\n" + text );
                }
            }

            return out.toByteArray();
        } catch (IOException e) {
            throw new Error(e);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid string: " + text, e);
        }
    }

    private static class InnerParser {
        private static final byte[] HI_NYBL = new byte['g'];
        private static final byte[] LO_NYBL = new byte['g'];
        static {
            char[] hexChars = "0123456789abcdef".toCharArray();
            for (byte i = 0; i < hexChars.length; i++) {
                char c = hexChars[i], C = Character.toUpperCase(c);
                LO_NYBL[C] = LO_NYBL[c] = i;
                HI_NYBL[C] = HI_NYBL[c] = (byte)(i << 4);
            }
        }

        final OutputStream out;
        final char[] chars;
        int byteIndex = 0;

        private InnerParser(OutputStream out, char[] chars) {
            this.out = out;
            this.chars = chars;
        }

        private boolean hasAnyBytesLeft() {
            // skip any spaces in the hex
            for( int h = byteIndex * 2 + byteIndex / 4; chars[h] == ' ' && byteIndex <= 16; byteIndex++);
            return byteIndex < 16;
        }

        private void writeNextByte() throws IOException, ParseException {
            int hexOffset = byteIndex * 2 + byteIndex / 4;
            int charOffset = byteIndex + 38;
            byte b = HI_NYBL[chars[hexOffset]];
            hexOffset++;
            b |= LO_NYBL[chars[hexOffset]];

            char c = chars[charOffset];

            // sanity check ascii string
            if (c != '.')   // '.' is a wildcard
                if (c != b) // any other char is literal
                    throw new ParseException(new String(chars), charOffset);

            out.write(b);
            byteIndex++;
        }
    }

    public static void main(String[] args) {
        byte b = -128;
        char c = (char)b;
        int i1 = b;
        int i2 = c;
        System.out.printf("b => 0x%x%n", (int)b);
        System.out.printf("b>>>4 => 0x%x%n", (int)((char)b)>>>4);
        System.out.printf("b/16%%16 => 0x%x%n", b/16%16);
        System.out.printf("i1 = %x%n", i1);
        System.out.printf("i2 = %x%n", i2);
    }

}

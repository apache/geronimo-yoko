package testify.hex;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.toUpperCase;

public enum HexParser {
    HEX_DUMP {
        public byte[] parse(final String text) {
            if (text == null) return null;
            StringReader sr = new StringReader(text);
            BufferedReader br = new BufferedReader(sr);
            int lineNo = 0;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                String line;
                while (null != (line = br.readLine())) {
                    line = line.trim();
                    lineNo++;
                    HexLineParser lineParser = HexLineParser.parse(line);
                    if (lineParser == null) continue;
                    lineParser.writeBytes(out);
                }

                return out.toByteArray();
            } catch (IOException e) {
                throw new Error(e);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Could not parse as hex dump: line " + lineNo + "\n", e);
            }
        }
    },
    HEX_STRING {
        @Override
        public byte[] parse(String text) {
            if (text == null) return null;
            text = text.replaceAll("\\s", ""); // remove all whitespace
            if (text.isEmpty()) return new byte[]{};
            if (text.length() % 2 != 0) throw new IllegalArgumentException("Odd number of characters in hex string: " + text);
            int expectedLength = text.length() / 2;
            byte[] bytes;
            try {
                 bytes = new BigInteger(text, 16).toByteArray();
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Could not parse as hex string \"" + text + "\"", nfe);
            }

            if (bytes.length == expectedLength)
                return bytes;

            byte[] nBytes = new byte[expectedLength];

            if (bytes.length < nBytes.length)
                System.arraycopy(bytes, 0, nBytes, nBytes.length - bytes.length, bytes.length);
            else
                System.arraycopy(bytes, bytes.length - nBytes.length, nBytes, 0, nBytes.length);
            return nBytes;
        }
    };

    private static class HexLineParser {
        private static final String COMMENT = "--";
        private static final Pattern HEX_LINE = Pattern.compile("(?:[0-9A-F]{4,8}: {2})?((?:[0-9 a-f]{8} ){4} \".{1,16}\")");
        private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
        static { Arrays.sort(HEX_CHARS); }
        private static final byte[] HI_NYBL = new byte['g'];
        private static final byte[] LO_NYBL = new byte['g'];
        static {
            char[] hexChars = "0123456789abcdef".toCharArray();
            for (byte i = 0; i < hexChars.length; i++) {
                LO_NYBL[toUpperCase(hexChars[i])] = LO_NYBL[hexChars[i]] = i;
                HI_NYBL[toUpperCase(hexChars[i])] = HI_NYBL[hexChars[i]] = (byte)(i << 4);
            }
        }

        static HexLineParser parse(String line) throws ParseException {
            if (line.isEmpty()) return null; // ignore blank lines
            if (line.startsWith(COMMENT)) return null; // ignore comment lines
            Matcher m = HEX_LINE.matcher(line);
            if (m.matches()) return new HexLineParser(m.group(1));
            throw new ParseException(line, 0);
        }

        final char[] chars;
        int logicalByteIndex = 0;

        private HexLineParser(String line) {
            this.chars = line.toCharArray();
        }

        void writeBytes(OutputStream out) throws IOException, ParseException { while(hasAnyBytesLeft()) writeNextByte(out);}

        private boolean hasAnyBytesLeft() {
            // true iff we haven't read 16 bytes yet AND there isn't a space under the cursor
            return logicalByteIndex < 16 && chars[cursor()] != ' ';
        }

        private void writeNextByte(OutputStream out) throws IOException, ParseException {
            byte b = getByte();
            char c = getAsciiChar();

            // sanity check ascii string
            if (c == '.' || c == b) {
                out.write(b);
                logicalByteIndex++;
            } else {
                String msg = "Invalid ascii character '%s' when parsing byte %02x at index %d";
                msg = String.format(msg, c, b, logicalByteIndex);
                throw new ParseException(msg, logicalByteIndex);
            }
        }

        private int cursor() { return logicalByteIndex * 2 + logicalByteIndex / 4; }
        private byte hiNybble() { return HI_NYBL[chars[cursor()]];}
        private byte loNybble() { return LO_NYBL[chars[cursor() + 1]]; }
        private byte getByte() { return (byte)(hiNybble() | loNybble()); }
        private char getAsciiChar() { return chars[logicalByteIndex + 38]; }
    }

    abstract public byte[] parse(final String text);
}

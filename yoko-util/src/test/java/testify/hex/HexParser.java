package testify.hex;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.regex.Pattern;

import static java.lang.Character.toUpperCase;

public enum HexParser {
    HEX_DUMP {
        private final String COMMENT = "--";
        private final Pattern HEX_LINE = Pattern.compile("([0-9 a-f]{8} ){4} \".{1,16}\"");
        private final byte[] HI_NYBL = new byte['g'];
        private final byte[] LO_NYBL = new byte['g'];
        {
            char[] hexChars = "0123456789abcdef".toCharArray();
            for (byte i = 0; i < hexChars.length; i++) {
                LO_NYBL[toUpperCase(hexChars[i])] = LO_NYBL[hexChars[i]] = i;
                HI_NYBL[toUpperCase(hexChars[i])] = HI_NYBL[hexChars[i]] = (byte)(i << 4);
            }
        }

        class InnerParser {
            final OutputStream out;
            final char[] chars;
            int byteIndex = 0;

            private InnerParser(OutputStream out, char[] chars) {
                this.out = out;
                this.chars = chars;
            }

            private boolean hasAnyBytesLeft() {
                // skip any spaces in the hex
                for (int h = byteIndex * 2 + byteIndex / 4; chars[h] == ' ' && byteIndex <= 16; byteIndex++);
                return byteIndex < 16;
            }

            private void writeNextByte() throws IOException, ParseException {
                byte b = getByte();
                char c = getChar();

                // sanity check ascii string
                if (c == '.' || c == b) {
                    out.write(b);
                    byteIndex++;
                } else {
                    String msg = "Invalid ascii character '%s' when parsing byte %02x at index %d";
                    msg = String.format(msg, c, b, byteIndex);
                    throw new ParseException(msg, byteIndex);
                }
            }

            private byte highNybble() {return HI_NYBL[chars[byteIndex * 2 + byteIndex / 4]];}
            private byte lowNybble() {return LO_NYBL[chars[1 + byteIndex * 2 + byteIndex / 4]];}
            private byte getByte() {return (byte)(highNybble()|lowNybble());}
            private char getChar() {return chars[byteIndex+38];}
        }

        public byte[] parse(final String text) {
            if (text == null) return null;
            StringReader sr = new StringReader(text);
            BufferedReader br = new BufferedReader(sr);
            int lineNo = 0;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                String line = null;
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
                        throw new IllegalArgumentException("Could not parse as hex dump: line " + lineNo + " of text:\n" + text );
                    }
                }

                return out.toByteArray();
            } catch (IOException e) {
                throw new Error(e);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Could not parse as hex dump: line " + lineNo + " of text:\n" + text, e);
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

    abstract public byte[] parse(final String text);
}

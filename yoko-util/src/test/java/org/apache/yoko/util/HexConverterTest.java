package org.apache.yoko.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.math.BigInteger;
import java.util.Arrays;

import static org.apache.yoko.util.HexConverter.*;
import static org.apache.yoko.util.HexConverterTest.Util.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class HexConverterTest {
    public static final String[] TEST_DATA = " 00 aa 7f 80 ff 0000 7fff 8000 ffff 00000000 7fffffff 80000000 ffffffff".split(" ");

    static {assert TEST_DATA[0].equals("");} // just in case anyone deletes the leading space

    @Parameters(name = "{0}")
    public static String[] testData() { return TEST_DATA;}

    @Parameter
    public String hex;

    @Test
    public void testAsciiToOctets() {
        assertThat(asciiToOctets(hex), matchesHex(hex));
    }

    @Test
    public void testAsciiToOctetsWithOffset() {
        int hexlen = hex.length();
        switch(hexlen) {
            default:
                fail("Unhandled hex string length: " + hex);
            case 8:
                assertThat(asciiToOctets(hex, hexlen - 6), matchesHex(hex.substring(hexlen - 6)));
            case 6:
                assertThat(asciiToOctets(hex, hexlen - 4), matchesHex(hex.substring(hexlen - 4)));
            case 4:
                assertThat(asciiToOctets(hex, hexlen - 2), matchesHex(hex.substring(hexlen - 2)));
            case 2:
            case 0:
                assertThat(asciiToOctets(hex, 0), matchesHex(hex));
        }
        assertThat(asciiToOctets(hex), matchesHex(hex));
    }

    @Test
    public void testOctetsToAscii() {
        byte[] bytes = bytes(hex);
        switch(bytes.length) {
            default:
                fail("Unhandled byte string length: " + hex);
            case 4:
                assertThat(octetsToAscii(bytes,4), is(hex.substring(0, 8)));
            case 3:
                assertThat(octetsToAscii(bytes,3), is(hex.substring(0, 6)));
            case 2:
                assertThat(octetsToAscii(bytes,2), is(hex.substring(0, 4)));
            case 1:
                assertThat(octetsToAscii(bytes,1), is(hex.substring(0, 2)));
            case 0:
                assertThat(octetsToAscii(bytes,0), is(""));
        }
    }

    enum Util {;
        static Matcher<byte[]> matchesHex(final String hex) {
            return new BaseMatcher<byte[]>(){
                public void describeTo(Description d) {d.appendValue(hex);}
                public void describeMismatch(Object o, Description d) {d.appendValue(hex((byte[])o));}
                public boolean matches(Object o) {return hex.toUpperCase().equals(hex((byte[])o));}
            };
        }

        static String hex(byte[] bytes) {
            String s = "";
            for (byte b: bytes) s += String.format("%02X", b);
            return s;
        }

        static byte[] bytes(String hex) {
            if (hex.isEmpty()) return new byte[0];
            BigInteger bigInt = new BigInteger(hex, 16);
            byte[] bytes = bigInt.toByteArray();
            return padLeft(bytes, hex.length() / 2);
        }

        private static byte[] padLeft(byte[] bytes, int newlen) {
            int oldlen = bytes.length;
            // return unmodified if correct
            if (oldlen == newlen) return bytes;
            // trim if necessary
            if (oldlen > newlen) return Arrays.copyOfRange(bytes, oldlen - newlen, oldlen);
            // padLeft
            byte[] buffer = new byte[newlen];
            System.arraycopy(bytes, 0, buffer, newlen - oldlen, oldlen);
            return buffer;
        }
    }
}


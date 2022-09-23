package testify.hex;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


class HexBuilderTest {

    @Test
    void testBuildHex() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        assertEquals(newHexBuildObject.getClass(), HexBuilder.class);
    }

    @Test
    void testOctWriting() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        int[] listInt = {0x0c, 0x0d, 0x64, 0x80};
        String expected = "0c0d6480";
        String result = newHexBuildObject.oct(listInt).hex();
        assertEquals(expected, result);
    }

    @Test
    void testInvalidOctWriting() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        assertThrows(AssertionError.class, () -> newHexBuildObject.oct(10, 256, 334));
        assertThrows(AssertionError.class, () -> newHexBuildObject.oct(-67));
    }

    @Test
    void test_u_s() {
        HexBuilder firstHexBuildObject = HexBuilder.buildHex();
        String result = firstHexBuildObject.u_s(256).hex();
        assertEquals("0100", result);
        HexBuilder secondHexBuildObject = HexBuilder.buildHex();
        result = secondHexBuildObject.u_s(65535).hex();
        assertEquals("ffff", result);
    }

    @Test
    void testInvalid_u_s() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        assertThrows(AssertionError.class, () -> newHexBuildObject.u_s(65536));
        assertThrows(AssertionError.class, () -> newHexBuildObject.u_s(8725536));
        assertThrows(AssertionError.class, () -> newHexBuildObject.u_s(-67));
    }

    @Test
    void test_u_l() {
        HexBuilder firstHexBuildObject = HexBuilder.buildHex();
        String result = firstHexBuildObject.u_l(65536).hex();
        assertEquals("00010000", result);
        HexBuilder secondHexBuildObject = HexBuilder.buildHex();
        result = secondHexBuildObject.u_l(2147483647).hex();
        assertEquals("7fffffff", result);
        HexBuilder thirdHexBuildObject = HexBuilder.buildHex();
        result = thirdHexBuildObject.u_l(-65536).hex();
        assertEquals("ffff0000", result);
    }

    @Test
    void testSeqHexOddLength() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        String hex = "abc";
        assertThrows(AssertionError.class, () -> newHexBuildObject.seq(hex));
    }

    @Test
    void testSeqHexInvalidExpr() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        String hex = "dq";
        assertThrows(AssertionError.class, () -> newHexBuildObject.seq(hex));
    }

    @Test
    void testSeqHex() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        String hex = "ac12118c";
        String expected = "00000004ac12118c";
        String result = newHexBuildObject.seq(hex).hex();
        assertEquals(expected, result);
    }

    @Test
    void testSeqBytes() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        byte[] byte_array = {0x0a, 0x05, 0x19, 0x64, 0x7f};
        String expected = "000000050a0519647f";
        String result = newHexBuildObject.seq(byte_array).hex();
        assertEquals(expected, result);
    }

    @Test
    void testNonAsciiStr() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        String s = "∞¶#";
        assertThrows(AssertionError.class, () -> newHexBuildObject.str(s));
    }

    @Test
    void testAsciiCharStr() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        String s = "B";
        String expected = "000000024200";
        String result = newHexBuildObject.str(s).hex();
        assertEquals(expected, result);
    }

    @Test
    void testAsciiStr() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        String s = "This message is a secret!";
        String expected = "0000001a54686973206d6573736167652069732061207365637265742100";
        String result = newHexBuildObject.str(s).hex();
        assertEquals(expected, result);
    }

    @Test
    void testUnfinishedCdrEncapsulation() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        HexBuilder hexBuilderCapsule = newHexBuildObject.cdr();
        assertThrows(IllegalStateException.class, hexBuilderCapsule::hex);
    }

    @Test
    void testEmptyCdrEncapsulation() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        HexBuilder hexBuilderCapsule = newHexBuildObject.cdr();
        String expected = "0000000100";
        String result = hexBuilderCapsule.end().hex();
        assertEquals(expected, result);
    }

    @Test
    void testCdrEncapsulation() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        int[] listInt = {0x0c, 0x0d, 0x64, 0x80};
        String expected = "0c0d64800000000100";
        newHexBuildObject.oct(listInt);
        HexBuilder hexBuilderCapsule = newHexBuildObject.cdr();
        String result = hexBuilderCapsule.end().hex();
        assertEquals(expected, result);
    }

    @Test
    void testReturnHexString() {
        HexBuilder newHexBuildObject = HexBuilder.buildHex();
        assertEquals(newHexBuildObject.hex().getClass(), String.class);
    }
}


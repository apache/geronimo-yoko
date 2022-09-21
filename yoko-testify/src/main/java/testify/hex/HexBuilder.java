package testify.hex;

import java.util.Formatter;

/**
 * Builds a hex string by specifying the IDL data types to be written.
 * Only very few types are supported but this should suffice to mock
 * up data for several tests.
 *
 * Pads with 0xBD to align each data type to its natural boundary.
 *
 * Writes lengths for strings, octet sequences, and CDR encapsulations.
 *
 * Always uses a byte order marker of 0x00 (i.e. big-endian).
 */
@SuppressWarnings("UnusedReturnValue")
public class HexBuilder {
    private final Formatter f = new Formatter();
    private int len = 0;

    public static HexBuilder buildHex() {
        return new HexBuilder();
    }

    /** align on an n-byte boundary */
    private HexBuilder align(int n) {
        assert n == 2 || n == 4 || n == 8 || n == 16;
        while (len % n > 0) {
            f.format("%s", "BD");
            len++;
        }
        return this;
    }

    /** write octets */
    public HexBuilder oct(int...ints) {
        for (int i: ints) {
            assert i >> 8 == 0;
            f.format("%02x", 0xff & i);
            len += 1;
        }
        return this;
    }

    /** write an unsigned short */
    public HexBuilder u_s(int i) {
        assert i >> 16 == 0;
        align(2);
        f.format("%04x", 0xffff & i);
        len += 2;
        return this;
    }

    /** write an unsigned long */
    public HexBuilder u_l(int i) {
        align(4);
        f.format("%08x", i);
        len += 4;
        return this;
    }

    /** write a sequence of octets, including the length */
    public HexBuilder seq(String hex) {
        assert hex.length() % 2 == 0;
        assert hex.matches("[0-9A-Fa-f]*");
        final int numBytes = hex.length() / 2;
        u_l(numBytes);
        len += numBytes;
        f.format("%s", hex);
        return this;
    }

    /** write a sequence of octets, including the length */
    public HexBuilder seq(byte[] bytes) {
        final int numOctets = bytes.length;
        u_l(numOctets);
        for (byte b: bytes) oct(b);
        return this;
    }

    /** write a string, including the length and a null terminator */
    public HexBuilder str(String s) {
        assert s.matches("\\p{ASCII}*");
        // CORBA strings need a null terminator, so add 1 to the length
        u_l(s.length() + 1);
        // write each char as a byte
        for (char c: s.toCharArray()) oct(c);
        // write the null terminator
        oct(0);
        return this;
    }

    /** write a CDR encapsulation */
    public HexBuilder cdr() {
        return new HexBuilder() {
            public HexBuilder end() { return HexBuilder.this.seq(super.hex()); }
            public String hex() { throw new IllegalStateException("Unfinished CDR encapsulation"); }
        }.oct(0); // write the BOM as the first octet of this CDR encapsulation
    }

    /** finish the current cdr encapsulation */
    public HexBuilder end() {
        throw new IllegalStateException("Cannot finish a CDR encapsulation because one was not started");
    }

    /** get the hex data **/
    public String hex() {
        try (Formatter f = this.f){
            return f.toString();
        }
    }
}

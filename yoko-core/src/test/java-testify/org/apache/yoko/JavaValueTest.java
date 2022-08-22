package org.apache.yoko;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.CosNaming.NameComponent;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static testify.hex.HexParser.HEX_DUMP;

/**
 * Test writing Java values directly to and reading them back from CDR streams.
 * This test was created in response to a bug where indirections
 * were not supported in CDR streams
 */
class JavaValueTest {
    OutputStream out;
    InputStream in;

    enum NameComponents {;

        static NameComponent stringToName(String idAndKind) {
            String id = idAndKind.replaceFirst("\\..*", "");
            String kind = idAndKind.replaceFirst( "[^.]*\\.?", "");
            if ("null".equals(id)) id = null;
            if ("null".equals(kind)) kind = null;
            //System.out.printf("%s -> '%s'.'%s'%n", idAndKind, id, kind);
            return new NameComponent(id, kind);
        }

        static NameComponent[] stringToPath(String components) {
            components = components.replace('/',' ').trim();
            List<NameComponent> ncs = new ArrayList<>();
            for (String idAndKind : components.split(" ")) ncs.add(stringToName(idAndKind));
            return ncs.toArray(new NameComponent[ncs.size()]);
        }

        static String nameToString(NameComponent nc) {
            return String.format("%s.%s", nc.id, nc.kind);
        }

        static String pathToString(NameComponent[] path) {
            if (path.length == 0) return "";
            String result = "";
            for (NameComponent nc : path) result += "/" + nameToString(nc);
            return result.substring(1);
        }

        static void assertEquals(NameComponent actual, NameComponent expected) {
            assertNotNull(expected);
            assertNotSame(actual, expected);
            Assertions.assertEquals(nameToString(actual), nameToString(expected));
        }

        static void assertEquals(NameComponent[] actual, NameComponent[] expected) {
            assertNotNull(expected);
            assertNotSame(actual, expected);
            Assertions.assertEquals(pathToString(actual), pathToString(expected));
        }
    }

    private void finishWriting() {
        System.out.println(out.getBufferReader().dumpAllData());
        in = out.create_input_stream();
        out = null;
    }

    private void writeHex(String hex) {
        byte[] bytes = HEX_DUMP.parse(hex);
        out.write_octet_array(bytes, 0, bytes.length);
        finishWriting();
    }

    @BeforeEach
    void setupStreams() {
        out = new OutputStream(null, GiopVersion.GIOP1_2);
    }

    @Test
    void marshalTwoDistinctLongs() {
        Long l1 = new Long(2);
        Long l2 = new Long(2);
        out.write_value(l1, Long.class);
        out.write_value(l2, Long.class);
        finishWriting();
        Long l3 = (Long) in.read_value(Long.class);
        Long l4 = (Long) in.read_value(Long.class);
        assertThat(l3, equalTo(l1));
        assertThat(l4, equalTo(l2));
        assertThat(l3, not(sameInstance(l1)));
        assertThat(l4, not(sameInstance(l2)));
        assertThat(l4, not(sameInstance(l3)));
    }

    @Test
    void marshalTheSameLongTwiceToTestValueIndirection() {
        Long l1 = new Long(2);
        Long l2 = l1;
        out.write_value(l1, Long.class);
        out.write_value(l2, Long.class);
        finishWriting();
        Long l3 = (Long) in.read_value(Long.class);
        Long l4 = (Long) in.read_value(Long.class);
        assertThat(l3, equalTo(l1));
        assertThat(l4, equalTo(l2));
        assertThat(l3, not(sameInstance(l1)));
        assertThat(l4, not(sameInstance(l2)));
        assertThat(l4, is(sameInstance(l3)));
    }

    @Test
    void marshalNameComponentAsValue() {
        NameComponent actual = NameComponents.stringToName("hello.world");
        out.write_value(actual, NameComponent.class);
        finishWriting();
        NameComponent expected = (NameComponent) in.read_value(NameComponent.class);
        NameComponents.assertEquals(actual, expected);
    }

    @Test
    void marshalNameComponentArrayAsValue() {
        NameComponent[] actual = NameComponents.stringToPath("hello.dir/world.dir/text.object");
        out.write_value(actual, NameComponent[].class);
        finishWriting();
        NameComponent[] expected = (NameComponent[]) in.read_value(NameComponent[].class);
        NameComponents.assertEquals(actual, expected);
    }

    @Test
    void marshalWstringWithNulls() {
        String expected = "Hello\0world";
        out.write_wstring(expected);
        finishWriting();
        String actual = in.read_wstring();
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    void marshalWstringWithSurrogates() {
        String expected = new String(Character.toChars(Character.MIN_SURROGATE)) + new String(Character.toChars(Character.MAX_CODE_POINT));
        out.write_wstring(expected);
        finishWriting();
        String actual = in.read_wstring();
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    void unmarshalTwoLongValues() {
        writeHex("" +
                "    7fffff02 00000035 524d493a 6a617661  \".......5RMI:java\"\n" +
                "    2e6c616e 672e4c6f 6e673a32 30354636  \".lang.Long:205F6\"\n" +
                "    43434630 30324536 4539303a 33423842  \"CCF002E6E90:3B8B\"\n" +
                "    45343930 43433846 32334446 00bdbdbd  \"E490CC8F23DF....\"\n" +
                "    00000000 00000007 7fffff02 ffffffff  \"................\"\n" +
                "    ffffffb4 bdbdbdbd 00000000 0000000b  \"................\"");
        Long actual1 = (Long)in.read_value(Long.class);
        Long actual2 = (Long)in.read_value(Long.class);
        assertThat(actual1, is(07L));
        assertThat(actual2, is(11l));
    }

    @Test
    void unmarshalLongValueAndIndirection() throws Exception {
        writeHex("" +
                "    7fffff02 00000035 524d493a 6a617661  \".......5RMI:java\"\n" +
                "    2e6c616e 672e4c6f 6e673a32 30354636  \".lang.Long:205F6\"\n" +
                "    43434630 30324536 4539303a 33423842  \"CCF002E6E90:3B8B\"\n" +
                "    45343930 43433846 32334446 00bdbdbd  \"E490CC8F23DF....\"\n" +
                "    00000000 00000002 ffffffff ffffffb4  \"................\"");
        Long actual1 = (Long)in.read_value(Long.class);
        Long actual2 = (Long)in.read_value(Long.class);
        assertThat(actual1, is(2L));
        assertThat(actual2, is(2L));
        assertThat(actual1, is(theInstance(actual2)));
    }

    @Test
    void unmarshalNameComponentValue() {
        writeHex("" +
                "    7fffff02 00000046 524d493a 6f72672e  \".......FRMI:org.\"\n" +
                "    6f6d672e 436f734e 616d696e 672e4e61  \"omg.CosNaming.Na\"\n" +
                "    6d65436f 6d706f6e 656e743a 45303638  \"meComponent:E068\"\n" +
                "    41373543 39383933 30443636 3a463136  \"A75C98930D66:F16\"\n" +
                "    34413231 39344136 36323832 4100bdbd  \"4A2194A66282A...\"\n" +
                "    00000006 68656c6c 6f00bdbd 00000006  \"....hello.......\"\n" +
                "    776f726c 6400                        \"world.\"");
        NameComponent actual = (NameComponent)in.read_value(NameComponent.class);
        NameComponent expected = NameComponents.stringToName("hello.world");
        NameComponents.assertEquals(expected, actual);
    }

    @Test
    void unmarshalNameComponentChunkedValue() {
        writeHex("" +
                "    7fffff0a 00000046 524d493a 6f72672e  \".......FRMI:org.\"\n" +
                "    6f6d672e 436f734e 616d696e 672e4e61  \"omg.CosNaming.Na\"\n" +
                "    6d65436f 6d706f6e 656e743a 45303638  \"meComponent:E068\"\n" +
                "    41373543 39383933 30443636 3a463136  \"A75C98930D66:F16\"\n" +
                "    34413231 39344136 36323832 4100bdbd  \"4A2194A66282A...\"\n" +
                "    00000016 00000006 68656c6c 6f00bdbd  \"........hello...\"\n" +
                "    00000006 776f726c 64000000 ffffffff  \"....world.......\"");
        NameComponent actual = (NameComponent)in.read_value(NameComponent.class);
        NameComponent expected = NameComponents.stringToName("hello.world");
        NameComponents.assertEquals(expected, actual);
    }

    @Test
    void unmarshalNameComponentArray() {
        writeHex("" +
                "7fffff02 00000049 524d493a 5b4c6f72  \".......IRMI:[Lor\"\n" +
                "672e6f6d 672e436f 734e616d 696e672e  \"g.omg.CosNaming.\"\n" +
                "4e616d65 436f6d70 6f6e656e 743b3a45  \"NameComponent;:E\"\n" +
                "30363841 37354339 38393330 4436363a  \"068A75C98930D66:\"\n" +
                "46313634 41323139 34413636 32383241  \"F164A2194A66282A\"\n" +
                "00000000 00000001 7fffff0a 00000046  \"...............F\"\n" +
                "524d493a 6f72672e 6f6d672e 436f734e  \"RMI:org.omg.CosN\"\n" +
                "616d696e 672e4e61 6d65436f 6d706f6e  \"aming.NameCompon\"\n" +
                "656e743a 45303638 41373543 39383933  \"ent:E068A75C9893\"\n" +
                "30443636 3a463136 34413231 39344136  \"0D66:F164A2194A6\"\n" +
                "36323832 41000000 00000016 00000006  \"6282A...........\"\n" +
                "68656c6c 6f00bdbd 00000006 776f726c  \"hello.......worl\"\n" +
                "64000000 ffffffff                    \"d.......\"");
        NameComponent[] actual = (NameComponent[])in.read_value(NameComponent[].class);
        NameComponent[] expected = NameComponents.stringToPath("hello.world");
        NameComponents.assertEquals(expected, actual);
    }

    @Test
    void unmarshalNameComponentArrayFromNeo() {
        writeHex("" +
                "7fffff02 00000049 524d493a 5b4c6f72  \".......IRMI:[Lor\"\n" +
                "672e6f6d 672e436f 734e616d 696e672e  \"g.omg.CosNaming.\"\n" +
                "4e616d65 436f6d70 6f6e656e 743b3a45  \"NameComponent;:E\"\n" +
                "30363841 37354339 38393330 4436363a  \"068A75C98930D66:\"\n" +
                "46313634 41323139 34413636 32383241  \"F164A2194A66282A\"\n" +
                "00000000 00000001 7fffff0a 00000046  \"...............F\"\n" +
                "524d493a 6f72672e 6f6d672e 436f734e  \"RMI:org.omg.CosN\"\n" +
                "616d696e 672e4e61 6d65436f 6d706f6e  \"aming.NameCompon\"\n" +
                "656e743a 45303638 41373543 39383933  \"ent:E068A75C9893\"\n" +
                "30443636 3a463136 34413231 39344136  \"0D66:F164A2194A6\"\n" +
                "36323832 41000000 00000025 0000001b  \"6282A......%....\"\n" +
                "5265736f 6c766162 6c65436f 734e616d  \"ResolvableCosNam\"\n" +
                "696e6743 6865636b 65720000 00000001  \"ingChecker......\"\n" +
                "00000000 ffffffff                    \"........\"");
        NameComponent[] actual = (NameComponent[])in.read_value(NameComponent[].class);
        NameComponent[] expected = NameComponents.stringToPath("ResolvableCosNamingChecker");
        NameComponents.assertEquals(expected, actual);
    }
}

package org.apache.yoko;

import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static testify.hex.HexParser.HEX_DUMP;
import static testify.matchers.ByteArrayMatchers.matchesHex;

/**
 * Test writing Java values directly to and reading them back from CDR streams.
 * This test was created in response to a bug where indirections
 * were not supported in CDR streams
 */
class JavaValueNullFieldsTest {
    private static String NULL_VALUE_HEX = "" +
            "0000:  7fffff02 0000004c 524d493a 6f72672e  \".......LRMI:org.\"\n" +
            "0010:  61706163 68652e79 6f6b6f2e 4e6f6e53  \"apache.yoko.NonS\"\n" +
            "0020:  65726961 6c697a61 626c6548 6f6c6465  \"erializableHolde\"\n" +
            "0030:  723a3338 41383146 45314245 36324537  \"r:38A81FE1BE62E7\"\n" +
            "0040:  39463a43 45323535 37343935 34453145  \"9F:CE25574954E1E\"\n" +
            "0050:  42383200 ab572ac7 00bdbdbd 00000000  \"B82..W*.........\"";

    OutputStream out;
    byte[] data;
    InputStream in;
    @BeforeEach
    void setupStreams() {
        out = new OutputStream(null, GiopVersion.GIOP1_2);
    }

    @Test
    void marshalNullFields() {
        out.write_value(new NonSerializableHolder());
        finishWriting();
        assertThat(data, matchesHex(NULL_VALUE_HEX));
    }

    @Test
    void unmarshalNullFields() {
        writeHex(NULL_VALUE_HEX);
        finishWriting();
        NonSerializableHolder holder = (NonSerializableHolder) in.read_value();
        assertThat(holder.eyecatcher, is(0xAB572AC7));
        assertThat(holder.value, is(nullValue()));
    }

    private void finishWriting() {
        // create a new reader view of the written data
        ReadBuffer rb = out.getBufferReader();
        // print it out as hex (non-destructive)
        System.out.println(rb.dumpAllData());
        // capture the data in a byte array (destructive: nothing left to read)
        data = new byte[rb.available()];
        rb.readBytes(data);
        // prepare an input stream using the written data
        in = out.create_input_stream();
    }

    private void writeHex(String hex) {
        byte[] bytes = HEX_DUMP.parse(hex);
        out.write_octet_array(bytes, 0, bytes.length);
        finishWriting();
    }
}

class NonSerializable {}

class NonSerializableHolder implements Serializable {
    int eyecatcher = 0xAB572AC7;
    NonSerializable value = null;
}

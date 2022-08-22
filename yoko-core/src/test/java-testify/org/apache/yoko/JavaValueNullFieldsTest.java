package org.apache.yoko;

import acme.AbstractInterface;
import acme.AbstractValue;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.apache.yoko.util.yasf.Yasf;
import org.apache.yoko.util.yasf.YasfThreadLocal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import testify.jupiter.annotation.logging.Logging;

import java.io.Serializable;
import java.util.EnumSet;

import static org.apache.yoko.util.yasf.Yasf.ENUM_FIXED;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static testify.matchers.ByteArrayMatchers.matchesHex;

/**
 * Test writing Java values directly to and reading them back from CDR streams.
 * This test was created in response to a bug where indirections
 * were not supported in CDR streams
 */
class JavaValueNullFieldsTest {

    private static final EnumSet<Yasf> OLD_STYLE = EnumSet.of(ENUM_FIXED);

    OutputStream out;
    byte[] data;
    InputStream in;
    @BeforeEach
    void setupStreams() {
        out = new OutputStream(null, GiopVersion.GIOP1_2);
        out = (OutputStream) ORB.init().create_output_stream();
    }

    @Test
    void marshalNullFieldsOldStyle() {
        YasfThreadLocal.push(OLD_STYLE);
        try {
            out.write_value(new HolderC());
            finishWriting();
            assertThat(data, matchesHex("" +
                    "0000:  7fffff02 0000003e 524d493a 6f72672e  \".......>RMI:org.\"\n" +
                    "0010:  61706163 68652e79 6f6b6f2e 486f6c64  \"apache.yoko.Hold\"\n" +
                    "0020:  6572433a 31413139 34423133 33444133  \"erC:1A194B133DA3\"\n" +
                    "0030:  30313446 3a313546 42373643 34413836  \"014F:15FB76C4A86\"\n" +
                    "0040:  42434630 4500bdbd aaaaaaaa 00bdbdbd  \"BCF0E...........\"\n" +
                    "0050:  00000000 bbbbbbbb 00000000 cccccccc  \"................\"\n" +
                    "0060:  00000000                             \"....\""));
            assertHolderUnmarshalledCorrectly(in.read_value());
        } finally {
            YasfThreadLocal.pop();
        }
    }

    @Test
    void marshalNonSerializableFieldsOldStyle() {
        YasfThreadLocal.push(OLD_STYLE);
        try {
            HolderA holder = new HolderA();
            holder.valueA = new NonSerializable();
            Assertions.assertThrows(MARSHAL.class, () -> out.write_value(holder));
        } finally {
            YasfThreadLocal.pop();
        }
    }

    @Test
    void marshalSerializableFieldsOldStyle() {
        YasfThreadLocal.push(OLD_STYLE);
        try {
            HolderA holder = new HolderA();
            holder.valueA = new SerializableChild();
            out.write_value(holder);
            finishWriting();
            assertThat(data, matchesHex("" +
                    "0000:  7fffff02 0000003e 524d493a 6f72672e  \".......>RMI:org.\"\n" +
                    "0010:  61706163 68652e79 6f6b6f2e 486f6c64  \"apache.yoko.Hold\"\n" +
                    "0020:  6572413a 46373241 46443130 30423538  \"erA:F72AFD100B58\"\n" +
                    "0030:  43394544 3a343931 35323330 34333833  \"C9ED:49152304383\"\n" +
                    "0040:  35394532 4300bdbd aaaaaaaa 00bdbdbd  \"59E2C...........\"\n" +
                    "0050:  7fffff02 00000048 524d493a 6f72672e  \".......HRMI:org.\"\n" +
                    "0060:  61706163 68652e79 6f6b6f2e 53657269  \"apache.yoko.Seri\"\n" +
                    "0070:  616c697a 61626c65 4368696c 643a3037  \"alizableChild:07\"\n" +
                    "0080:  31444138 42453746 39373131 32383a32  \"1DA8BE7F971128:2\"\n" +
                    "0090:  31444343 36464241 30333644 46434600  \"1DCC6FBA036DFCF.\""));
            HolderA actual = (HolderA) in.read_value();
            assertThat(actual, notNullValue());
            assertThat(actual.valueA, notNullValue());
            assertThat(actual.valueA, instanceOf(SerializableChild.class));
        } finally {
            YasfThreadLocal.pop();
        }
    }

    @Test
    @Logging("yoko")
    void marshalNullFields() {
        out.write_value(new HolderC());
        finishWriting();
        assertThat(data, matchesHex("" +
                "0000:  7fffff02 0000003e 524d493a 6f72672e  \".......>RMI:org.\"\n" +
                "0010:  61706163 68652e79 6f6b6f2e 486f6c64  \"apache.yoko.Hold\"\n" +
                "0020:  6572433a 31413139 34423133 33444133  \"erC:1A194B133DA3\"\n" +
                "0030:  30313446 3a313546 42373643 34413836  \"014F:15FB76C4A86\"\n" +
                "0040:  42434630 4500bdbd aaaaaaaa 00000000  \"BCF0E...........\"\n" +
                "0050:  bbbbbbbb 00000000 cccccccc 00000000  \"................\""));
        Object holder = in.read_value();
        assertHolderUnmarshalledCorrectly(holder);
    }

    @Test
    void marshalNonSerializableFields() {
        HolderA holder = new HolderA();
        holder.valueA = new NonSerializable();
        Assertions.assertThrows(MARSHAL.class, () -> out.write_value(holder));
    }

    @Test
    void marshalSerializableFields() {
        HolderA holder = new HolderA();
        holder.valueA = new SerializableChild();
        out.write_value(holder);
        finishWriting();
        assertThat(data, matchesHex("" +
                "0000:  7fffff02 0000003e 524d493a 6f72672e  \".......>RMI:org.\"\n" +
                "0010:  61706163 68652e79 6f6b6f2e 486f6c64  \"apache.yoko.Hold\"\n" +
                "0020:  6572413a 46373241 46443130 30423538  \"erA:F72AFD100B58\"\n" +
                "0030:  43394544 3a343931 35323330 34333833  \"C9ED:49152304383\"\n" +
                "0040:  35394532 4300bdbd aaaaaaaa 7fffff02  \"59E2C...........\"\n" +
                "0050:  00000048 524d493a 6f72672e 61706163  \"...HRMI:org.apac\"\n" +
                "0060:  68652e79 6f6b6f2e 53657269 616c697a  \"he.yoko.Serializ\"\n" +
                "0070:  61626c65 4368696c 643a3037 31444138  \"ableChild:071DA8\"\n" +
                "0080:  42453746 39373131 32383a32 31444343  \"BE7F971128:21DCC\"\n" +
                "0090:  36464241 30333644 46434600           \"6FBA036DFCF.\""));
        HolderA actual = (HolderA) in.read_value();
        assertThat(actual, notNullValue());
        assertThat(actual.valueA, notNullValue());
        assertThat(actual.valueA, instanceOf(SerializableChild.class));
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

    private void assertHolderUnmarshalledCorrectly(Object o) {
        HolderC holder = (HolderC) o;
        assertThat(holder.eyecatcherA, is(0xAAAAAAAA));
        assertThat(holder.valueA, is(nullValue()));
        assertThat(holder.eyecatcherB, is(0xBBBBBBBB));
        assertThat(holder.valueB, is(nullValue()));
        assertThat(holder.eyecatcherC, is(0xCCCCCCCC));
        assertThat(holder.valueC, is(nullValue()));
    }
}

class NonSerializable {}

class SerializableChild extends NonSerializable implements Serializable {}

class HolderA implements Serializable {
    int eyecatcherA = 0xAAAAAAAA;
    NonSerializable valueA = null;
}

class HolderB extends HolderA {
    int eyecatcherB = 0xBBBBBBBB;
    AbstractValue valueB = null;
}

class HolderC extends HolderB {
    int eyecatcherC = 0xCCCCCCCC;
    AbstractInterface valueC = null;
}

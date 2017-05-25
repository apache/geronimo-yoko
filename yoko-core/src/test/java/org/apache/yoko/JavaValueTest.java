package org.apache.yoko;

import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA_2_3.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test writing Java values directly to and reading them back from CDR streams.
 * This test was created in response to a bug where indirections
 * were not supported in CDR streams
 */
public class JavaValueTest {
    OutputStream out;
    @Before
    public void setupStreams() {
        ORB orb = (ORB)ORB.init((String[]) null, (Properties) null);
        out = (OutputStream) orb.create_output_stream();
    }

    @Test
    public void marshalTwoDistinctLongs() {
        Long l1 = new Long(2);
        Long l2 = new Long(2);
        out.write_value(l1, Long.class);
        out.write_value(l2, Long.class);
        InputStream in = (InputStream) out.create_input_stream();
        Long l3 = (Long) in.read_value(Long.class);
        Long l4 = (Long) in.read_value(Long.class);
        assertThat(l3, equalTo(l1));
        assertThat(l4, equalTo(l2));
        assertThat(l3, not(sameInstance(l1)));
        assertThat(l4, not(sameInstance(l2)));
        assertThat(l4, not(sameInstance(l3)));
    }


    @Test
    public void marshalTheSameLongTwice() {
        Long l1 = new Long(2);
        Long l2 = l1;
        out.write_value(l1, Long.class);
        out.write_value(l2, Long.class);
        InputStream in = (InputStream) out.create_input_stream();
        Long l3 = (Long) in.read_value(Long.class);
        Long l4 = (Long) in.read_value(Long.class);
        assertThat(l3, equalTo(l1));
        assertThat(l4, equalTo(l2));
        assertThat(l3, not(sameInstance(l1)));
        assertThat(l4, not(sameInstance(l2)));
        assertThat(l4, is(sameInstance(l3)));
    }


}

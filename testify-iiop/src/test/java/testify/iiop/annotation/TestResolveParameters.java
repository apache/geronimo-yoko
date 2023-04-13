package testify.iiop.annotation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import testify.bus.Bus;
import testify.bus.StringSpec;
import testify.iiop.annotation.ConfigureServer.BeforeServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.oneOf;
import static testify.iiop.annotation.ConfigureServer.ServerName.DEFAULT_SERVER;

@ConfigureServer
public class TestResolveParameters {

    enum StringKey implements StringSpec {POA_NAME}

    static final String SERVER_NAME = DEFAULT_SERVER.name();

    @BeforeServer
    public static void beforeServer(Bus bus, POA poa) {
        bus.put(StringKey.POA_NAME, poa.the_name());
    };

    @Test
    void testResolveServerPOA(Bus bus) {
        assertThat(bus.get(StringKey.POA_NAME), is(DEFAULT_SERVER.name()));
    }

    @Test
    void testResolveOrb(ORB orb) {
        assertThat(orb, is(notNullValue()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b"})
    void testResolveStringFromValueSource(String string, ORB orb) {
        assertThat(orb, is(notNullValue()));
        assertThat(string, is(oneOf("a", "b")));
    }
}

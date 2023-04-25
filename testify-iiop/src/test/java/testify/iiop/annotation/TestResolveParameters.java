package testify.iiop.annotation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import testify.bus.Bus;
import testify.iiop.annotation.ConfigureServer.BeforeServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.oneOf;
import static testify.iiop.annotation.ConfigureServer.ServerName.DEFAULT_SERVER;

@ConfigureServer
public class TestResolveParameters {

    /** Somewhere to store things supplied to the @BeforeServer method */
    enum ServerParams {
        ;
        static Bus bus;
        static ORB orb;
        static POA poa;
    }

    @BeforeServer
    public static void beforeServer(Bus bus, ORB orb, POA poa) {
        // store these parameters away so they can be tested later
        ServerParams.bus = bus;
        ServerParams.orb = orb;
        ServerParams.poa = poa;
    };

    @Test
    void testResolveBeforeServerParams(Bus bus) {
        // check that non-null parameters were supplied to the @BeforeServer method
        assertThat(ServerParams.bus, is(notNullValue()));
        assertThat(ServerParams.orb, is(notNullValue()));
        assertThat(ServerParams.poa, is(notNullValue()));
        // check the supplied POA has the name of the test server
        assertThat(ServerParams.poa.the_name(), is(DEFAULT_SERVER.name()));
    }

    @Test
    void testOrbParameterResolvesCorrectly(ORB orb) {
        assertThat(orb, is(notNullValue()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b"})
    void testParameterizedTestDoesNotConflictWithConfigureServerAsAParameterResolver(String string, ORB orb) {
        assertThat(orb, is(notNullValue()));
        assertThat(string, is(oneOf("a", "b")));
    }
}

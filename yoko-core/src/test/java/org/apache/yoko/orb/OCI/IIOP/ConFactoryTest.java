package org.apache.yoko.orb.OCI.IIOP;

import org.apache.yoko.orb.OCI.ConFactory;
import org.apache.yoko.orb.OCI.Connector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CSIIOP.TransportAddress;
import org.omg.IOP.IOR;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;
import org.omg.IOP.TAG_CSI_SEC_MECH_LIST;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;
import testify.hex.HexParser;
import testify.jupiter.annotation.iiop.ConfigureOrb;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static testify.hex.HexBuilder.buildHex;

@ConfigureOrb
@ExtendWith(MockitoExtension.class)
class ConFactoryTest {
    private static final int TAG_UNKNOWN = -1;

    // build hex strings semantically
    private static final String
            ANYDATA = "DEADC0DE", // we will use this wherever the content doesn't matter
            IOP_1_0 = buildHex().oct(0,1,0).str("HAL9000").u_s(2001).seq(ANYDATA).hex(),
            IOP_1_1 = buildHex().oct(0,1,1).str("deepthought").u_s(42).seq(ANYDATA).u_l(0).hex(),
            ALT_ADR = buildHex().oct(0,1,1).str("holly").u_s(1988).seq(ANYDATA).u_l(1)
                    .u_l(TAG_ALTERNATE_IIOP_ADDRESS.value).cdr().str("holly").u_s(1999).end().hex(),
            CSI_SEC = buildHex().oct(0,1,1).str("MU-TH-R").u_s(182).seq(ANYDATA).u_l(1)
                    .u_l(TAG_CSI_SEC_MECH_LIST.value).cdr().seq(ANYDATA).end().hex();

    private static final TaggedProfile
            UNKNOWN_PROFILE = profile(TAG_UNKNOWN, ANYDATA),
            IOP_1_0_PROFILE = profile(TAG_INTERNET_IOP.value, IOP_1_0),
            IOP_1_1_PROFILE = profile(TAG_INTERNET_IOP.value, IOP_1_1),
            ALT_ADR_PROFILE = profile(TAG_INTERNET_IOP.value, ALT_ADR),
            CSI_SEC_PROFILE = profile(TAG_INTERNET_IOP.value, CSI_SEC);

    private final ListenerMap lm = new ListenerMap();

    private static TaggedProfile profile(int tag, String hex) {
        return new TaggedProfile(tag, HexParser.HEX_STRING.parse(hex));
    }

    private static IOR ior(TaggedProfile...profiles) {
        return new IOR("IDL:Location_Service:1.0", profiles);
    }

    @Mock(name = "Mock ExtendedConnectionHelper", answer = Answers.CALLS_REAL_METHODS)
    public ExtendedConnectionHelper mockHelper;

    private ConFactory impl;
    private Connector[] connectors;

    @BeforeEach
    public void setup(ORB orb) throws Exception{
        this.connectors = null;
        when(mockHelper.tags()).thenReturn(new int[]{TAG_CSI_SEC_MECH_LIST.value});
        this.impl = new ConFactory_impl(orb, true, lm, mockHelper.getUnifiedConnectionHelper());
    }

    private String create_connectors(TaggedProfile...profiles) {
        IOR ior = ior(profiles);
        Policy[] policies = {};
        this.connectors = impl.create_connectors(ior, policies);
        for (Connector c: connectors) System.out.printf("connector: '%s'%n", c);
        // now summarize them into a handy string form
        return stream(connectors)
                .map(Connector::get_info)
                .map(Object::toString)
                .collect(joining(" "));
    }

    @Test
    void testEmptyIOR(){
        create_connectors();
        assertThat(connectors, is(arrayWithSize(0)));
    }

    @Test
    void testUnknownProfile(){
        create_connectors(UNKNOWN_PROFILE);
        assertThat(connectors, is(arrayWithSize(0)));
    }

    @Test
    void testIOP_1_0_Profile(){
        assertThat(create_connectors(IOP_1_0_PROFILE), is("[HAL9000:2001]"));
    }

    @Test
    void testIOP_1_1_Profile(){
        assertThat(create_connectors(IOP_1_1_PROFILE), is("[deepthought:42]"));
    }

    @Test
    void testAlternate_IIOP_ADDRESS_Profile(){
        assertThat(create_connectors(ALT_ADR_PROFILE), is("[holly:1988] [holly:1999]"));
    }

    @Test
    void testMultipleProfiles(){
        assertThat(create_connectors(IOP_1_0_PROFILE, IOP_1_1_PROFILE, ALT_ADR_PROFILE), is("[HAL9000:2001] [deepthought:42] [holly:1988] [holly:1999]"));
    }

    private void setMockHelperEndpoints(TransportAddress...endpoints) {
        when(mockHelper.getEndpoints(any(TaggedComponent.class), any(Policy[].class))).thenReturn(endpoints);
    }

    @Test
    void testCSIProfileWithNoEndpoints(){
        setMockHelperEndpoints();
        assertThat(create_connectors(CSI_SEC_PROFILE), is("[MU-TH-R:182]"));
    }

    @Test
    void testCSIProfileWithOneEndpoint(){
        setMockHelperEndpoints(new TransportAddress("WOPR", (short)1983));
        assertThat(create_connectors(CSI_SEC_PROFILE), is("[WOPR:1983]"));
    }

    @Test
    void testMultipleProfilesWithCSIButNoEndpoints() {
        setMockHelperEndpoints();
        assertThat(create_connectors(IOP_1_0_PROFILE, IOP_1_1_PROFILE, ALT_ADR_PROFILE, CSI_SEC_PROFILE),
                is("[HAL9000:2001] [deepthought:42] [holly:1988] [holly:1999] [MU-TH-R:182]"));
    }

    @Test
    void testMultipleProfilesWithCSIWithOneEndpoint() {
        setMockHelperEndpoints(new TransportAddress("WOPR", (short)1983));
        assertThat(create_connectors(IOP_1_0_PROFILE, IOP_1_1_PROFILE, ALT_ADR_PROFILE, CSI_SEC_PROFILE), is("[WOPR:1983]"));
    }
}

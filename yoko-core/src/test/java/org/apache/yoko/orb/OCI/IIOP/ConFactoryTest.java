package org.apache.yoko.orb.OCI.IIOP;

import org.apache.yoko.orb.OCI.ConFactory;
import org.apache.yoko.orb.OCI.Connector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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

import static org.mockito.ArgumentMatchers.any;
import static testify.hex.HexBuilder.buildHex;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class ConFactoryTest {
    private static final int TAG_UNKNOWN = -1;

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

    public ORB orb = ORB.init((String[]) null, null);

    @Mock
    public ExtendedConnectionHelper mockHelper = mock(ExtendedConnectionHelper.class, withSettings().verboseLogging());

    private ConFactory impl;
    private Connector[] connectors;
    private String connectorsDesc;

    @Before
    public void setup() throws Exception{
        this.connectors = null;
        this.connectorsDesc = null;
        when(mockHelper.tags()).thenReturn(new int[]{TAG_CSI_SEC_MECH_LIST.value});
        when(mockHelper.getUnifiedConnectionHelper()).thenCallRealMethod();
        this.impl = new ConFactory_impl(orb, true, lm, mockHelper.getUnifiedConnectionHelper());
    }

    private void create_connectors(TaggedProfile...profiles) {
        IOR ior = ior(profiles);
        Policy[] policies = {};
        this.connectors = impl.create_connectors(ior, policies);
        // now summarize them into a handy string form
        String s = "";
        for (Connector conn: connectors) {
            s += conn.get_info().toString() + " ";
        }
        this.connectorsDesc = s.replaceFirst(" $", "");
    }

    @Test
    public void testEmptyIOR(){
        create_connectors();
        assertThat(connectors, is(arrayWithSize(0)));
    }

    @Test
    public void testUnknownProfile(){
        create_connectors(UNKNOWN_PROFILE);
        assertThat(connectors, is(arrayWithSize(0)));
    }

    @Test
    public void testIOP_1_0_Profile(){
        create_connectors(IOP_1_0_PROFILE);
        assertThat(connectorsDesc, is("[HAL9000:2001]"));
    }

    @Test
    public void testIOP_1_1_Profile(){
        create_connectors(IOP_1_1_PROFILE);
        assertThat(connectorsDesc, is("[deepthought:42]"));
    }

    @Test
    public void testAlternate_IIOP_ADDRESS_Profile(){
        create_connectors(ALT_ADR_PROFILE);
        assertThat(connectorsDesc, is("[holly:1988] [holly:1999]"));
    }

    @Test
    public void testMultipleProfiles(){
        create_connectors(IOP_1_0_PROFILE, IOP_1_1_PROFILE, ALT_ADR_PROFILE);
        assertThat(connectorsDesc, is("[HAL9000:2001] [deepthought:42] [holly:1988] [holly:1999]"));
    }

    private void setMockHelperEndpoints(TransportAddress...endpoints) {
        when(mockHelper.getEndpoints(any(TaggedComponent.class), any(Policy[].class))).thenReturn(endpoints);
    }

    @Test
    public void testCSIProfileWithNoEndpoints(){
        setMockHelperEndpoints();
        create_connectors(CSI_SEC_PROFILE);
        assertThat(connectorsDesc, is("[MU-TH-R:182]"));
    }

    @Test
    public void testCSIProfileWithOneEndpoint(){
        setMockHelperEndpoints(new TransportAddress("WOPR", (short)1983));
        create_connectors(CSI_SEC_PROFILE);
        assertThat(connectorsDesc, is("[WOPR:1983]"));
    }

    @Test
    public void testMultipleProfilesWithCSIButNoEndpoints() {
        setMockHelperEndpoints();
        create_connectors(IOP_1_0_PROFILE, IOP_1_1_PROFILE, ALT_ADR_PROFILE, CSI_SEC_PROFILE);
        assertThat(connectorsDesc, is("[HAL9000:2001] [deepthought:42] [holly:1988] [holly:1999] [MU-TH-R:182]"));
    }

    @Test
    public void testMultipleProfilesWithCSIWithOneEndpoint() {
        setMockHelperEndpoints(new TransportAddress("WOPR", (short)1983));
        create_connectors(IOP_1_0_PROFILE, IOP_1_1_PROFILE, ALT_ADR_PROFILE, CSI_SEC_PROFILE);
        assertThat(connectorsDesc, is("[WOPR:1983]"));
    }
}

package org.apache.yoko.orb.OCI.IIOP;

import org.apache.yoko.orb.OCI.ConFactory;
import org.apache.yoko.orb.OCI.Connector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;
import org.omg.IOP.TAG_CSI_SEC_MECH_LIST;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.IOP.TaggedProfile;
import test.util.HexParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;


public class ConFactoryTest {
    private static final int TAG_UNKNOWN = -1;
    private static final int TAG_IOP = TAG_INTERNET_IOP.value;
    private static final HexParser HEX = HexParser.HEX_STRING;
    private static final TaggedProfile
            UNKNOWN_PROFILE = new TaggedProfile(TAG_UNKNOWN, HEX.parse("cafebabedeadbeef")),
            IOP_1_0_PROFILE = new TaggedProfile(TAG_IOP, HEX.parse(""+
                    "000100BD"+ // BOM, major, minor, PAD
                    "0000000a 6c6f6361 6c686f73 74000af9"+ // localhost:2809
                    "00000000"  // empty object key
            ));
    private final ListenerMap lm = new ListenerMap();

    private static IOR ior(TaggedProfile...profiles) {
        return new IOR("IDL:Location_Service:1.0", profiles);
    }

    public ORB orb = ORB.init((String[]) null, null);

    @Mock
    public ExtendedConnectionHelper mockHelper = mock(ExtendedConnectionHelper.class, withSettings().verboseLogging());

    private ConFactory impl;
    private Connector[] connectors;

    @Before
    public void setup() throws Exception{
        this.connectors = null;
        this.impl = new ConFactory_impl(orb, true, lm, mockHelper);
        when(mockHelper.tags()).thenReturn(new int[]{TAG_CSI_SEC_MECH_LIST.value});
    }

    private void create_connectors(TaggedProfile...profiles) {
        IOR ior = ior(profiles);
        Policy[] policies = {};
        this.connectors = impl.create_connectors(ior, policies);
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
        assertThat(connectors, is(arrayWithSize(1)));
    }
}

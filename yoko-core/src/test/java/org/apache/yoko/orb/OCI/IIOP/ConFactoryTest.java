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
import org.omg.IOP.TaggedProfile;
import test.util.HexParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;


public class ConFactoryTest {
    public static final int TAG_UNKNOWN = -1;
    private static final HexParser HEX = HexParser.HEX_STRING;
    public static final TaggedProfile UNKNOWN_PROFILE = new TaggedProfile(TAG_UNKNOWN, HEX.parse("cafebabedeadbeef"));
    public final ListenerMap lm = new ListenerMap();

    public static final IOR EMPTY_IOR = new IOR("IDL:Location_Service:1.0", array());

    private static TaggedProfile[] array(TaggedProfile...profiles) { return profiles; }

    @Mock
    public ORB mockOrb = mock(ORB.class, withSettings().verboseLogging());
    @Mock
    public ExtendedConnectionHelper mockHelper = mock(ExtendedConnectionHelper.class, withSettings().verboseLogging());

    ConFactory impl;

    @Before
    public void setup() {
        impl = new ConFactory_impl(mockOrb, true, lm, mockHelper);
        when(mockHelper.tags()).thenReturn(new int[]{TAG_CSI_SEC_MECH_LIST.value});
    }

    @Test
    public void testEmptyIOR(){
        Connector[] connectors = impl.create_connectors(EMPTY_IOR, new Policy[]{});
        assertThat(connectors, is(arrayWithSize(0)));
    }
}

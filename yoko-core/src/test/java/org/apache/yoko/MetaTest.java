package org.apache.yoko;

import org.junit.Before;
import org.junit.Test;

public class MetaTest extends AbstractOrbTestBase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setWaitForFile("Test.ref");
    }

    @Test
    public void testMeta() throws Exception {
        client.invokeMain("test.meta.TestMeta");
    }
}

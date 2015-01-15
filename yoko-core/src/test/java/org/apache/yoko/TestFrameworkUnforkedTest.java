package org.apache.yoko;

public class TestFrameworkUnforkedTest extends TestFrameworkTest {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("server.forked","false");
        System.setProperty("client.forked","false");
    }

}

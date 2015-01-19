/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.yoko;

public class PoaTest extends AbstractOrbTestBase {

    public void setUp() throws Exception {
        super.setUp();
        setWaitForFile("Test.ref");
    }
    public void testActivate() throws Exception {
        client.invokeMain("test.poa.TestActivate");
    }

    public void testDeactivate() throws Exception {
        client.invokeMain("test.poa.TestDeactivate");
    }

    public void testCollocated() throws Exception {
        client.invokeMain("test.poa.TestCollocated");
    }

    public void testCreate() throws Exception {
        client.invokeMain("test.poa.TestCreate");
    }

    public void testDestroy() throws Exception {
        client.invokeMain("test.poa.TestDestroy");
    }

    public void testFind() throws Exception {
        client.invokeMain("test.poa.TestFind");
    }

    public void testMisc() throws Exception {
        client.invokeMain("test.poa.TestMisc");
    }

    public void testDefaultServant() throws Exception {
        runServerClientTest("test.poa.TestDefaultServantServer");
    }

    public void testServantActivatorServer() throws Exception {
        runServerClientTest("test.poa.TestServantActivatorServer");
    }

    public void testServantLocatorServer() throws Exception {
        runServerClientTest("test.poa.TestServantLocatorServer");
    }

    public void testLocationForwardServer() throws Exception {
        runServerClientTest("test.poa.TestLocationForwardServerMain", "test.poa.TestLocationForwardClient");
    }

    public void testAdapterActivatorServer() throws Exception {
        runServerClientTest("test.poa.TestAdapterActivatorServer");
    }

    public void testPoaManagerServer() throws Exception {
        runServerClientTest("test.poa.TestPOAManagerServer", "test.poa.TestPOAManagerClient");
    }

    public void testDispatchStrategyServer() throws Exception {
        runServerClientTest("test.poa.TestDispatchStrategyServer", "test.poa.TestDispatchStrategyClient");
    }

    public void testMultipleOrbsServer() throws Exception {
        runServerClientTest("test.poa.TestMultipleOrbsServer", "test.poa.TestMultipleOrbsClient");
    }

    public void testMultipleOrbsThreadedServer() throws Exception {
        runServerClientTest("test.poa.TestMultipleOrbsThreadedServer", "test.poa.TestMultipleOrbsThreadedClient");
    }

    private void runServerClientTest(String serverClass) throws Exception {
        runServerClientTest(serverClass, "test.poa.TestClient");
    }
}

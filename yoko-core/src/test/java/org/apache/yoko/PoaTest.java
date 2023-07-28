/*
 * Copyright 2019 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko;

import test.poa.TestAdapterActivatorServer;
import test.poa.TestClient;
import test.poa.TestCollocated;
import test.poa.TestCreate;
import test.poa.TestDeactivate;
import test.poa.TestDefaultServantServer;
import test.poa.TestDestroy;
import test.poa.TestDispatchStrategyClient;
import test.poa.TestDispatchStrategyServer;
import test.poa.TestFind;
import test.poa.TestLocationForwardClient;
import test.poa.TestLocationForwardServerMain;
import test.poa.TestMisc;
import test.poa.TestMultipleOrbsClient;
import test.poa.TestMultipleOrbsServer;
import test.poa.TestMultipleOrbsThreadedClient;
import test.poa.TestMultipleOrbsThreadedServer;
import test.poa.TestPOAManagerClient;
import test.poa.TestPOAManagerServer;
import test.poa.TestServantActivatorServer;
import test.poa.TestServantLocatorServer;

public class PoaTest extends AbstractOrbTestBase {

    public void setUp() throws Exception {
        super.setUp();
        setWaitForFile("Test.ref");
    }

    public void testDeactivate() throws Exception {
        client.invokeMain(TestDeactivate.class);
    }

    public void testCollocated() throws Exception {
        client.invokeMain(TestCollocated.class);
    }

    public void testCreate() throws Exception {
        client.invokeMain(TestCreate.class);
    }

    public void testDestroy() throws Exception {
        client.invokeMain(TestDestroy.class);
    }

    public void testFind() throws Exception {
        client.invokeMain(TestFind.class);
    }

    public void testMisc() throws Exception {
        client.invokeMain(TestMisc.class);
    }

    public void testDefaultServant() throws Exception {
        runServerClientTest(TestDefaultServantServer.class);
    }

    public void testServantActivatorServer() throws Exception {
        runServerClientTest(TestServantActivatorServer.class);
    }

    public void testServantLocatorServer() throws Exception {
        runServerClientTest(TestServantLocatorServer.class);
    }

    public void testLocationForwardServer() throws Exception {
        runServerClientTest(TestLocationForwardServerMain.class, TestLocationForwardClient.class);
    }

    public void testAdapterActivatorServer() throws Exception {
        runServerClientTest(TestAdapterActivatorServer.class);
    }

    public void testPoaManagerServer() throws Exception {
        runServerClientTest(TestPOAManagerServer.class, TestPOAManagerClient.class);
    }

    public void testDispatchStrategyServer() throws Exception {
        runServerClientTest(TestDispatchStrategyServer.class, TestDispatchStrategyClient.class);
    }

    public void testMultipleOrbsServer() throws Exception {
        runServerClientTest(TestMultipleOrbsServer.class, TestMultipleOrbsClient.class);
    }

    public void testMultipleOrbsThreadedServer() throws Exception {
        runServerClientTest(TestMultipleOrbsThreadedServer.class, TestMultipleOrbsThreadedClient.class);
    }

    private void runServerClientTest(Class<?> serverClass) throws Exception {
        runServerClientTest(serverClass, TestClient.class);
    }
}

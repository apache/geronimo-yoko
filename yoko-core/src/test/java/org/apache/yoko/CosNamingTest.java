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

import test.tnaming.ClientForReadOnlyNameService;
import test.tnaming.ClientForReadWriteNameService;
import test.tnaming.ServerWithReadWriteIntegralNameService;
import test.tnaming.ServerWithReadOnlyIntegralNameService;
import test.tnaming.ServerWithReadWriteStandaloneNameService;

public class CosNamingTest extends AbstractOrbTestBase {
    public void testReadOnlyIntegralNameService() throws Exception {
        final String refFile = "readonlyintegralnameservice.ref";
        setWaitForFile(refFile);
        runServerClientTest(ServerWithReadOnlyIntegralNameService.class, ClientForReadOnlyNameService.class, refFile);
    }

    public void testReadWriteIntegralNameService() throws Exception {
        final String refFile = "readwriteintegralnameservice.ref";
        setWaitForFile(refFile);
        runServerClientTest(ServerWithReadWriteIntegralNameService.class, ClientForReadWriteNameService.class, refFile);
    }

    public void testReadWriteStandaloneNameService() throws Exception {
        final String refFile = "readwritestandalonenameservice.ref";
        setWaitForFile(refFile);
        runServerClientTest(ServerWithReadWriteStandaloneNameService.class, ClientForReadWriteNameService.class, refFile);
    }
}

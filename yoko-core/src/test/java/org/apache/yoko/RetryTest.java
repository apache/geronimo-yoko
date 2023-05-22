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

import test.retry.Client;
import test.retry.Server;

public class RetryTest extends AbstractOrbTestBase {
    private static final Class<?> SERVER_CLASS = Server.class;
    private static final Class<?> CLIENT_CLASS = Client.class;
    public void testRetry() throws Exception {
        setWaitForFile("Test.ref");
        runServerClientTest(SERVER_CLASS, CLIENT_CLASS);
    }
}

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import test.obv.Client;
import test.obv.Server;

import java.io.File;

public class ObvTest extends TestCase {
    private static final Class<?> SERVER_CLASS = Server.class;
    private static final Class<?> CLIENT_CLASS = Client.class;
    private static final File waitForFile = new File("TestOBV.ref");
        
    String[] serverArgs, clientArgs;
        

    public static TestSuite suite() {
        return AbstractMatrixOrbTestBase.generateTestSuite(SERVER_CLASS, CLIENT_CLASS, waitForFile);
    }
}

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


/**
 * @version $Rev: 554983 $ $Date: 2007-07-10 08:38:17 -0700 (Tue, 10 Jul 2007) $
 */
package org.apache.yoko;

import java.io.File;

public class IIOPPluginTest extends AbstractOrbTestBase {
    private static final String SERVER_CLASS = "test.iiopplugin.Server";
    private static final String CLIENT_CLASS = "test.iiopplugin.Client";
    private static final String OUTPUT_FILE = "Test.ref";

    public void setUp() throws Exception {
        super.setUp();
        setWaitForFile(new File(OUTPUT_FILE));
    }
    public void testLocal() throws Exception {
        runServerClientTest(SERVER_CLASS, CLIENT_CLASS);
    }
}


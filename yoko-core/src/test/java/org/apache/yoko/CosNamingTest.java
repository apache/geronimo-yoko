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
 * @version $Rev: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */
package org.apache.yoko;

import test.tnaming.ClientForReadOnlyNameService;
import test.tnaming.ClientForWritableNameService;
import test.tnaming.ServerWithIntegralNameService;
import test.tnaming.ServerWithReadOnlyNameService;
import test.tnaming.ServerWithStandaloneNameService;
import test.tnaming.Util;

public class CosNamingTest extends AbstractOrbTestBase {
    
    public void testStandaloneNameService() throws Exception {
        final String refFile = "standalone.ref";
        setWaitForFile(refFile);
        runServerClientTest(ServerWithStandaloneNameService.class, ClientForWritableNameService.class, refFile);
    }
    
    public void testIntegralNameService() throws Exception {
        final String refFile = "integral.ref";
        setWaitForFile(refFile);
        runServerClientTest(ServerWithIntegralNameService.class, ClientForWritableNameService.class, refFile);
    }
    
    public void testReadOnlyNameService() throws Exception {
        final String refFile = "readonly.ref";
        setWaitForFile(refFile);
        runServerClientTest(ServerWithReadOnlyNameService.class, ClientForReadOnlyNameService.class, refFile);
    }
}

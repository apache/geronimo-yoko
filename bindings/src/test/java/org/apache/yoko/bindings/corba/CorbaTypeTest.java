/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.yoko.bindings.corba;

import java.io.IOException;

import javax.xml.namespace.QName;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

public class CorbaTypeTest extends AbstractTypeTestClient1 {
    protected static final String WSDL_PATH = "/wsdl/type_test/type_test_corba.wsdl";
    protected static final QName SERVICE_NAME = new QName("http://apache.org/type_test/corba", "TypeTestCORBAService");
    protected static final QName PORT_NAME = new QName("http://apache.org/type_test/corba", "TypeTestCORBAPort");

    static boolean serverStarted = false;
    
    public CorbaTypeTest(String name) {
        super(name);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite(CorbaTypeTest.class);
        return new CorbaTypeTestSetup(suite) {
                public void setUp() throws Exception {
                    super.setUp();
                    initClient(AbstractTypeTestClient1.class, SERVICE_NAME, PORT_NAME, WSDL_PATH);
                }
            };
    }

    public static void main(String[] args) throws Exception {
        initClient(AbstractTypeTestClient.class, SERVICE_NAME, PORT_NAME, WSDL_PATH);
        junit.textui.TestRunner.run(CorbaTypeTest.class);       
    }

    public void testOneway() throws Exception {
    }

    //STAX output incorrect from CXF.
    public void testQName() throws Exception {
    }
                
    public void testSimpleAll() throws Exception {
    }
        
    static abstract class CorbaTypeTestSetup extends TestSetup {

        ServerLauncher sl = null;

        public CorbaTypeTestSetup(Test tester) {
            super(tester);
            boolean startServer = !Boolean.getBoolean("NO_SERVER_START");
            boolean debug = Boolean.getBoolean("DEBUG");
            if (startServer) {
                java.util.Map<String, String> properties = new java.util.HashMap<String, String>();
                properties.put("java.endorsed.dirs", System.getProperty("java.endorsed.dirs"));
                sl = new ServerLauncher(CorbaTypeTestServer.class.getName(), properties, null, debug);
            }
        }

        public void setUp() throws Exception {
            if (sl != null) {
                boolean ok = sl.launchServer();
                assertTrue("failed to launch server", ok);
            }
        }

        public void tearDown() throws Exception {
            boolean passed = true;
            if (sl != null) {
                try { 
                    sl.signalStop();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try { 
                    passed = passed && sl.stopServer(); 
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            System.gc();
            assertTrue("server failed", passed);
        }
    }

}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.yoko.bindings.corba;

import java.io.File;
import java.util.HashMap;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.cxf.jaxb.JAXBUtils;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.apache.cxf.wsdl.WSDLManager;
import org.apache.cxf.wsdl11.WSDLManagerImpl;

import org.apache.schemas.yoko.idl.opnames.M1M1Interface;
import org.apache.schemas.yoko.idl.opnames.M1M1InterfaceCORBAService;
import org.apache.schemas.yoko.idl.opnames.M2M2Interface;
import org.apache.schemas.yoko.idl.opnames.M2M2InterfaceCORBAService;

import junit.framework.TestCase;

public class CorbaOperationNameManglingTest extends TestCase {

    private final QName INTERFACE1_PORT_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/OpNames", "M1.M1InterfaceCORBAPort"); 
    
    private final QName INTERFACE1_SERVICE_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/OpNames", "M1.M1InterfaceCORBAService"); 
    
    private final QName INTERFACE2_PORT_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/OpNames", "M2.M2InterfaceCORBAPort"); 
    
    private final QName INTERFACE2_SERVICE_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/OpNames", "M2.M2InterfaceCORBAService"); 
    
    private final static String WSDL_LOCATION = "/wsdl/OpNames.wsdl";
    private final static int MAX_WAIT_COUNT = 15;
    
    private static TestInterface1Server server1;
    private static TestInterface2Server server2;
    private static boolean testServerReady;
    private M1M1Interface client1;
    private M2M2Interface client2;
    private URL wsdlUrl;

    public CorbaOperationNameManglingTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaOperationNameManglingTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
       
        if (server1 == null) {
            server1 = new TestInterface1Server();
            server1.start();
        }

        int waitCount = 0;
        // Wait for the server to start if it hasn't already
        while (waitCount < MAX_WAIT_COUNT && !server1.isReady()) {
            try {
                Thread.sleep(1000);
                waitCount++;
            } catch (Exception ex) {
                // Consume
            }
        }

        if (!server1.isReady()) {
            throw new Exception("Server1 failed to start in a timely fashion");
        }
        
        if (server2 == null) {
            server2 = new TestInterface2Server();
            server2.start();
        }

        waitCount = 0;
        // Wait for the server to start if it hasn't already
        while (waitCount < MAX_WAIT_COUNT && !server2.isReady()) {
            try {
                Thread.sleep(1000);
                waitCount++;
            } catch (Exception ex) {
                // Consume
            }
        }

        if (!server2.isReady()) {
            throw new Exception("Server2 failed to start in a timely fashion");
        }

        // Now initialize the client-side
        try {
            wsdlUrl = M1M1Interface.class.getResource(WSDL_LOCATION);
        } catch (Exception ex) {
            throw new Exception("Unable to resolve WSDL location");
        }
        
        M1M1InterfaceCORBAService service1 = 
            new M1M1InterfaceCORBAService(wsdlUrl, INTERFACE1_SERVICE_NAME);
        client1 = service1.getPort(INTERFACE1_PORT_NAME, M1M1Interface.class);

        if (client1 == null) {
            throw new Exception("Unable to create client1");
        }
        
        M2M2InterfaceCORBAService service2 = 
            new M2M2InterfaceCORBAService(wsdlUrl, INTERFACE2_SERVICE_NAME);
        client2 = service2.getPort(INTERFACE2_PORT_NAME, M2M2Interface.class);

        if (client2 == null) {
            throw new Exception("Unable to create client2");
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        server1.interrupt();

        try {
            // Sleep for 3 seconds waiting for the server to shut down
            Thread.sleep(3000);
        } catch (Exception ex) {
            // Move on to check if the server is down
        }

        if (server1.isAlive()) {
            throw new Exception("Did not terminate test server!");
        }
        
        server2.interrupt();

        try {
            // Sleep for 3 seconds waiting for the server to shut down
            Thread.sleep(3000);
        } catch (Exception ex) {
            // Move on to check if the server is down
        }

        if (server2.isAlive()) {
            throw new Exception("Did not terminate test server!");
        }
    }

    public void testM1M1InterfaceTestOp() {
        String result = null;
        try {
            result = client1.testOp();
        } catch (Exception ex) {
            ex.printStackTrace();
            assertTrue(false);
        }
        assertNotNull(result);
        assertTrue(result.equals("M1Interface"));
    }
    
    public void testM2M2InterfaceTestOp() {
        String result = null;
        try {
            result = client2.m2M2InterfaceTestOp();
        } catch (Exception ex) {
            ex.printStackTrace();
            assertTrue(false);
        }
        assertNotNull(result);
        assertTrue(result.equals("M2Interface"));
    }

    
    // A small test server for the test case to interact with
    public class TestInterface1Server extends Thread {
        private boolean serverReady;

        public TestInterface1Server() {
            serverReady = false;
        }

        public void run() {
            Object implementor = new M1InterfaceImpl();
            String address = "corbaloc::localhost:11111/M1Interface";
            Endpoint ep = Endpoint.create("http://schemas.apache.org/yoko/bindings/corba", implementor);
            String wsdlLocation = getClass().getResource("/wsdl/OpNames.wsdl").toString();
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("javax.xml.ws.wsdl.description", wsdlLocation);
            ep.setProperties(props);
            ep.publish(address);
            serverReady = true;
        }

        public boolean isReady() {
            return serverReady;
        }
    }

    // A small test server for the test case to interact with
    public class TestInterface2Server extends Thread {
        private boolean serverReady;

        public TestInterface2Server() {
            serverReady = false;
        }

        public void run() {
            Object implementor = new M2InterfaceImpl();
            String address = "corbaloc::localhost:22222/M2Interface";
            Endpoint ep = Endpoint.create("http://schemas.apache.org/yoko/bindings/corba", implementor);
            String wsdlLocation = getClass().getResource("/wsdl/OpNames.wsdl").toString();
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("javax.xml.ws.wsdl.description", wsdlLocation);
            ep.setProperties(props);
            ep.publish(address);
            serverReady = true;
        }

        public boolean isReady() {
            return serverReady;
        }
    }

    @WebService(portName = "M1.M1InterfaceCORBAPort",
                          serviceName = "M1.M1InterfaceCORBAService",
                          targetNamespace = "http://schemas.apache.org/yoko/idl/OpNames",
                          endpointInterface = "org.apache.schemas.yoko.idl.opnames.M1M1Interface")
    public class M1InterfaceImpl implements M1M1Interface {
       public String testOp() {
           return "M1Interface";
       }
    }

    @WebService(portName = "M2.M2InterfaceCORBAPort",
                          serviceName = "M2.M2InterfaceCORBAService",
                          targetNamespace = "http://schemas.apache.org/yoko/idl/OpNames",
                          endpointInterface = "org.apache.schemas.yoko.idl.opnames.M2M2Interface")
    public class M2InterfaceImpl implements M2M2Interface {
       public String m2M2InterfaceTestOp() {
           return "M2Interface";
       }
    }
}

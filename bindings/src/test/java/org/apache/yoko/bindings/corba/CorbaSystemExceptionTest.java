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

import org.apache.schemas.yoko.idl.systemex.SystemExceptionTester;
import org.apache.schemas.yoko.idl.systemex.SystemExceptionTesterCORBAService;

import junit.framework.TestCase;

public class CorbaSystemExceptionTest extends TestCase {

    public final static int COMM_FAILURE = 1;
    public final static int TRANSIENT = 2;
    public final static int NO_PERMISSION = 3;
   
    private final QName PORT_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/SystemEx", "SystemExceptionTesterCORBAPort"); 
    
    private final QName SERVICE_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/SystemEx", "SystemExceptionTesterCORBAService"); 
    
    private final static String WSDL_LOCATION = "/wsdl/SystemEx.wsdl";
    private final static int MAX_WAIT_COUNT = 15;
    
    private static TestServer server;
    private static boolean testServerReady;
    private SystemExceptionTester client;
    private URL wsdlUrl;

    public CorbaSystemExceptionTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaSystemExceptionTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
       
        if (server == null) {
            server = new TestServer();
            server.start();
        }

        int waitCount = 0;
        // Wait for the server to start if it hasn't already
        while (waitCount < MAX_WAIT_COUNT && !server.isReady()) {
            try {
                Thread.sleep(1000);
                waitCount++;
            } catch (Exception ex) {
                // Consume
            }
        }

        if (!server.isReady()) {
            throw new Exception("Server failed to start in a timely fashion");
        }

        // Now initialize the client-side
        try {
            wsdlUrl = SystemExceptionTester.class.getResource(WSDL_LOCATION);
        } catch (Exception ex) {
            throw new Exception("Unable to resolve WSDL location");
        }
        
        SystemExceptionTesterCORBAService service = 
            new SystemExceptionTesterCORBAService(wsdlUrl, SERVICE_NAME);
        client = service.getPort(PORT_NAME, SystemExceptionTester.class);

        if (client == null) {
            throw new Exception("Unable to create client");
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        server.interrupt();

        try {
            // Sleep for 3 seconds waiting for the server to shut down
            Thread.sleep(3000);
        } catch (Exception ex) {
            // Move on to check if the server is down
        }

        if (server.isAlive()) {
            throw new Exception("Did not terminate test server!");
        }
    }

    public void testCommFailureException() throws Exception {
        try {
            client.testSystemException(COMM_FAILURE);
        } catch (org.omg.CORBA.COMM_FAILURE ex) {
            assertTrue(true);
            return;
        } catch (Exception ex) {
            Throwable t = ex.getCause();
            if ((t != null) && (t instanceof org.omg.CORBA.COMM_FAILURE)) {
                assertTrue(true);
                return;
            }
        }
        assertTrue(false);
    }

    public void testTransientException() throws Exception {
        try {
            client.testSystemException(TRANSIENT);
        } catch (org.omg.CORBA.TRANSIENT ex) {
            assertTrue(true);
            return;
        } catch (Exception ex) {
            Throwable t = ex.getCause();
            if ((t != null) && (t instanceof org.omg.CORBA.TRANSIENT)) {
                assertTrue(true);
                return;
            }
        }
        assertTrue(false);
    }

    public void testNoPermissionException() throws Exception {
        try {
            client.testSystemException(NO_PERMISSION);
        } catch (org.omg.CORBA.NO_PERMISSION ex) {
            assertTrue(true);
            return;
        } catch (Exception ex) {
            Throwable t = ex.getCause();
            if ((t != null) && (t instanceof org.omg.CORBA.NO_PERMISSION)) {
                assertTrue(true);
                return;
            }
        }
        assertTrue(false);
    }

    // A small test server for the test case to interact with
    public class TestServer extends Thread {
        private boolean serverReady;

        public TestServer() {
            serverReady = false;
        }

        public void run() {
            Object implementor = new SystemExceptionTesterImpl();
            String address = "corbaloc::localhost:43210/SystemExTest";
            Endpoint ep = Endpoint.create("http://schemas.apache.org/yoko/bindings/corba", implementor);
            String wsdlLocation = getClass().getResource("/wsdl/SystemEx.wsdl").toString();
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

    // A minimal bank implementation to test object references
    @WebService(portName = "SystemExceptionTesterCORBAPort",
                          serviceName = "SystemExceptionTesterCORBAService",
                          targetNamespace = "http://schemas.apache.org/yoko/idl/SystemEx",
                          endpointInterface = "org.apache.schemas.yoko.idl.systemex.SystemExceptionTester")
    public class SystemExceptionTesterImpl implements SystemExceptionTester {
        public void testSystemException(int exId) {
            switch(exId) {
                case COMM_FAILURE:
                    throw new org.omg.CORBA.COMM_FAILURE();
                case TRANSIENT:
                    throw new org.omg.CORBA.TRANSIENT();
                case NO_PERMISSION:
                    throw new org.omg.CORBA.NO_PERMISSION();
            }
            // simply return if we don't match the exception id.  The client will flag this as a
            // failure.
        }
    }
}

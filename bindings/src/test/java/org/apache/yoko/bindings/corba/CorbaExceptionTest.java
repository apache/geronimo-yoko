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

import org.apache.schemas.idl.except.ExceptionTest;
import org.apache.schemas.idl.except.ExceptionTestCORBAService;
import org.apache.schemas.idltypes.except.ExceptionTestReviewData;
import org.apache.schemas.idltypes.except.ExceptionTestReviewDataResult;

import junit.framework.TestCase;

public class CorbaExceptionTest extends TestCase {

    private final QName PORT_NAME = 
        new QName("http://schemas.apache.org/idl/except", "ExceptionTestCORBAPort"); 
    
    private final QName SERVICE_NAME = 
        new QName("http://schemas.apache.org/idl/except", "ExceptionTestCORBAService"); 
        

    private final static String WSDL_LOCATION = "/wsdl/exceptions.wsdl";
    private final static int MAX_WAIT_COUNT = 15;
    
    private static TestServer server;
    private static boolean testServerReady;
    private ExceptionTest client;
    private URL wsdlUrl;

    public CorbaExceptionTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaExceptionTest.class);
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
            wsdlUrl = ExceptionTest.class.getResource(WSDL_LOCATION);
        } catch (Exception ex) {
            throw new Exception("Unable to resolve WSDL location");
        }
        
        ExceptionTestCORBAService service = 
            new ExceptionTestCORBAService(wsdlUrl, SERVICE_NAME);
        client = service.getPort(PORT_NAME, ExceptionTest.class);

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

    public void testBadRecordException() throws Exception {
        try {
            ExceptionTestReviewData data = new ExceptionTestReviewData();
            data.setData("testbadrecord");
            client.reviewData(data);
        } catch (org.apache.schemas.idl.except.BadRecord ex) {
            assertTrue(true);
            assertEquals("BadRecord exception does not contain proper values",
                         "testReason",
                         ex.getFaultInfo().getReason());
            return;
        } catch (Exception ex) {
            Throwable t = ex.getCause();
            if ((t != null) && (t instanceof org.apache.schemas.idl.except.BadRecord)) {
                assertTrue(true);
                org.apache.schemas.idl.except.BadRecord recEx =
                    (org.apache.schemas.idl.except.BadRecord) t;
                assertEquals("BadRecord exception does not contain proper values",
                             "testReason",
                             recEx.getFaultInfo().getReason());
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
            Object implementor = new ExceptionTestImpl();
            String address = "corbaloc::localhost:40000/except";
            Endpoint ep = Endpoint.create("http://schemas.apache.org/yoko/bindings/corba", implementor);
            String wsdlLocation = getClass().getResource("/wsdl/exceptions.wsdl").toString();
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
    @WebService(portName = "ExceptionTestCORBAPort",
                          serviceName = "ExceptionTestCORBAService",
                          targetNamespace = "http://schemas.apache.org/idl/except",
                          endpointInterface = "org.apache.schemas.idl.except.ExceptionTest")
    public class ExceptionTestImpl implements ExceptionTest {
        public ExceptionTestReviewDataResult reviewData(ExceptionTestReviewData exId)
            throws org.apache.schemas.idl.except.BadRecord {
            org.apache.schemas.idltypes.except.BadRecord rec = new org.apache.schemas.idltypes.except.BadRecord();
            rec.setReason("testReason");
            rec.setCode((short)10);
            throw new org.apache.schemas.idl.except.BadRecord("test", rec);                                                              
        }
    }
}

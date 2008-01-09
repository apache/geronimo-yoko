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
import javax.xml.ws.BindingType;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;


import org.apache.cxf.jaxb.JAXBUtils;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.apache.cxf.wsdl.WSDLManager;
import org.apache.cxf.wsdl11.WSDLManagerImpl;

import org.apache.schemas.yoko.idl.objectref.TestDefaultObjectParam;
import org.apache.schemas.yoko.idl.objectref.TestDefaultObjectParamResponse;
import org.apache.schemas.yoko.idl.objectref.TestDefaultObjectReturn;
import org.apache.schemas.yoko.idl.objectref.TestDefaultObjectReturnResponse;
import org.apache.schemas.yoko.idl.objectref.TestInterface;
import org.apache.schemas.yoko.idl.objectref.TestInterfaceCORBAService;
import org.apache.schemas.yoko.idl.objectref.TestObject;
import org.apache.schemas.yoko.idl.objectref.TestObjectCORBAService;
import org.apache.yoko.bindings.corba.utils.CorbaBindingHelper;
import org.apache.yoko.orb.CORBA.ORB;

import junit.framework.TestCase;

public class CorbaObjectReferenceTest extends TestCase {

    private final QName OBJECT_PORT_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/ObjectRef", "TestObjectCORBAPort"); 
    
    private final QName OBJECT_PORT_TYPE = 
        new QName("http://schemas.apache.org/yoko/idl/ObjectRef", "TestObject"); 
    
    private final QName OBJECT_SERVICE_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/ObjectRef", "TestObjectCORBAService"); 
    
    private final QName INTERFACE_PORT_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/ObjectRef", "TestInterfaceCORBAPort"); 

    private final QName INFERRED_INTERFACE_PORT_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/ObjectRef", "TestInterfaceCORBAPort"); 

    private final QName INTERFACE_SERVICE_NAME = 
        new QName("http://schemas.apache.org/yoko/idl/ObjectRef", "TestInterfaceCORBAService"); 
    
    private final static String WSDL_LOCATION = "/wsdl/ObjectRef.wsdl";
    private final static int MAX_WAIT_COUNT = 15;
    
    private static TestServer server;
    private static boolean testServerReady;
    private TestInterface client;
    private URL wsdlUrl;

    public CorbaObjectReferenceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaObjectReferenceTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
       
        if (server == null) {
            System.out.println("Initializing object reference support test...");
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
        if (client == null) {
            try {
                wsdlUrl = TestInterface.class.getResource(WSDL_LOCATION);
            } catch (Exception ex) {
                throw new Exception("Unable to resolve WSDL location");
            }

            TestInterfaceCORBAService service = 
                new TestInterfaceCORBAService(wsdlUrl, INTERFACE_SERVICE_NAME);
            client = service.getPort(INTERFACE_PORT_NAME, TestInterface.class);
        }
        
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

    public void testCustomObjectParam() {
        System.out.println("Testing custom object reference as a parameter...");
        EndpointReferenceType ref = null;
        ref = createEndpointReferenceType("CustomObjectParam", false);

        boolean result = client.testCustomObjectParam(ref);
        assertTrue(result);
    }

    public void testDefaultObjectParam() {
        System.out.println("Testing default object reference as a parameter...");
        EndpointReferenceType ref = null;
        ref = createEndpointReferenceType("DefaultObjectParam", false);

        boolean result = client.testDefaultObjectParam(ref);
        assertTrue(result);
    }

    public void testCustomObjectReturn() throws Exception {
        System.out.println("Testing custom object reference as a return value...");
        EndpointReferenceType ref = client.testCustomObjectReturn();
        TestObject testObj = createObjectFromEndpointReferenceType(ref);

        int updateVal = 789;

        int startVal = testObj.testObjectValue();

        testObj.testObjectValueUpdate(updateVal);
        int endVal = testObj.testObjectValue();

        assertTrue(endVal == updateVal);
    }

    public void testDefaultObjectReturn() throws Exception {
        System.out.println("Testing default object reference as a return value...");
        EndpointReferenceType ref = client.testDefaultObjectReturn();

        assertNotNull(ref.getAddress().getValue());

        QName interfaceName = EndpointReferenceUtils.getInterfaceName(ref);
        assertNull(interfaceName);

        String wsdlLocation = EndpointReferenceUtils.getWSDLLocation(ref);
        assertNull(wsdlLocation);

        QName serviceName = EndpointReferenceUtils.getServiceName(ref);
        assertNull(serviceName);

        String portName = EndpointReferenceUtils.getPortName(ref);
        assertNull(portName);
    }

    public void testNilObjectParam() {
        System.out.println("Testing nil object reference as a parameter...");
        boolean result = client.testNilObjectParam(null);
        assertTrue(result);
    }
    
    public void testNilObjectReturn() {
        System.out.println("Testing nil object reference as a return value...");
        EndpointReferenceType result = client.testNilObjectReturn();
        assertTrue(result == null);
    }
    
    public void testInferredObjectParam() {
        EndpointReferenceType ref = null;
        ref = createEndpointReferenceType("InferredObjectParam", false);
        assertTrue(client.testInferredObjectParam(ref));      
    }
    
    public void testInferredObjectReturn() {
        
        EndpointReferenceType ref = client.testInferredObjectReturn();

        assertNotNull(ref.getAddress().getValue());

        QName interfaceName = EndpointReferenceUtils.getInterfaceName(ref);
        assertNotNull(interfaceName);

        String wsdlLocation = EndpointReferenceUtils.getWSDLLocation(ref);
        assertNotNull(wsdlLocation);

        QName serviceName = EndpointReferenceUtils.getServiceName(ref);
        assertNotNull(serviceName);

        String portName = EndpointReferenceUtils.getPortName(ref);
        assertNotNull(portName);
    }
    
 
    // Helper methods that can be used throughout the test
    public EndpointReferenceType createEndpointReferenceType(String name, boolean serverSide) {
        String corbaAddress = null;
        // The server and client bindings need to be on two different ORBs since they can't
        // share.  So we need to make sure that the port numbers are different.
        if (serverSide) {
            corbaAddress = "corbaloc::localhost:54321/Server" + name;
        } else {
            corbaAddress = "corbaloc::localhost:54322/Client" + name;
        }

        Object testObj = new TestObjectImpl();
        Endpoint ep = Endpoint.create("http://schemas.apache.org/yoko/bindings/corba", testObj);
        String wsdlLocation = getClass().getResource("/wsdl/ObjectRefTestService.wsdl").toString();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("javax.xml.ws.wsdl.description", wsdlLocation);
        ep.setProperties(props);
        ep.publish(corbaAddress);

        EndpointReferenceType ref =
            EndpointReferenceUtils.getEndpointReference(wsdlUrl,
                                                        OBJECT_SERVICE_NAME,
                                                        OBJECT_PORT_NAME.getLocalPart());
        EndpointReferenceUtils.setInterfaceName(ref, OBJECT_PORT_TYPE);
        
        // get the real IOR rather than the corbaloc address information
        corbaAddress = resolveAddressFromEndpoint(corbaAddress, ep);
        EndpointReferenceUtils.setAddress(ref, corbaAddress);

        return ref;
    }

    private String resolveAddressFromEndpoint(String corbaAddress, Endpoint ep) {
        String addr = corbaAddress;
        EndpointImpl epImpl = (EndpointImpl)ep;
        addr = epImpl.getServer().getDestination().getAddress().getAddress().getValue();
        return addr;
    }

    public TestObject createObjectFromEndpointReferenceType(EndpointReferenceType epr) throws Exception {
            WSDLManager manager = null;
            manager = new WSDLManagerImpl();

            QName interfaceName = EndpointReferenceUtils.getInterfaceName(epr);
            QName serviceName = EndpointReferenceUtils.getServiceName(epr);
            String portName = EndpointReferenceUtils.getPortName(epr);

            QName port = new QName(serviceName.getNamespaceURI(), portName);

            StringBuffer seiName = new StringBuffer();
            seiName.append("org.apache.schemas.yoko.idl.objectref.");
            seiName.append(JAXBUtils.nameToIdentifier(interfaceName.getLocalPart(),
                           JAXBUtils.IdentifierType.INTERFACE));

            Class<?> sei = null;
            sei = Class.forName(seiName.toString(), true, manager.getClass().getClassLoader());

            Service service = Service.create(wsdlUrl, serviceName);
            TestObject testObj = (TestObject)service.getPort(port, sei);

            Map<String, Object> requestContext = ((BindingProvider)testObj).getRequestContext();
            requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epr.getAddress().getValue());

            return testObj;
    }

    
    
    // A small test server for the test case to interact with
    public class TestServer extends Thread {
        private boolean serverReady;

        public TestServer() {
            serverReady = false;
        }

        public void run() {
            Object implementor = new TestInterfaceImpl();
            String address = "corbaloc::localhost:54321/ObjectRefTest";
            Endpoint ep = Endpoint.create("http://schemas.apache.org/yoko/bindings/corba", implementor);
            String wsdlLocation = getClass().getResource("/wsdl/ObjectRef.wsdl").toString();
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


    
    // A minimal TestInterface implementation to test object references
    @WebService(portName = "TestInterfaceCORBAPort",
                serviceName = "TestInterfaceCORBAService",
                targetNamespace = "http://schemas.apache.org/yoko/idl/ObjectRef",
                endpointInterface = "org.apache.schemas.yoko.idl.objectref.TestInterface")
    public class TestInterfaceImpl implements TestInterface {

        public boolean testCustomObjectParam(EndpointReferenceType param) {
            TestObject testObj = null;
            try {
                testObj = createObjectFromEndpointReferenceType(param);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

            if (testObj == null) {
                return false;
            }

            try {
                int updateVal = 123;
                int startVal = testObj.testObjectValue();

                testObj.testObjectValueUpdate(updateVal);
                int endVal = testObj.testObjectValue();

                return (endVal == updateVal);
            } catch(Exception ex) {
                return false;
            }
        }

        public boolean testDefaultObjectParam(EndpointReferenceType param) {           
            if (param.getAddress().getValue() == null) {
                return false;
            }

            QName interfaceName = EndpointReferenceUtils.getInterfaceName(param);
            String wsdlLocation = EndpointReferenceUtils.getWSDLLocation(param);
            QName serviceName = EndpointReferenceUtils.getServiceName(param);
            String portName = EndpointReferenceUtils.getPortName(param);

            if (interfaceName != null ||
                wsdlLocation != null ||
                serviceName != null ||
                portName != null) {
                return false;
            }

            return true;
        }

        public EndpointReferenceType testCustomObjectReturn() {
            EndpointReferenceType ref = null;
            ref = createEndpointReferenceType("CustomObjectReturn", true);

            return ref;
        }

        public EndpointReferenceType testDefaultObjectReturn() {
            EndpointReferenceType ref = null;
            ref = createEndpointReferenceType("DefaultObjectReturn", true);

            return ref;
        }

        public boolean testNilObjectParam(EndpointReferenceType param) {
            return (param == null);
        }

        public EndpointReferenceType testNilObjectReturn() {
            return null;
        }

        public boolean testInferredObjectParam(EndpointReferenceType param) {
            boolean ret = false;
            
            QName interfaceName = EndpointReferenceUtils.getInterfaceName(param);
            String wsdlLocation = EndpointReferenceUtils.getWSDLLocation(param);
            QName serviceName = EndpointReferenceUtils.getServiceName(param);
            String portName = EndpointReferenceUtils.getPortName(param);

            // EPR should be complete
            if (param.getAddress().getValue() != null && 
                interfaceName != null &&
                wsdlLocation != null &&
                serviceName != null &&
                portName != null) {
                
                 ret = true;
            }
            
            return ret;
        }

        public EndpointReferenceType testInferredObjectReturn() {
           return createEndpointReferenceType("InferredObjectReturn", true);
        }
    }


    
    // A minimal TestInterface implementation to test object references
    @javax.jws.WebService(portName = "TestObjectCORBAPort",
                          serviceName = "TestObjectCORBAService",
                          targetNamespace = "http://schemas.apache.org/yoko/idl/ObjectRef",
                          endpointInterface = "org.apache.schemas.yoko.idl.objectref.TestObject")
    public class TestObjectImpl implements TestObject {

        private int val;

        public int testObjectValue() {
            return val;
        }

        public void testObjectValueUpdate(int value) {
            val = value;
        }
    }
}

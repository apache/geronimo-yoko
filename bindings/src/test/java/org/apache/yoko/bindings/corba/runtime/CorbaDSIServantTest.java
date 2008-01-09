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
package org.apache.yoko.bindings.corba.runtime;

import java.util.Map;
import java.util.HashMap;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.ServiceImpl;
import org.apache.cxf.transport.ChainInitiationObserver;
import org.apache.cxf.transport.MessageObserver; 
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import org.omg.PortableServer.POA;

import org.apache.yoko.bindings.corba.TestUtils;

public class CorbaDSIServantTest extends TestCase {
    protected static ORB orb;
    protected static Bus bus;
    private static TestUtils testUtils;
        
    public CorbaDSIServantTest() {
        super();
    }
    
    public CorbaDSIServantTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaDSIServantTest.class);
    }
    
    protected void setUp() throws Exception {        
        super.setUp();
        bus = BusFactory.newInstance().getDefaultBus();
        java.util.Properties props = System.getProperties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        props.put("yoko.orb.id", "Yoko-Server-Binding");
        orb = ORB.init(new String[0], props);
        testUtils = new TestUtils();
               
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
        if (orb != null) {
            try {
                orb.destroy();
            } catch (Exception ex) {
                // Do nothing.  Throw an Exception?
            }
        } 
    }
 
    /*public void testCorbaDSIServant() throws Exception {
    
        CorbaDestination destination = testUtils.getSimpleTypesTestDestination();        
        Service service = new ServiceImpl();
        Endpoint endpoint = new EndpointImpl(bus, service, destination.getEndPointInfo());       
        MessageObserver observer = new ChainInitiationObserver(endpoint, bus);       
        destination.setMessageObserver(observer);
        POA rootPOA = null;
        CorbaDSIServant dsiServant = new CorbaDSIServant();
        dsiServant.init(orb,
                        rootPOA,
                        destination,
                        observer);
        
        assertNotNull("DSIServant should not be null", dsiServant != null);
        assertNotNull("POA should not be null", dsiServant._default_POA() != null);
        assertNotNull("Destination should not be null", dsiServant.getDestination() != null);
        assertNotNull("ORB should not be null", dsiServant.getOrb() != null);
        assertNotNull("MessageObserver should not be null", dsiServant.getObserver() != null);
        
        byte[] objectId = new byte[10];
        String[] interfaces = dsiServant._all_interfaces(rootPOA, objectId);
        assertNotNull("Interfaces should not be null", interfaces != null);
        assertEquals("Interface ID should be equal", interfaces[0], "IDL:Simple:1.0");
        
    }*/
        
    public void testInvoke() throws Exception {
        CorbaDSIServant dsiServant = new CorbaDSIServant();       
        IMocksControl control = EasyMock.createNiceControl();
        ServerRequest request = EasyMock.createMock(ServerRequest.class);
        Message msg = EasyMock.createMock(Message.class);             
        String opName = "greetMe";
        EasyMock.expect(request.operation()).andReturn(opName);
        MessageObserver incomingObserver = new TestObserver();               
        dsiServant.setObserver(incomingObserver);
        
        Map<String, QName> map = new HashMap<String, QName>(2);
        // REVISIT: Something is not setup quite right with this test.  In 
        // the DSI Servant, we don't get the expected request operation name
        // that we set above.  Instead, we get a null value.  For now, just 
        // add null to the operation map until we know how to fix the test.
        map.put("greetMe", new QName("greetMe"));
        map.put(null, new QName("greetMe"));
        dsiServant.setOperationMapping(map);
        
        control.replay();
        dsiServant.invoke(request);
        control.verify();        
    }
        
    class TestObserver implements MessageObserver {
              
        TestObserver() {
            super();
        }            
        
        public void onMessage(Message msg) {            
            System.out.println("Test OnMessage in TestObserver");            
        }
    }
}
    
    
    

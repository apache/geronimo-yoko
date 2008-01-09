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

import junit.framework.TestCase;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.ServiceImpl;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.ChainInitiationObserver;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

import org.apache.yoko.bindings.corba.utils.OrbConfig;

public class CorbaDestinationTest extends TestCase {    
    
    protected static TestUtils testUtils;
    EndpointInfo endpointInfo;
    OrbConfig orbConfig;
    
    public CorbaDestinationTest() {
        super();
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaDestinationTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();     
        testUtils = new TestUtils();
        orbConfig = new OrbConfig();
        orbConfig.setOrbClass("org.apache.yoko.orb.CORBA.ORB");
        orbConfig.setOrbSingletonClass("org.apache.yoko.orb.CORBA.ORBSingleton");
    }
    
    public void tearDown() {        
    }
    
    public void testDestination() throws Exception {       
       endpointInfo = testUtils.setupServiceInfo("http://yoko.apache.org/simple",
                        "/wsdl/simpleIdl.wsdl", "SimpleCORBAService",
                        "SimpleCORBAPort");
       CorbaDestination destination = new CorbaDestination(endpointInfo, orbConfig);
       
       EndpointReferenceType  rtype = destination.getAddress();
       assertTrue("EndpointReferenceType should not be null", rtype != null);       
       BindingInfo bindingInfo = destination.getBindingInfo();
       assertTrue("BindingInfo should not be null", bindingInfo != null);       
       EndpointInfo endpointInfo = destination.getEndPointInfo();
       assertTrue("EndpointInfo should not be null", endpointInfo != null);
       
       Message m = new MessageImpl();
       CorbaServerConduit serverConduit = (CorbaServerConduit)destination.getBackChannel(m, m, rtype);
       assertTrue("CorbaServerConduit should not be null", serverConduit != null);             
   }
   
   /*public void testSetMessageObserverActivate() throws Exception {
       endpointInfo = testUtils.setupServiceInfo("http://yoko.apache.org/simple",
                        "/wsdl/simpleIdl.wsdl", "SimpleCORBAService",
                        "SimpleCORBAPort");
       CorbaDestination destination = new CorbaDestination(endpointInfo);
       String addr = destination.getAddressType().getLocation();
       assertEquals(addr, "corbaloc::localhost:40000/Simple");  
       
       Bus bus = BusFactory.newInstance().getDefaultBus();
       Service service = new ServiceImpl();
       Endpoint endpoint = new EndpointImpl(bus, service, endpointInfo);       
       MessageObserver observer = new ChainInitiationObserver(endpoint, bus);       
       destination.setMessageObserver(observer);               
       assertTrue("orb should not be null",  destination.getOrb() != null);
       
       try { 
           File file = new File("endpoint.ior");   
           assertEquals(true,file.exists());
       } finally {
           new File("endpoint.ior").deleteOnExit();
       }
       
       addr = destination.getAddressType().getLocation();
       addr = addr.substring(0,4);
       assertEquals(addr, "IOR:");    
       destination.shutdown();
   }*/     

}

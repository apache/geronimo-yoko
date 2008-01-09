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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;

import junit.framework.TestCase;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.BindingFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.binding.BindingFactoryManagerImpl;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl11.WSDLServiceFactory;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.omg.CORBA.ORB;

import org.apache.yoko.wsdl.CorbaConstants;

public class CorbaBindingTest extends TestCase {
    
    private ORB orb;
    
    public CorbaBindingTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaBindingFactoryTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();                   
        java.util.Properties props = System.getProperties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        props.put("yoko.orb.id", "Yoko-Server-Binding");
        orb = ORB.init(new String[0], props);
    }
    
    public void tearDown() {   
        if (orb != null) {
            try {
                orb.destroy();
            } catch (Exception ex) {
                // Do nothing.  Throw an Exception?
            }
        } 
    }
           
    public void testCorbaBinding() {
        CorbaBinding binding = new CorbaBinding();
        List<Interceptor> in = binding.getInInterceptors();
        assertNotNull(in);
        List<Interceptor> out = binding.getOutInterceptors();
        assertNotNull(out);
        List<Interceptor> infault = binding.getFaultInInterceptors();
        assertNotNull(infault);
        List<Interceptor> outfault = binding.getFaultOutInterceptors();
        assertNotNull(outfault);
        Message message = binding.createMessage();
        message.put(ORB.class, orb);
        assertNotNull(message);
        ORB corbaORB = message.get(ORB.class);
        assertNotNull(corbaORB);        
        MessageImpl mesage = new MessageImpl();
        mesage.put(ORB.class, orb);
        Message msg = binding.createMessage(mesage);        
        assertNotNull(msg);                
        ORB corbaOrb = msg.get(ORB.class);
        assertNotNull(corbaOrb);
        /*List<Interceptor> infault = binding.getInFaultInterceptors();
        assertEquals(1, infault.size());
        List<Interceptor> outfault = binding.getOutFaultInterceptors();
        assertEquals(1, fault.size());*/    
    }        
            
}

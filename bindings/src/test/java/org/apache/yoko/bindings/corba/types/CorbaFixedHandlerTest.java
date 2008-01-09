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
package org.apache.yoko.bindings.corba.types;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.schemas.yoko.bindings.corba.Fixed;
import org.apache.yoko.wsdl.CorbaConstants;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;

public class CorbaFixedHandlerTest extends TestCase {

    private ORB orb;
    
    public CorbaFixedHandlerTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaObjectHandlerTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        
        java.util.Properties props = System.getProperties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        props.put("yoko.orb.id", "Yoko-Server-Binding");
        orb = ORB.init(new String[0], props);
    }
    
    protected void tearDown() throws Exception {
        if (orb != null) {
            try {
                orb.destroy();
            } catch (Exception ex) {
                // Do nothing.  Throw an Exception?
            }
        }
    }
    
    public void testCorbaFixedHandler() {
        Fixed fixedType = new Fixed();
        fixedType.setName("FixedType");
        fixedType.setDigits((long)3);
        fixedType.setScale((long)2);
        
        QName fixedName = new QName(fixedType.getName());
        QName fixedIdlType = 
            new QName(CorbaConstants.NU_WSDL_CORBA, "FixedType", CorbaConstants.NP_WSDL_CORBA);
        TypeCode fixedTC = orb.create_fixed_tc((short)fixedType.getDigits(), (short)fixedType.getScale());
        CorbaFixedHandler obj = new CorbaFixedHandler(fixedName, fixedIdlType, fixedTC, fixedType);
        assertNotNull(obj);
        
        java.math.BigDecimal value = new java.math.BigDecimal(123.45);
        obj.setValue(value);
        
        assertTrue(value.equals(obj.getValue()));
        
        String valueData = obj.getValueData();
        assertTrue(valueData.equals(value.toString()));
        
        assertTrue(fixedType.getDigits() == obj.getDigits());
        assertTrue(fixedType.getScale() == obj.getScale());
    }
}

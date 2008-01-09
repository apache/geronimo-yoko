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

import org.apache.yoko.wsdl.CorbaConstants;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;

public class CorbaObjectHandlerTest extends TestCase {

    private ORB orb;
    
    public CorbaObjectHandlerTest(String arg0) {
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
    
    public void testCreateCorbaObjectHandler() {
        QName objName = new QName("object");
        QName objIdlType = new QName(CorbaConstants.NU_WSDL_CORBA, "long", CorbaConstants.NP_WSDL_CORBA);
        TypeCode objTypeCode = orb.get_primitive_tc(TCKind.tk_long);
        CorbaObjectHandler obj = new CorbaObjectHandler(objName, objIdlType, objTypeCode, null);
        assertNotNull(obj);
    }
    
    public void testGetObjectAttributes() {
        QName objName = new QName("object");
        QName objIdlType = new QName(CorbaConstants.NU_WSDL_CORBA, "long", CorbaConstants.NP_WSDL_CORBA);
        TypeCode objTypeCode = orb.get_primitive_tc(TCKind.tk_long);
        CorbaObjectHandler obj = new CorbaObjectHandler(objName, objIdlType, objTypeCode, null);

        QName name = obj.getName();
        assertNotNull(name);
        assertTrue(name.equals(objName));
        
        QName idlType = obj.getIdlType();
        assertNotNull(idlType);
        assertTrue(idlType.equals(objIdlType));
        
        TypeCode tc = obj.getTypeCode();
        assertNotNull(tc);
        assertTrue(tc.kind().value() == objTypeCode.kind().value());
        
        Object objDef = obj.getType();
        assertNull(objDef);
    }
}

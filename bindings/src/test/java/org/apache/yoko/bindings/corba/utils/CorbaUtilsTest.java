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
package org.apache.yoko.bindings.corba.utils;

import javax.xml.namespace.QName;
import junit.framework.TestCase;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.apache.yoko.wsdl.CorbaConstants;

public class CorbaUtilsTest extends TestCase {

    private static ORB orb;
    
    public CorbaUtilsTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CorbaUtilsTest.class);
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
    
    public void testBooleanTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "boolean", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_boolean);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testCharTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "char", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_char);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testWCharTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "wchar", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_wchar);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testOctetTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "octet", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_octet);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testShortTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "short", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_short);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testUShortTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "ushort", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_ushort);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testLongTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "long", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_long);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testULongTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "ulong", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_ulong);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testLongLongTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "longlong", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_longlong);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testULongLongTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "ulonglong", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_ulonglong);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testFloatTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "float", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_float);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testDoubleTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "double", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_double);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testStringTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "string", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_string);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
    
    public void testWStringTypeCode() {
        QName type = new QName(CorbaConstants.NU_WSDL_CORBA, "wstring", "corba");
        TypeCode tc = CorbaUtils.getPrimitiveTypeCode(orb, type);
        assertNotNull(tc);
        assertTrue(tc.kind().value() == TCKind._tk_wstring);
        assertTrue(CorbaUtils.isPrimitiveIdlType(type));
    }
        
}

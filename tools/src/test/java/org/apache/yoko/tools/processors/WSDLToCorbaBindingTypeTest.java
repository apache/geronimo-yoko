/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

package org.apache.yoko.tools.processors;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.TestCase;

import org.apache.schemas.yoko.bindings.corba.AddressType;
import org.apache.schemas.yoko.bindings.corba.Anonarray;
import org.apache.schemas.yoko.bindings.corba.Anonfixed;
import org.apache.schemas.yoko.bindings.corba.Anonsequence;
import org.apache.schemas.yoko.bindings.corba.Anonstring;
import org.apache.schemas.yoko.bindings.corba.Array;
import org.apache.schemas.yoko.bindings.corba.Struct;
import org.apache.schemas.yoko.bindings.corba.TypeMappingType;
import org.apache.schemas.yoko.bindings.corba.Union;
import org.apache.schemas.yoko.bindings.corba.Unionbranch;
import org.apache.yoko.tools.common.WSDLCorbaFactory;
import org.apache.yoko.tools.processors.wsdl.WSDLToCorbaBinding;
import org.apache.yoko.tools.processors.wsdl.WSDLToIDLAction;
import org.apache.yoko.wsdl.CorbaTypeImpl;

public class WSDLToCorbaBindingTypeTest extends TestCase {
    WSDLToCorbaBinding generator;
    WSDLWriter writer;

    public WSDLToCorbaBindingTypeTest(String name) {
        super(name);
    }

    protected void setUp() {
        generator = new WSDLToCorbaBinding();
        try {
            WSDLCorbaFactory wsdlfactory = WSDLCorbaFactory
                .newInstance("org.apache.yoko.tools.common.WSDLCorbaFactoryImpl");
    
            writer = wsdlfactory.newWSDLWriter();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void tearDown() {
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(WSDLToCorbaBindingTest.class);
    }
    
    private Element getElementNode(Document document, String elName) {
        Element root = document.getDocumentElement();
        for (Node nd = root.getFirstChild(); nd != null; nd = nd.getNextSibling()) {
            if (Node.ELEMENT_NODE == nd.getNodeType() && (elName.equals(nd.getNodeName()))) {
                return (Element)nd;                
            }
        }
        return null;
    }

    public void testWsAddressingAccountType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/wsaddressing_bank.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("Bank");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(1, typemap.getElementsByTagName("corba:sequence").getLength());
            assertEquals(2, typemap.getElementsByTagName("corba:object").getLength()); 

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("BankCORBABinding");
            idlgen.setOutputFile("wsaddressing_bank.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("wsaddressing_bank.idl");
            assertTrue("wsaddressing_bank.idl should be generated", f.exists()); 
        } finally {
            new File("wsaddressing_bank.idl").deleteOnExit();
        }
    }
    
    public void testWsAddressingBankType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/wsaddressing_account.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("Account");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);            
            assertEquals(1, typemap.getElementsByTagName("corba:object").getLength());            

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("AccountCORBABinding");
            idlgen.setOutputFile("wsaddressing_account.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("wsaddressing_account.idl");
            assertTrue("wsaddressing_account.idl should be generated", f.exists()); 
        } finally {
            new File("wsaddressing_account.idl").deleteOnExit();
        }
    }

    
    public void testWsAddressingTypes() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/wsaddressing_server.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("TestServer");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(1, typemap.getElementsByTagName("corba:object").getLength());            

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("TestServerCORBABinding");
            idlgen.setOutputFile("wsaddressing_server.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("wsaddressing_server.idl");
            assertTrue("wsaddressing_server.idl should be generated", f.exists()); 
        } finally {
            new File("wsaddressing_server.idl").deleteOnExit();
        }
    }

    public void testDateTimeTypes() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/datetime.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("BasePortType");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(2, typemap.getElementsByTagName("corba:union").getLength());
            assertEquals(2, typemap.getElementsByTagName("corba:struct").getLength());

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("BaseCORBABinding");
            idlgen.setOutputFile("datetime.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("datetime.idl");
            assertTrue("datetime.idl should be generated", f.exists()); 
        } finally {
            new File("datetime.idl").deleteOnExit();
        }
    }

    public void testNestedInterfaceTypes() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/nested_interfaces.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("C.C1");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(1, typemap.getElementsByTagName("corba:anonstring").getLength());
            assertEquals(9, typemap.getElementsByTagName("corba:struct").getLength());                       

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("C.C1CORBABinding");
            idlgen.setOutputFile("nested_interfaces.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("nested_interfaces.idl");
            assertTrue("nested_interfaces.idl should be generated", f.exists()); 
        } finally {
            new File("nested_interfaces.idl").deleteOnExit();
        }
    }    

    public void testNestedComplexTypes() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/nested_complex.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("X");

            Definition model = generator.generateCORBABinding();


            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(6, typemap.getElementsByTagName("corba:union").getLength());
            assertEquals(14, typemap.getElementsByTagName("corba:struct").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:enum").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:array").getLength());

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("XCORBABinding");
            idlgen.setOutputFile("nested_complex.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("nested_complex.idl");
            assertTrue("nested_complex.idl should be generated", f.exists()); 
        } finally {
            new File("nested_complex.idl").deleteOnExit();
        }
    }    

    
    public void testNestedDerivedTypes() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/nested-derivedtypes.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("DerivedTypesPortType");

            Definition model = generator.generateCORBABinding();

            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(6, typemap.getElementsByTagName("corba:union").getLength());
            assertEquals(58, typemap.getElementsByTagName("corba:struct").getLength());
            assertEquals(3, typemap.getElementsByTagName("corba:sequence").getLength());

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("DerivedTypesCORBABinding");
            idlgen.setOutputFile("nested-derivedtypes.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("nested-derivedtypes.idl");
            assertTrue("nested-derivedtypes.idl should be generated", f.exists()); 
        } finally {
            new File("nested-derivedtypes.idl").deleteOnExit();
        }
    }    

    public void testNestedType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/nested.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("TypeInheritancePortType");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(4, typemap.getElementsByTagName("corba:union").getLength());
            assertEquals(23, typemap.getElementsByTagName("corba:struct").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:anonstring").getLength());
            
            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("TypeInheritanceCORBABinding");
            idlgen.setOutputFile("nested.idl");
            idlgen.generateIDL(model);
                        
            File f = new File("nested.idl");
            assertTrue("nested.idl should be generated", f.exists()); 
        } finally {
            new File("nested.idl").deleteOnExit();
        }
    }    

    
    public void testNillableType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/nillable.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("NillablePortType");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(2, typemap.getElementsByTagName("corba:union").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:struct").getLength());            
            
            TypeMappingType mapType = (TypeMappingType)model.getExtensibilityElements().get(0);            

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("NillableCORBABinding");
            idlgen.setOutputFile("nillable.idl");
            idlgen.generateIDL(model);
            
            Union un = (Union)mapType.getStructOrExceptionOrUnion().get(2);
            assertEquals("Name is incorrect for Union Type", "long_nil", 
                         un.getName());
            assertEquals("Type is incorrect for Union Type", "PEl", 
                         un.getType().getLocalPart());
            Unionbranch unbranch = un.getUnionbranch().get(0);
            assertEquals("Name is incorrect for UnionBranch Type", "value", 
                         unbranch.getName());
            assertEquals("Type is incorrect for UnionBranch Type", "long", 
                         unbranch.getIdltype().getLocalPart());

            File f = new File("nillable.idl");
            assertTrue("nillable.idl should be generated", f.exists()); 
        } finally {
            new File("nillable.idl").deleteOnExit();
        }
    }    

    
    
    // tests Type Inheritance and attributes.
    public void testTypeInheritance() throws Exception {
        try {
            String fileName = getClass().getResource("/wsdl/TypeInheritance.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("TypeInheritancePortType");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(3, typemap.getElementsByTagName("corba:union").getLength());            
            assertEquals(1, typemap.getElementsByTagName("corba:anonstring").getLength());            
            assertEquals(17, typemap.getElementsByTagName("corba:struct").getLength());            
            
            TypeMappingType mapType = (TypeMappingType)model.getExtensibilityElements().get(0);            

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("TypeInheritanceCORBABinding");
            idlgen.setOutputFile("typeInherit.idl");
            idlgen.generateIDL(model);

            List<CorbaTypeImpl> types = mapType.getStructOrExceptionOrUnion();
            for (int i = 0; i < types.size(); i++) {
                CorbaTypeImpl type = types.get(i);
                if ("Type5SequenceStruct".equals(type.getName())) {
                    assertTrue("Name is incorrect for Type5SequenceStruct Type", type instanceof Struct);
                    assertEquals("Type is incorrect for AnonSequence Type", "Type5", 
                                 type.getType().getLocalPart());
                } else if ("attrib2Type".equals(type.getName())) {
                    assertTrue("Name is incorrect for attrib2Type Type", type instanceof Anonstring);
                    assertEquals("Type is incorrect for AnonString Type", "string", 
                                 type.getType().getLocalPart());
                }  else if ("attrib2Type_nil".equals(type.getName())) {
                    assertTrue("Name is incorrect for Struct Type", type instanceof Union);
                    assertEquals("Type is incorrect for AnonSequence Type", "attrib2", 
                                 type.getType().getLocalPart());
                }
            }
            File f = new File("typeInherit.idl");
            assertTrue("typeInherit.idl should be generated", f.exists()); 
        } finally {
            new File("typeInherit.idl").deleteOnExit();
        }
    }    
    
    // tests anonymous strings and fixed types.
    public void testAnonFixedType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/anonfixed.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("X");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);

            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(1, typemap.getElementsByTagName("corba:anonfixed").getLength());            
            assertEquals(1, typemap.getElementsByTagName("corba:anonstring").getLength());            
            assertEquals(3, typemap.getElementsByTagName("corba:struct").getLength());            
            
            TypeMappingType mapType = (TypeMappingType)model.getExtensibilityElements().get(0);            

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("XCORBABinding");
            idlgen.setOutputFile("atype.idl");
            idlgen.generateIDL(model);

            Anonstring str = (Anonstring)mapType.getStructOrExceptionOrUnion().get(2);
            assertEquals("Name is incorrect for Array Type", "X._1_S", 
                         str.getName());
            assertEquals("Type is incorrect for AnonString Type", "string", 
                         str.getType().getLocalPart());
            
            Anonfixed fx = (Anonfixed)mapType.getStructOrExceptionOrUnion().get(3);
            assertEquals("Name is incorrect for Anon Array Type", "X._2_S", 
                         fx.getName());
            assertEquals("Type is incorrect for AnonFixed Type", "decimal", 
                         fx.getType().getLocalPart());
            
            Struct struct = (Struct)mapType.getStructOrExceptionOrUnion().get(1);
            assertEquals("Name is incorrect for Anon Array Type", "X.S", 
                         struct.getName());            
            assertEquals("Type is incorrect for Struct Type", "X.S", 
                         struct.getType().getLocalPart());
            assertEquals("Name for first Struct Member Type is incorrect", "str", 
                         struct.getMember().get(0).getName());
            assertEquals("Idltype for first Struct Member Type is incorrect", "X._1_S", 
                         struct.getMember().get(0).getIdltype().getLocalPart());            
            assertEquals("Name for second Struct Member Type is incorrect", "fx", 
                         struct.getMember().get(1).getName());
            assertEquals("Idltype for second Struct Member Type is incorrect", "X._2_S", 
                         struct.getMember().get(1).getIdltype().getLocalPart());
            
            File f = new File("atype.idl");
            assertTrue("atype.idl should be generated", f.exists());
        } finally {
            new File("atype.idl").deleteOnExit();
        }
    }
    
    // tests anonymous arrays and sequences
    public void testAnonType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/atype.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("X");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);
            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);
            assertEquals(3, typemap.getElementsByTagName("corba:anonsequence").getLength());         
            assertEquals(2, typemap.getElementsByTagName("corba:anonarray").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:array").getLength());
            assertEquals(2, typemap.getElementsByTagName("corba:struct").getLength());            

            TypeMappingType mapType = (TypeMappingType)model.getExtensibilityElements().get(0);

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("XCORBABinding");
            idlgen.setOutputFile("atype.idl");
            idlgen.generateIDL(model);

            Array arr = (Array)mapType.getStructOrExceptionOrUnion().get(0);            
            assertEquals("ElementType is incorrect for Array Type", "X._5_A", 
                         arr.getElemtype().getLocalPart());
            
            Anonarray arr2 = (Anonarray)mapType.getStructOrExceptionOrUnion().get(3);
            assertEquals("ElementType is incorrect for Anon Array Type", "X._4_A", 
                         arr2.getElemtype().getLocalPart());
            
            Anonarray arr3 = (Anonarray)mapType.getStructOrExceptionOrUnion().get(2);
            assertEquals("ElementType is incorrect for Anon Array Type", "X._1_A", 
                         arr3.getElemtype().getLocalPart());
            
            
            Anonsequence seq = (Anonsequence)mapType.getStructOrExceptionOrUnion().get(5);
            assertEquals("Name is incorrect for Anon Array Type", "X._1_A", 
                         seq.getName());                        
            assertEquals("ElementType is incorrect for Anon Sequence Type", "X._2_A", 
                         seq.getElemtype().getLocalPart());
            
            
            Anonsequence seq2 = (Anonsequence)mapType.getStructOrExceptionOrUnion().get(1);
            assertEquals("Name is incorrect for Anon Array Type", "X._2_A", 
                         seq2.getName());                        
            assertEquals("ElementType is incorrect for Anon Sequence Type", "X._3_A", 
                         seq2.getElemtype().getLocalPart());
            
            Anonsequence seq3 = (Anonsequence)mapType.getStructOrExceptionOrUnion().get(4);
            assertEquals("Name is incorrect for Anon Array Type", "X._3_A", 
                         seq3.getName());
            assertEquals("ElementType is incorrect for Anon Sequence Type", "long", 
                         seq3.getElemtype().getLocalPart());
            
            File f = new File("atype.idl");
            assertTrue("atype.idl should be generated", f.exists());
        } finally {
            new File("atype.idl").deleteOnExit();
        }
    }
    
    public void testAnyType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/any.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("anyInterface");            
            Definition model = generator.generateCORBABinding();

            TypeMappingType mapType = (TypeMappingType)model.getExtensibilityElements().get(0);
            assertEquals(5, mapType.getStructOrExceptionOrUnion().size());
            Iterator i = mapType.getStructOrExceptionOrUnion().iterator();
            int strcnt = 0;
            int unioncnt = 0;
            while (i.hasNext()) {
                CorbaTypeImpl corbaType = (CorbaTypeImpl)i.next();
                if (corbaType instanceof Struct) {
                    strcnt++;
                }
                if (corbaType instanceof Union) {
                    unioncnt++;
                }
            }
                        
            assertNotNull(mapType);
            assertEquals(3, strcnt);
            assertEquals(2, unioncnt);                                   

            WSDLToIDLAction idlgen = new WSDLToIDLAction();
            idlgen.setBindingName("anyInterfaceCORBABinding");
            idlgen.setOutputFile("any.idl");
            idlgen.generateIDL(model);

            File f = new File("any.idl");
            assertTrue("any.idl should be generated", f.exists());
        } finally {
            new File("any.idl").deleteOnExit();
        }
    }
    
    public void testMultipleBindings() throws Exception {
        String fileName = getClass().getResource("/wsdl/multiplePortTypes.wsdl").toString();
        generator.setWsdlFile(fileName);
        generator.setAllBindings(true);
        Definition model = generator.generateCORBABinding();
        assertEquals("All bindings should be generated.", 2, model.getAllBindings().size());
    }

    public void testAnonymousReturnParam() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/factory_pattern.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("Number");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);
            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);            
            assertEquals(3, typemap.getElementsByTagName("corba:struct").getLength());            
        } finally {
            new File("factory_pattern-corba.wsdl").deleteOnExit();
        }
    }
    
    public void testComplextypeDerivedSimpletype() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/complex_types.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("TypeTestPortType");

            Definition model = generator.generateCORBABinding();
            
            Document document = writer.getDocument(model);
            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);            
            assertEquals(8, typemap.getElementsByTagName("corba:struct").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:fixed").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:array").getLength());
            assertEquals(5, typemap.getElementsByTagName("corba:union").getLength());
            assertEquals(3, typemap.getElementsByTagName("corba:sequence").getLength());
            
        } finally {
            new File("complex_types-corba.wsdl").deleteOnExit();
        }
    }
    
    public void testCorbaExceptionComplextype() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/databaseService.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("Database");

            Definition model = generator.generateCORBABinding();
            Document document = writer.getDocument(model);
            Element typemap = getElementNode(document, "corba:typeMapping");            
            assertNotNull(typemap);            
            assertEquals(2, typemap.getElementsByTagName("corba:struct").getLength());
            assertEquals(1, typemap.getElementsByTagName("corba:exception").getLength());           
            assertEquals(1, typemap.getElementsByTagName("corba:anonsequence").getLength());
        } finally {
            new File("databaseService-corba.wsdl").deleteOnExit();
        }
    }    
    
    public void testSetCorbaAddress() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/datetime.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("BasePortType");

            Definition model = generator.generateCORBABinding();                
            QName name = new QName("http://schemas.apache.org/idl/datetime.idl",
                                     "BaseCORBAService", "tns");
            Service service = model.getService(name);
            Port port = service.getPort("BaseCORBAPort");
            AddressType addressType = (AddressType)port.getExtensibilityElements().get(0);
            String address = addressType.getLocation();
            assertEquals("file:./Base.ref", address);            
            
            generator.setAddress("corbaloc::localhost:40000/hw");
            model = generator.generateCORBABinding();
            service = model.getService(name);
            port = service.getPort("BaseCORBAPort");
            addressType = (AddressType)port.getExtensibilityElements().get(0);
            address = addressType.getLocation();
            assertEquals("corbaloc::localhost:40000/hw", address);
        } finally {
            new File("datetime-corba.wsdl").deleteOnExit();
        }
    }    
    
    public void testSetCorbaAddressFile() throws Exception {
        
        try {
            String fileName = getClass().getResource("/wsdl/datetime.wsdl").toString();
            generator.setWsdlFile(fileName);
            generator.addInterfaceName("BasePortType");

            Definition model = generator.generateCORBABinding();                
            QName name = new QName("http://schemas.apache.org/idl/datetime.idl",
                                     "BaseCORBAService", "tns");
            Service service = model.getService(name);
            Port port = service.getPort("BaseCORBAPort");
            AddressType addressType = (AddressType)port.getExtensibilityElements().get(0);
            String address = addressType.getLocation();
            assertEquals("file:./Base.ref", address);            
            
            URL idl = getClass().getResource("/wsdl/addressfile.txt");
            String filename = idl.getFile();
            generator.setAddressFile(filename);
            model = generator.generateCORBABinding();
            service = model.getService(name);
            port = service.getPort("BaseCORBAPort");
            addressType = (AddressType)port.getExtensibilityElements().get(0);
            address = addressType.getLocation();
            assertEquals("corbaloc::localhost:60000/hw", address);
        } finally {
            new File("datetime-corba.wsdl").deleteOnExit();
        }
    }    
    
        
}

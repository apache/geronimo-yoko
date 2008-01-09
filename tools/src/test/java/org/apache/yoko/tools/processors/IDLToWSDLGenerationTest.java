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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.apache.yoko.tools.common.ProcessorEnvironment;
import org.apache.yoko.tools.common.ToolCorbaConstants;
import org.apache.yoko.tools.processors.idl.IDLToWSDLProcessor;
import org.apache.yoko.tools.utils.WSDLGenerationTester;

public class IDLToWSDLGenerationTest extends TestCase {

    private XMLInputFactory factory;
    private WSDLGenerationTester wsdlGenTester;

    public IDLToWSDLGenerationTest(String name) {
        super(name);
        wsdlGenTester = new WSDLGenerationTester();
    }
    
    protected void setUp() {
        factory = XMLInputFactory.newInstance();
    }

    protected void tearDown() {
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(IDLToWSDLGenerationTest.class);
    }
    
    public void testWSDLGeneration(String sourceIdlFilename, 
                                   String expectedWsdlFilename) 
        throws Exception {
        URL idl = getClass().getResource(sourceIdlFilename);
        ProcessorEnvironment env = new ProcessorEnvironment();
        Map<String, Object> cfg = new HashMap<String, Object>();
        cfg.put(ToolCorbaConstants.CFG_IDLFILE, idl.getFile());
        env.setParameters(cfg);
        IDLToWSDLProcessor processor = new IDLToWSDLProcessor();
        processor.setEnvironment(env);        
        java.io.CharArrayWriter out = new java.io.CharArrayWriter();
        processor.setOutputWriter(out);        
        processor.process();

        InputStream origStream = getClass().getResourceAsStream(expectedWsdlFilename);  
        InputStream actualStream = new ByteArrayInputStream(out.toString().getBytes());

        XMLStreamReader orig = factory.createXMLStreamReader(origStream);
        XMLStreamReader actual = factory.createXMLStreamReader(actualStream);

        wsdlGenTester.compare(orig, actual);

    }
    
    public void testHelloWorldWSDLGeneration() throws Exception {
        testWSDLGeneration("/idl/HelloWorld.idl", "/idl/expected_HelloWorld.wsdl");
    }
    
    public void testPrimitivesGeneration() throws Exception {
        testWSDLGeneration("/idl/primitives.idl", "/idl/expected_Primitives.wsdl");
    }

    public void testExceptionGeneration() throws Exception {
        testWSDLGeneration("/idl/Exception.idl", "/idl/expected_Exception.wsdl");
    }

    public void testStructGeneration() throws Exception {
        testWSDLGeneration("/idl/Struct.idl", "/idl/expected_Struct.wsdl");
    }
    
    public void testScopedStructGeneration() throws Exception {
        testWSDLGeneration("/idl/scopedStruct.idl", "/idl/expected_scopedStruct.wsdl");
    }

    public void testOnewayGeneration() throws Exception {
        testWSDLGeneration("/idl/Oneway.idl", "/idl/expected_Oneway.wsdl");
    }

    public void testConstGeneration() throws Exception {
        testWSDLGeneration("/idl/Const.idl", "/idl/expected_Const.wsdl");
    }

    public void testEnumGeneration() throws Exception {
        testWSDLGeneration("/idl/Enum.idl", "/idl/expected_Enum.wsdl");
    }

    public void testUnionGeneration() throws Exception {
        testWSDLGeneration("/idl/Union.idl", "/idl/expected_Union.wsdl");
    }

    public void testFixedGeneration() throws Exception {
        testWSDLGeneration("/idl/Fixed.idl", "/idl/expected_Fixed.wsdl");
    }

    public void testTypedefGeneration() throws Exception {
        testWSDLGeneration("/idl/Typedef.idl", "/idl/expected_Typedef.wsdl");
    }

    public void testStringGeneration() throws Exception {
        testWSDLGeneration("/idl/String.idl", "/idl/expected_String.wsdl");
    }

    public void testAttributesGeneration() throws Exception {
        testWSDLGeneration("/idl/Attributes.idl", "/idl/expected_Attributes.wsdl");
    }

    public void testSequenceGeneration() throws Exception {
        testWSDLGeneration("/idl/Sequence.idl", "/idl/expected_Sequence.wsdl");
    }

    public void testArrayGeneration() throws Exception {
        testWSDLGeneration("/idl/Array.idl", "/idl/expected_Array.wsdl");
    }

    public void testAnonarrayGeneration() throws Exception {
        testWSDLGeneration("/idl/Anonarray.idl", "/idl/expected_Anonarray.wsdl");
    }

    public void testAnonsequenceGeneration() throws Exception {
        testWSDLGeneration("/idl/Anonsequence.idl", "/idl/expected_Anonsequence.wsdl");
    }

    public void testAnonboundedsequenceGeneration() throws Exception {
        testWSDLGeneration("/idl/Anonboundedsequence.idl", "/idl/expected_Anonboundedsequence.wsdl");
    }

    public void testAnonstringGeneration() throws Exception {
        testWSDLGeneration("/idl/Anonstring.idl", "/idl/expected_Anonstring.wsdl");
    }

    public void testMultipleDeclaratorsGeneration() throws Exception {
        testWSDLGeneration("/idl/Declarators.idl", "/idl/expected_Declarators.wsdl");
    }   
        
    public void testObjectReferenceGeneration() throws Exception {
        testWSDLGeneration("/idl/ObjectRef.idl", "/idl/expected_ObjectRef.wsdl");
    }
    
    
    public void testScopingOperationGeneration() throws Exception {
        testWSDLGeneration("/idl/scopingOperation.idl", "/idl/expected_scopingOperation.wsdl");
    }
    
    public void testScopingObjectRefGlobalGeneration() throws Exception {
        testWSDLGeneration("/idl/scopingObjectRefGlobal.idl", "/idl/expected_scopingObjectRefGlobal.wsdl");
    }
        
    public void testScopingObjectRefGeneration() throws Exception {
        testWSDLGeneration("/idl/scopingObjectRef.idl", "/idl/expected_scopingObjectRef.wsdl");
    }
    
    public void testScopingStringGeneration() throws Exception {
        testWSDLGeneration("/idl/scopedString.idl", "/idl/expected_scopedString.wsdl");
    }
       
    public void testForwardInterface() throws Exception {
        testWSDLGeneration("/idl/ForwardInterface.idl", "/idl/expected_ForwardInterface.wsdl");
    }
    
    public void testForwardInterfaceParam() throws Exception {
        testWSDLGeneration("/idl/ForwardInterfaceParam.idl", "/idl/expected_ForwardInterfaceParam.wsdl");
    }
    
    public void testForwardInterfaceStructUnion() throws Exception {
        testWSDLGeneration("/idl/ForwardInterfaceStructUnion.idl", 
                           "/idl/expected_ForwardInterfaceStructUnion.wsdl");
    }

    public void testForwardInterfaceSequence() throws Exception {
        testWSDLGeneration("/idl/ForwardInterfaceSequence.idl", 
                           "/idl/expected_ForwardInterfaceSequence.wsdl");
    }
    
    public void testForwardInterfaceArray() throws Exception {
        testWSDLGeneration("/idl/ForwardInterfaceArray.idl", "/idl/expected_ForwardInterfaceArray.wsdl");
    }
    
    public void testForwardInterfaceAttributes() throws Exception {
        testWSDLGeneration("/idl/ForwardInterfaceAttributes.idl", 
                           "/idl/expected_ForwardInterfaceAttributes.wsdl");
    }
    
    public void testForwardInterfaceExceptions() throws Exception {
        testWSDLGeneration("/idl/ForwardInterfaceException.idl", 
                           "/idl/expected_ForwardInterfaceException.wsdl");
    }
    
    public void testForwardInterfaceTypedef() throws Exception {
        testWSDLGeneration("/idl/ForwardInterfaceTypedef.idl", 
                           "/idl/expected_ForwardInterfaceTypedef.wsdl");
    }
    
    public void testForwardStruct() throws Exception {
        testWSDLGeneration("/idl/ForwardStruct.idl", 
                           "/idl/expected_ForwardStruct.wsdl");
    }
    
    public void testForwardUnion() throws Exception {
        testWSDLGeneration("/idl/ForwardUnion.idl", 
                           "/idl/expected_ForwardUnion.wsdl");
    }
    
    public void testIncludeGeneration() throws Exception {
        testWSDLGeneration("/idl/included.idl", "/idl/expected_Included.wsdl");
    }

    public void testInterfaceInheritance() throws Exception {
        testWSDLGeneration("/idl/inheritance.idl", "/idl/expected_Inheritance.wsdl");
    }

    public void testDuplicateOperationNames() throws Exception {
        // This tests operations with the same name but in different scopes
        testWSDLGeneration("/idl/duplicateOpNames.idl", "/idl/expected_duplicateOpNames.wsdl");
    }
    
    public void testConstScopedNames() throws Exception {
        // This tests consts where their types are scoped names
        testWSDLGeneration("/idl/ConstScopename.idl", "/idl/expected_ConstScopename.wsdl");
    }
    
    public void testTypedfOctet() throws Exception {
        // This tests typedef sequence of octets.
        testWSDLGeneration("/idl/Octet.idl", "/idl/expected_Octet.wsdl");
    }
    
    public void testRecursiveStructs() throws Exception {
        // This tests for recursive structs
        testWSDLGeneration("/idl/RecursiveStruct.idl", "/idl/expected_RecursiveStruct.wsdl");
    }

    public void testRecursiveUnions() throws Exception {
        // This tests for recursive unions
        testWSDLGeneration("/idl/RecursiveUnion.idl", "/idl/expected_RecursiveUnion.wsdl");
    }

    public void testLogicalPhysicalSchemaGeneration(String idlFilename, 
                                             String logicalName,
                                             String physicalName, 
                                             String schemaFilename,
                                             String defaultFilename,
                                             String importName,
                                             String defaultImportName) throws Exception {

        URL idl = getClass().getResource(idlFilename);
        ProcessorEnvironment env = new ProcessorEnvironment();
        Map<String, Object> cfg = new HashMap<String, Object>();
        cfg.put(ToolCorbaConstants.CFG_IDLFILE, idl.getFile());
        if (logicalName != null) {
            cfg.put(ToolCorbaConstants.CFG_LOGICAL, logicalName);
        }
        if (physicalName != null) {
            cfg.put(ToolCorbaConstants.CFG_PHYSICAL, physicalName);
        }
        if (schemaFilename != null) {
            cfg.put(ToolCorbaConstants.CFG_SCHEMA, schemaFilename);
        }

        env.setParameters(cfg);
        IDLToWSDLProcessor processor = new IDLToWSDLProcessor();
        processor.setEnvironment(env);    
        java.io.CharArrayWriter outD = new java.io.CharArrayWriter();
        processor.setOutputWriter(outD);
        java.io.CharArrayWriter outL = new java.io.CharArrayWriter();
        java.io.CharArrayWriter outP = new java.io.CharArrayWriter();
        java.io.CharArrayWriter outS = new java.io.CharArrayWriter();
        if (logicalName != null) {            
            processor.setLogicalOutputWriter(outL);
        }
        if (physicalName != null) {            
            processor.setPhysicalOutputWriter(outP);
        }
        if (schemaFilename != null) {            
            processor.setSchemaOutputWriter(outS);
        }
        processor.process();        
        
        String userdir = System.getProperty("user.dir");
        String sep = System.getProperty("file.separator");                    
        File file = file = new File(userdir + sep + importName);            
        String location = file.toURI().toString();
        File schemaFile = new File(userdir + sep + schemaFilename);                       
        String schemaLocation = schemaFile.toURI().toString();
        File defaultFile = new File(userdir + sep + defaultImportName);                       
        String defaultLocation = defaultFile.toURI().toString();
         
        
        if (logicalName != null) {                
            testCompare(logicalName, outL, schemaLocation);            
        }
        
        if (physicalName != null) {
            testCompare(physicalName, outP, location);            
        }
                
        if (schemaFilename != null) {                        
            InputStream origSchemaStream = getClass().getResourceAsStream("/idl/" + schemaFilename);
            InputStream actualSchemaStream = new ByteArrayInputStream(outS.toString().getBytes());
            XMLStreamReader orig = factory.createXMLStreamReader(origSchemaStream);
            XMLStreamReader actual = factory.createXMLStreamReader(actualSchemaStream);
            wsdlGenTester.compare(orig, actual);
        }
        
        if (defaultFilename != null) {                
            testCompare(defaultFilename, outD, defaultLocation);            
        }
        
    }         
    
    public boolean testCompare(String filename, java.io.CharArrayWriter outWriter, String location)
        throws Exception {
        try {
            InputStream origExpectedStream = getClass().getResourceAsStream("/idl/" + filename);
            ByteArrayInputStream expectedByteStream = get(origExpectedStream, location);
            InputStream actualPhysicalStream = new ByteArrayInputStream(outWriter.toString().getBytes());
            ByteArrayInputStream  actualByteStream = get(actualPhysicalStream, location);
            XMLStreamReader orig = factory.createXMLStreamReader(expectedByteStream);
            XMLStreamReader actual = factory.createXMLStreamReader(actualByteStream);
            wsdlGenTester.compare(orig, actual);
        } catch (Exception ex) {
            throw ex;
        }
        return true;
    }
    
    public ByteArrayInputStream get(InputStream stream, String location) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bos));
            String line = br.readLine();
            while (null != line) {
                // replace line if necessary
                String modifiedLine = line;
                if (location != null) {
                    modifiedLine = line.replace("@LOCATION@", location);
                }
                bw.write(modifiedLine);
                line = br.readLine();
            }
            bw.close();
            return new ByteArrayInputStream(bos.toByteArray()); 
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    
    public void testSchemaOnly() throws Exception {
        // This tests if -T option is only passed.
        testLogicalPhysicalSchemaGeneration("/idl/OptionsSchema.idl", 
                                            null, null,
                                            "expected_Schema.xsd",
                                            "expected_OptionsSchema.wsdl",
                                            "expected_Schema.xsd",
                                            "expected_Schema.xsd");
    }
    
    
    // default files generated in user dir - no full path specified.
    // This tests if -P and -T options are passed.
    public void testPhysicalSchema() throws Exception { 
        testLogicalPhysicalSchemaGeneration("/idl/OptionsPT.idl", null,
                                        "expected_PhysicalPT.wsdl", 
                                        "expected_SchemaPT.xsd",
                                        "expected_OptionsPT.wsdl",
                                        "OptionsPT.wsdl",
                                        "expected_SchemaPT.xsd");                                     
    }
            
    public void testLogicalSchema() throws Exception {
        // This tests -L and -T options are passed.
        testLogicalPhysicalSchemaGeneration("/idl/OptionsLT.idl", 
                                            "expected_LogicalLT.wsdl",
                                            null, "expected_SchemaLT.xsd",
                                            "expected_OptionsLT.wsdl",
                                            "OptionsLT.wsdl",
                                            "expected_LogicalLT.wsdl");       
    }


    public void testLogicalOnly() throws Exception {
        // This tests if only -L option is passed.
        testLogicalPhysicalSchemaGeneration("/idl/OptionL.idl", 
                                            "expected_Logical.wsdl",
                                            null, null,
                                            "expected_OptionL.wsdl",
                                            "expected_Logical.wsdl",
                                            "expected_Logical.wsdl");
    }
    
    public void testLogicalPhysical() throws Exception {
        // This tests if -L and -P options are passed.
        testLogicalPhysicalSchemaGeneration("/idl/OptionsLP.idl", "expected_LogicalLP.wsdl",
                                            "expected_PhysicalLP.wsdl", null,
                                            null,
                                            "expected_LogicalLP.wsdl",
                                            null);
    }
    
    public void testPhysicalOnly() throws Exception {
        // This tests if -P option is only passed.
        testLogicalPhysicalSchemaGeneration("/idl/OptionP.idl", null,
                                            "expected_Physical.wsdl", 
                                            null,
                                            "expected_OptionP.wsdl",
                                            "OptionP.wsdl",
                                            "null");
    }            
    
    public void testLogicalPyhsicalSchema() throws Exception {
        // This tests if -L, -P and -T options are passed. 
        testLogicalPhysicalSchemaGeneration("/idl/OptionsLPT.idl", 
                                            "expected_LogicalLPT.wsdl",
                                            "expected_PhysicalLPT.wsdl", 
                                            "expected_SchemaLPT.xsd",
                                            null,
                                            "expected_LogicalLPT.wsdl",
                                            null);
                                            
        
    }
                
    public void testEncodingGeneration() throws Exception {     
        
        try {
            String sourceIdlFilename = "/idl/Enum.idl";                 
            URL idl = getClass().getResource(sourceIdlFilename);
            ProcessorEnvironment env = new ProcessorEnvironment();
            Map<String, Object> cfg = new HashMap<String, Object>();
            cfg.put(ToolCorbaConstants.CFG_IDLFILE, idl.getFile());
            cfg.put(ToolCorbaConstants.CFG_WSDL_ENCODING, "UTF-16");
            env.setParameters(cfg);
            IDLToWSDLProcessor processor = new IDLToWSDLProcessor();
            processor.setEnvironment(env);
            Writer out = processor.getOutputWriter("Enum.wsdl", ".");                   
                                   
            if (out instanceof OutputStreamWriter) {
                OutputStreamWriter writer = (OutputStreamWriter)out;
                assertEquals("Encoding should be UTF-16", writer.getEncoding(), 
                             "UTF-16");                 
            }
            out.close();
        } finally {
            new File("Enum.wsdl").deleteOnExit();                       
        }

    }
                
}

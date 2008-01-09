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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.apache.yoko.tools.processors.wsdl.WSDLToIDLAction;

public class WSDLToIDLGenerationTest extends TestCase {

    protected static final String START_COMMENT = "/*";
    protected static final String END_COMMENT = "*/";
    WSDLToIDLAction idlgen;
    ByteArrayOutputStream idloutput;

    public WSDLToIDLGenerationTest(String name) {
        super(name);
    }
    
    protected void setUp() {
        System.setProperty("WSDLTOIDLGeneration", "false");
        idlgen = new WSDLToIDLAction();
        idloutput = new ByteArrayOutputStream();
    }

    protected void tearDown() {
        System.setProperty("WSDLTOIDLGeneration", "true");
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(WSDLToCorbaBindingTest.class);
    }
    
    private void checkIDLStrings(byte orig[], byte generated[]) throws Exception {
        BufferedReader origReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(orig)));
        BufferedReader genReader = new BufferedReader(
                                       new InputStreamReader(new ByteArrayInputStream(generated)));

        String sorig = origReader.readLine();
        String sgen = genReader.readLine();

        boolean origComment = false;
        boolean genComment = false;
        while (sorig != null && sgen != null) {
            if (sorig.trim().startsWith(START_COMMENT)) {
                origComment = true;
            }
            if (sgen.trim().startsWith(START_COMMENT)) {
                genComment = true;
            }
            if ((!origComment) && (!genComment)) {
                assertEquals(sorig, sgen);
                sgen = genReader.readLine();
                sorig = origReader.readLine();
            }
            if (sorig != null && sgen != null) {
                if (sorig.trim().endsWith(END_COMMENT)) {
                    origComment = false;
                    sorig = origReader.readLine();
                }
                if (sgen.trim().endsWith(END_COMMENT)) {
                    genComment = false;
                    sgen = genReader.readLine();
                }
                if (genComment) {
                    sgen = genReader.readLine();
                }
                if (origComment) {
                    sorig = origReader.readLine();
                }
            }
        }
    }

    public byte[] inputStreamToBytes(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
        return out.toByteArray();
    } 

    public void testOnewayGeneration() throws Exception {
        
        String fileName = getClass().getResource("/idlgen/oneway.wsdl").toString();
        idlgen.setWsdlFile(fileName);
                
        idlgen.setBindingName("BaseCORBABinding");
        idlgen.setOutputFile("oneway.idl");
        idlgen.setPrintWriter(new PrintWriter(idloutput));
        idlgen.generateIDL(null);

        InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_oneway.idl");
        byte orig[] = inputStreamToBytes(origstream);

        checkIDLStrings(orig, idloutput.toByteArray());
    }
    
    public void testStringtypesIdlgen() throws Exception {
        try {
            String fileName = getClass().getResource("/idlgen/stringtypes.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("BasePortTypeCORBABinding");
            idlgen.setOutputFile("stringtypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_stringtypes.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("stringtypes.idl").deleteOnExit();
        }
    }

    public void testIntegertypesIdlgen() throws Exception {
        try {
            String fileName = getClass().getResource("/idlgen/integertypes.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("BasePortTypeCORBABinding");
            idlgen.setOutputFile("integertypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_integertypes.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("integertypes.idl").deleteOnExit();
        }
    }
    
    public void testUniontypesIdlgen() throws Exception {
        try {
            String fileName = getClass().getResource("/idlgen/uniontypes.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("Test.MultiPartCORBABinding");
            idlgen.setOutputFile("uniontypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_uniontypes.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("uniontypes.idl").deleteOnExit();
        }
    }
    
    public void testDefaultUniontypesIdlgen() throws Exception {
        try {
            String fileName = getClass().getResource("/idlgen/defaultuniontypes.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("Test.MultiPartCORBABinding");
            idlgen.setOutputFile("defaultuniontypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_defaultuniontypes.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("defaultuniontypes.idl").deleteOnExit();
        }
    }


    public void testExceptionIdlgen() throws Exception {

        try {
            String fileName = getClass().getResource("/idlgen/exceptions.wsdl").toString();
            idlgen.setWsdlFile(fileName);

            idlgen.setBindingName("TestException.ExceptionTestCORBABinding");
            idlgen.setOutputFile("exceptiontypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_exceptions.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("exceptiontypes.idl").deleteOnExit();
        }
    }
    
    public void testStructIdlgen() throws Exception {

        try {
            String fileName = getClass().getResource("/idlgen/struct.wsdl").toString();
            idlgen.setWsdlFile(fileName);

            idlgen.setBindingName("StructTestCORBABinding");
            idlgen.setOutputFile("structtypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_struct.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("structtypes.idl").deleteOnExit();
        }
    }


    public void testSequenceIdlgen() throws Exception {

        try {
            String fileName = getClass().getResource("/idlgen/sequencetype.wsdl").toString();
            idlgen.setWsdlFile(fileName);

            idlgen.setBindingName("IACC.ServerCORBABinding");
            idlgen.setOutputFile("sequencetypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_sequencetype.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("sequencetypes.idl").deleteOnExit();
        }
    }
    
    public void testArrayIdlgen() throws Exception {

        try {
            String fileName = getClass().getResource("/idlgen/array.wsdl").toString();
            idlgen.setWsdlFile(fileName);

            idlgen.setBindingName("XCORBABinding");
            idlgen.setOutputFile("arraytypes.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_array.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("arraytypes.idl").deleteOnExit();
        }
    }


    public void testEnumIdlgen() throws Exception {
        
        try {           
            String fileName = getClass().getResource("/idlgen/enum.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("BVOIPCORBABinding");
            idlgen.setOutputFile("enumtype.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_enum.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("enumtype.idl").deleteOnExit();
        }
    }
    
    public void testContentIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/content.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("ContentCORBABinding");
            idlgen.setOutputFile("contenttype.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_content.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("contenttype.idl").deleteOnExit();
        }
    }
    
    public void testAllTypeIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/alltype.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("BaseCORBABinding");
            idlgen.setOutputFile("alltype.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_alltype.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("alltype.idl").deleteOnExit();
        }
    }
    
    public void testFixedTypeIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/fixed.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("YCORBABinding");
            idlgen.setOutputFile("fixed.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_fixed.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("fixed.idl").deleteOnExit();
        }
    }
    
    public void testAnonFixedTypeIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/anonfixed.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("XCORBABinding");
            idlgen.setOutputFile("anonfixed.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_anonfixed.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("anonfixed.idl").deleteOnExit();
        }
    }

    public void testAnyTypeIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/any.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("anyInterfaceCORBABinding");
            idlgen.setOutputFile("any.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_any.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("any.idl").deleteOnExit();
        }
    }


    public void testTypeInheritanceIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/TypeInheritance.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("TypeInheritanceInterfaceCORBABinding");
            idlgen.setOutputFile("typeInheritance.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_typeInheritance.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("typeInheritance.idl").deleteOnExit();
        }
    }
    
    public void testNillableIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/nillable.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("NillableCORBABinding");
            idlgen.setOutputFile("nillable.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_nillable.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("nillable.idl").deleteOnExit();
        }
    }
    
    public void testTypedefIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/typedef.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("XCORBABinding");
            idlgen.setOutputFile("typedef.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_typedef.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("typedef.idl").deleteOnExit();
        }
    }

    public void testNestedIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/nested.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("TypeInheritanceCORBABinding");
            idlgen.setOutputFile("nested.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_nested.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("nested.idl").deleteOnExit();
        }
    }
    
    public void testNestedDerivedTypesIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/nested-derivedtypes.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("DerivedTypesCORBABinding");
            idlgen.setOutputFile("nested-derivedtypes.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_nested-derivedtypes.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("nested-derivedtypes.idl").deleteOnExit();
        }
    }

    public void testNestedComplexTypesIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/nested_complex.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("XCORBABinding");
            idlgen.setOutputFile("nested_complex.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_nested_complex.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("nested_complex.idl").deleteOnExit();
        }
    }

    public void testNestedInterfaceTypesIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/nested_interfaces.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("C.C1CORBABinding");
            idlgen.setOutputFile("nested_interfaces.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_nested_interfaces.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("nested_interfaces.idl").deleteOnExit();
        }
    }
    
    public void testDateTimeTypesIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/datetime.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("BaseCORBABinding");
            idlgen.setOutputFile("datetime.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_datetime.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("datetime.idl").deleteOnExit();
        }
    }
    
    public void testWsaddressingServerIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/wsaddressing_server.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("TestServerCORBABinding");
            idlgen.setOutputFile("wsaddress_server.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_wsaddressing_server.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("wsaddressing_server.idl").deleteOnExit();
        }
    }
    
    public void testWsaddressingAccountIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/wsaddressing_account.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("AccountCORBABinding");
            idlgen.setOutputFile("wsaddress_account.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_wsaddressing_account.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("wsaddressing_account.idl").deleteOnExit();
        }
    }
    
    public void testWsaddressingBankIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/wsaddressing_bank.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("BankCORBABinding");
            idlgen.setOutputFile("wsaddress_bank.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_wsaddressing_bank.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("wsaddressing_bank.idl").deleteOnExit();
        }
    }
    
    public void testMultipleBindingIdlgen() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/multiplebinding.wsdl").toString();
            idlgen.setWsdlFile(fileName);
                        
            idlgen.setOutputFile("multiplebinding.idl");
            idlgen.setPrintWriter(new PrintWriter(idloutput));  
            idlgen.setGenerateAllBindings(true);            
            idlgen.generateIDL(null);

            InputStream origstream = 
                getClass().getResourceAsStream("/idlgen/expected_multiplebinding.idl");
            byte orig[] = inputStreamToBytes(origstream);
            checkIDLStrings(orig, idloutput.toByteArray());           
        } finally {
            new File("multiplebinding.idl").deleteOnExit();
        }
    }

    public void testComplextypeDerivedSimpletype() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/complex_types.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("TypeTestCORBABinding");
            idlgen.setOutputFile("complex_types.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_complex_types.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("complex_types.idl").deleteOnExit();
        }
    }

    
    public void testCorbaExceptionComplexType() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/databaseService.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("DatabaseCORBABinding");
            idlgen.setOutputFile("databaseService.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_databaseService.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("databaseService.idl").deleteOnExit();
        }
    }

    public void testCorbaRecursiveStructs() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/recursivestruct.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("TestInterfaceCORBABinding");
            idlgen.setOutputFile("recursivestruct.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream =
                getClass().getResourceAsStream("/idlgen/expected_recursivestruct.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("recursivestruct.idl").deleteOnExit();
        }
    }

    public void testCoraRecursiveUnion() throws Exception {
        
        try {
            String fileName = getClass().getResource("/idlgen/recursiveunion.wsdl").toString();
            idlgen.setWsdlFile(fileName);
            
            idlgen.setBindingName("TestInterfaceCORBABinding");
            idlgen.setOutputFile("recursiveunion.idl");            
            idlgen.setPrintWriter(new PrintWriter(idloutput));
            idlgen.generateIDL(null);

            InputStream origstream = getClass().getResourceAsStream("/idlgen/expected_recursiveunion.idl");
            byte orig[] = inputStreamToBytes(origstream);

            checkIDLStrings(orig, idloutput.toByteArray());
        } finally {
            new File("recursiveunion.idl").deleteOnExit();
        }
    }






}

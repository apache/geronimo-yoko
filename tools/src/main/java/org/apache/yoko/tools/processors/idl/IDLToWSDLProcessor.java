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

package org.apache.yoko.tools.processors.idl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import antlr.collections.AST;

import org.apache.cxf.tools.common.Processor;
import org.apache.cxf.tools.common.ToolConstants;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.util.FileWriterUtil;

import org.apache.schemas.yoko.bindings.corba.AddressType;
import org.apache.schemas.yoko.bindings.corba.TypeMappingType;
import org.apache.yoko.tools.common.ProcessorEnvironment;
import org.apache.yoko.tools.common.ToolCorbaConstants;
import org.apache.yoko.tools.idlpreprocessor.DefaultIncludeResolver;
import org.apache.yoko.tools.idlpreprocessor.DefineState;
import org.apache.yoko.tools.idlpreprocessor.IdlPreprocessorReader;
import org.apache.yoko.wsdl.CorbaConstants;
import org.apache.yoko.wsdl.CorbaTypeImpl;

public class IDLToWSDLProcessor implements Processor {

    protected ToolContext toolContext;
    private String idl;
    private String schemaFilename;
    private String importSchemaFilename;
    private String logical;
    private String physical;
    private String outputDir;
    private ProcessorEnvironment env;
    private Writer outputWriter;
    private Writer schemaOutputWriter;   
    private Writer logicalOutputWriter;
    private Writer physicalOutputWriter;    
    
    public void process() throws ToolException {
        idl = getBaseFilename(env.get(ToolCorbaConstants.CFG_IDLFILE).toString());
        checkFileOptions();                
        try {
            parseIDL();
        } catch (Exception e) {
            throw new ToolException(e);
        }
    }


    public void setEnvironment(ToolContext toolCtx) {
        toolContext = toolCtx;
    }

    public void setOutputWriter(Writer writer) {
        outputWriter = writer;
    }
    
    public void setSchemaOutputWriter(Writer writer) {
        schemaOutputWriter = writer;
    }
    
    public void setLogicalOutputWriter(Writer writer) {
        logicalOutputWriter = writer;
    }
    
    public void setPhysicalOutputWriter(Writer writer) {
        physicalOutputWriter = writer;
    }

    
    private void checkFileOptions() {
                
        if (env.optionSet(ToolCorbaConstants.CFG_LOGICAL)) {
            // set the logical filename 
            logical = env.get(ToolCorbaConstants.CFG_LOGICAL).toString();        
        }
        if (env.optionSet(ToolCorbaConstants.CFG_PHYSICAL)) {
            // set the physical file name
            physical = env.get(ToolCorbaConstants.CFG_PHYSICAL).toString();            
        }
        if (env.optionSet(ToolCorbaConstants.CFG_SCHEMA)) {
            // deal with writing schema types to the schema specified file
            schemaFilename = env.get(ToolCorbaConstants.CFG_SCHEMA).toString();
        }
        if (env.optionSet(ToolCorbaConstants.CFG_IMPORTSCHEMA)) {
            // deal with importing schema types 
            importSchemaFilename = env.get(ToolCorbaConstants.CFG_IMPORTSCHEMA).toString();
        }                
    }
    
        
    public void parseIDL() throws Exception {
        String location = env.get(ToolCorbaConstants.CFG_IDLFILE).toString();
        File file = new File(location).getAbsoluteFile();
        if (!file.exists()) {
            throw new Exception("IDL file " + file.getName() + " doesn't exist");
        }
        URL orig = file.toURI().toURL();
        DefaultIncludeResolver includeResolver = new DefaultIncludeResolver(file.getParentFile());
        DefineState defineState = new DefineState(new HashMap<String, String>());
        IdlPreprocessorReader preprocessor = new IdlPreprocessorReader(orig,
                                                                       location,
                                                                       includeResolver,
                                                                       defineState);
        IDLParser parser = new IDLParser(new IDLLexer(new java.io.LineNumberReader(preprocessor)));
        parser.specification();
        AST idlTree = parser.getAST();

        if (env.isVerbose()) {
            System.out.println(idlTree.toStringTree());
        }               

        // target namespace
        String tns = (String) env.get(ToolCorbaConstants.CFG_TNS);
        if (tns == null) {
            tns = CorbaConstants.WSDL_NS_URI + idl;
        }
        // XmlSchema namespace
        String schemans = (String) env.get(ToolCorbaConstants.CFG_SCHEMA_NAMESPACE);
        
        // corba typemap namespace
        String corbatypemaptns = (String) env.get(ToolCorbaConstants.CFG_CORBATYPEMAP_NAMESPACE);
        
        try {
            WSDLASTVisitor visitor = new WSDLASTVisitor(tns, schemans, corbatypemaptns);
            Definition def = visitor.getDefinition();
            if (env.optionSet(ToolCorbaConstants.CFG_SEQUENCE_OCTET_TYPE)) {
                visitor.setSequenceOctetType((String) env.get(ToolCorbaConstants.CFG_SEQUENCE_OCTET_TYPE));
            }
            if (env.optionSet(ToolCorbaConstants.CFG_SCHEMA_NAMESPACE)) {
                //visitor.getDefinition()
                def.addNamespace(ToolCorbaConstants.CFG_SCHEMA_NAMESPACE_PREFIX,
                                  (String) env.get(ToolCorbaConstants.CFG_SCHEMA_NAMESPACE));
            }
            if (env.optionSet(ToolCorbaConstants.CFG_BOUNDEDSTRINGS)) {
                visitor.setBoundedStringOverride(true);
            }
            
            if (env.optionSet(ToolCorbaConstants.CFG_SCHEMA)) {
                visitor.setSchemaGenerated(true);
                // generate default namespace for schema if -T is used alone.
                if (env.get(ToolCorbaConstants.CFG_SCHEMA_NAMESPACE) == null) {
                    visitor.updateSchemaNamespace(def.getTargetNamespace() + "-types");
                    def.addNamespace(ToolCorbaConstants.CFG_SCHEMA_NAMESPACE_PREFIX, def.getTargetNamespace()
                                                                                     + "-types");
                }
            }
            
            visitor.visit(idlTree);  
                      
            cleanUpTypeMap(visitor.getTypeMap());
            
            Binding[] bindings = visitor.getCorbaBindings();
            generateCORBAService(def, bindings);
            writeDefinitions(visitor);           
        } catch (Exception ex) {           
            throw new ToolException(ex.getMessage(), ex);
        }
    }
    
    // Sets the output directory and the generated filenames.
    // Output directory is specified 
    //     - File names have no path specified
    //     - File names do have specified so they take precedence.
    // Output directory is not specified
    //     - File names have no path specified so use current directory.
    //     - File names have full path specified.
    private void writeDefinitions(WSDLASTVisitor visitor) 
        throws Exception {
        
        Object obj = env.get(ToolConstants.CFG_OUTPUTDIR);
        outputDir = ".";
        if (obj != null) {
            outputDir =  obj.toString();
        }
                        
        if (env.optionSet(ToolCorbaConstants.CFG_LOGICAL)
            || env.optionSet(ToolCorbaConstants.CFG_PHYSICAL)
            || env.optionSet(ToolCorbaConstants.CFG_SCHEMA)
            || env.optionSet(ToolCorbaConstants.CFG_IMPORTSCHEMA)) {
                        
            if (logical == null || physical == null) {
                if (outputWriter == null) {
                    outputWriter = getOutputWriter(idl + ".wsdl", outputDir);
                }
                String separator = System.getProperty("file.separator");
                File file = null;
                if (env.get(ToolConstants.CFG_OUTPUTDIR) != null) {
                    file = new File(outputDir + separator + idl + ".wsdl");                        
                } else {
                    file = new File(idl + ".wsdl");                        
                }   
                visitor.setIdlFile(file.getAbsolutePath());                                    
            }            
            
            if (logical != null) {                
                logical = getFilePath(logical).getAbsolutePath();
                if  (logicalOutputWriter == null) {
                    logicalOutputWriter = createOutputWriter(logical);                    
                }                                  
            }
                            
            if (physical != null) {               
                physical = getFilePath(physical).getAbsolutePath();
                if (physicalOutputWriter == null) {            
                    physicalOutputWriter = createOutputWriter(physical); 
                }                                
            }            
            
            if (schemaFilename != null) {                
                schemaFilename = getFilePath(schemaFilename).getAbsolutePath();
                if (schemaOutputWriter == null) {            
                    schemaOutputWriter = createOutputWriter(schemaFilename); 
                }    
                File file = new File(schemaFilename);                
                URI uri = file.toURI();
                schemaFilename = uri.toString();                                           
            }
                        
            if (importSchemaFilename != null) {
                importSchemaFilename = getImportFile(importSchemaFilename);               
                visitor.setImportSchema(importSchemaFilename);
            }                     
                        
            visitor.setOutputDir(outputDir);   
            visitor.writeDefinitions(outputWriter, schemaOutputWriter,
                                     logicalOutputWriter, physicalOutputWriter, 
                                     schemaFilename, logical, physical);
        } else {
            if (outputWriter == null) {
                outputWriter = getOutputWriter(idl + ".wsdl", outputDir); 
            }
            visitor.writeDefinition(outputWriter);
        }         
    }
    
    // Get the imported schema file.
    private String getImportFile(String importFilename) {
        // check that file exists        
        File file = new File(importFilename);                        
        
        if (!file.exists()) {            
            if (!file.isAbsolute()) {
                String separator = System.getProperty("file.separator");
                String userdir = System.getProperty("user.dir");                
                file = new File(userdir + separator + importFilename);
            }
            if (!file.exists()) {
                String msg = importFilename + " File not found";
                FileNotFoundException ex = new FileNotFoundException(msg);
                System.err.println("IDLToWsdl Error : " + ex.getMessage());
                System.err.println();            
                ex.printStackTrace();            
                System.exit(1);
            } else {
                URI url = file.toURI();
                return url.toString();
                
            }            
        } else {
            URI url = file.toURI();
            return url.toString();
        }
        return null;
    }
    
    // check if file has a fully qualified path
    private boolean isFQPath(String ifile) {
        File file = new File(ifile);
        return file.isAbsolute();
    }
    
    private Writer createOutputWriter(String name) throws Exception {        
        String outDir = outputDir;
        String filename = name;               
        int index = name.lastIndexOf(System.getProperty("file.separator"));
        outDir = name.substring(0, index);
        filename = name.substring(index + 1, name.length());                        
        return getOutputWriter(filename, outDir);        
    }
    
    // Gets the fully qualified path of a file.
    private File getFilePath(String ifile) {        
        String separator = System.getProperty("file.separator");
        StringTokenizer token = new StringTokenizer(ifile, separator);        

        if (token.countTokens() == 1) {
            if (env.get(ToolConstants.CFG_OUTPUTDIR) != null) {
                return new File(outputDir + separator + ifile);
            } else {
                return new File(ifile);
            }
        } else {
            return new File(ifile);
        }                           
    }           
    
    public Writer getOutputWriter(String filename, String outputDirectory) throws Exception {

        FileWriterUtil fw = new FileWriterUtil(outputDirectory);        
        
        if (env.optionSet(ToolCorbaConstants.CFG_WSDL_ENCODING)) { 
            String encoding = env.get(ToolCorbaConstants.CFG_WSDL_ENCODING).toString();            
            return fw.getWriter(new File(outputDirectory, filename), encoding); 
        } else {
            return fw.getWriter("", filename); 
        }       
    }
    
    public void setEnvironment(ProcessorEnvironment penv) {
        env = penv;
    }

    public ProcessorEnvironment getEnvironment() {
        return env;
    }

    public String getBaseFilename(String ifile) {
        String fileName = ifile;
        StringTokenizer token = new StringTokenizer(ifile, "\\/");

        while (token.hasMoreTokens()) {
            fileName = token.nextToken();
        }
        if (fileName.endsWith(".idl")) {
            fileName = new String(fileName.substring(0, fileName.length() - 4));
        }
        return fileName;
    }

    public void generateCORBAService(Definition def, Binding[] bindings) throws Exception {
        for (int i = 0; i < bindings.length; i++) {
            String portTypeName = bindings[i].getPortType().getQName().getLocalPart();
            QName serviceName = new QName(def.getTargetNamespace(),
                                          portTypeName + "CORBAService");
            Service service = def.createService();
            service.setQName(serviceName);
            Port port = def.createPort();
            port.setName(portTypeName + "CORBAPort");
            AddressType address =
                (AddressType) def.getExtensionRegistry().createExtension(Port.class,
                                                                         CorbaConstants.NE_CORBA_ADDRESS);
            
            String addr = null;
            String addrFileName = (String) env.get(ToolCorbaConstants.CFG_ADDRESSFILE); 
            if (addrFileName != null) {
                try {
                    File addrFile = new File(addrFileName);
                    FileReader fileReader = new FileReader(addrFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    addr = bufferedReader.readLine();
                } catch (Exception ex) {
                    throw new ToolException(ex.getMessage(), ex);
                }
            } else {
                addr = (String) env.get(ToolCorbaConstants.CFG_ADDRESS);
            }
            if (addr == null) {
                addr = "IOR:";
            }
            address.setLocation(addr);
            port.addExtensibilityElement(address);
            service.addPort(port);
            port.setBinding(bindings[i]);
            def.addService(service);
        }
    }

    public void cleanUpTypeMap(TypeMappingType typeMap) {
        List<CorbaTypeImpl> types = typeMap.getStructOrExceptionOrUnion();
        if (types != null) {
            for (int i = 0; i < types.size(); i++) {
                CorbaTypeImpl type = types.get(i);
                if (type.getQName() != null) {
                    type.setName(type.getQName().getLocalPart());
                    type.setQName(null);
                }
            }
        }
    }
    
}

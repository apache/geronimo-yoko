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

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import antlr.ASTVisitor;
import antlr.collections.AST;

import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.wsdl.JAXBExtensionHelper;
import org.apache.cxf.wsdl.WSDLConstants;
import org.apache.schemas.yoko.bindings.corba.AddressType;
import org.apache.schemas.yoko.bindings.corba.BindingType;
import org.apache.schemas.yoko.bindings.corba.OperationType;
import org.apache.schemas.yoko.bindings.corba.TypeMappingType;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaSerializer;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.apache.yoko.tools.common.ToolCorbaConstants;
import org.apache.yoko.tools.common.WSDLUtils;
import org.apache.yoko.wsdl.CorbaConstants;

public class WSDLASTVisitor implements ASTVisitor {
    
    
    Definition definition;    
    XmlSchema schema;
    XmlSchemaCollection schemas;    
    TypeMappingType typeMap;
    ScopeNameCollection scopedNames;
    ScopeNameCollection recursionList;
    DeferredActionCollection deferredActions;
    String targetNamespace;
    private boolean declaredWSAImport;

    private XmlSchemaType sequenceOctetType;
    private boolean boundedStringOverride;
    private String idlFile;    
    private String outputDir;
    private String importSchemaFilename; 
    private boolean schemaGenerated;
    
    public WSDLASTVisitor(String tns, String schemans, String corbatypemaptns)
        throws WSDLException, JAXBException {
        
        definition = createWsdlDefinition(tns);
        
        targetNamespace = tns;
        schemas = new XmlSchemaCollection();
        scopedNames = new ScopeNameCollection();
        deferredActions = new DeferredActionCollection();       
        schema = createSchema(schemans);
        declaredWSAImport = false;
        
        addAnyType();
        
        createCorbaTypeMap(corbatypemaptns);
        
        // idl:sequence<octet> maps to xsd:base64Binary by default
        sequenceOctetType = schemas.getTypeByQName(Constants.XSD_BASE64);
        
        // treat bounded corba:string/corba:wstring as unbounded if set to true
        setBoundedStringOverride(false);
    }

    public void visit(AST node) {
        // <specification> ::= <definition>+

        while (node != null) {
            DefinitionVisitor definitionVisitor = new DefinitionVisitor(new Scope(),
                                                                        this);
            definitionVisitor.visit(node);

            node = node.getNextSibling();
        }
        
        try {           
            attachSchema();            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void setSchemaGenerated(boolean value) {
        schemaGenerated = value;
    }
    
    public boolean isSchemaGenerated() {
        return schemaGenerated;
    }
    
    public void updateSchemaNamespace(String name) throws Exception  {
        schema.setTargetNamespace(name);                   
    }
           
    public void setIdlFile(String idl) {
        idlFile = idl;
    }
    
    public String getIdlFile() {
        return idlFile;
    }
    
    public void setOutputDir(String outDir) {
        outputDir = outDir;
    }
    
    public String getOutputDir() {
        return outputDir;
    }
    
    public Definition getDefinition() {
        return definition;
    }

    public XmlSchema getSchema() {
        return schema;
    }
    
    public XmlSchemaCollection getSchemas() {
        return schemas;
    }
    
    public ScopeNameCollection getScopedNames() {
        return scopedNames;
    }

    public ScopeNameCollection getRecursionList() {
        return recursionList;
    }

    public DeferredActionCollection getDeferredActions() {
        return deferredActions;
    }
    
    public TypeMappingType getTypeMap() {
        return typeMap;
    }
    
    public XmlSchemaType getSequenceOctetType() {
        return sequenceOctetType;
    }
    
    public void setImportSchema(String filename) {        
        importSchemaFilename = filename;
    }
    
    public String getImportSchemaFilename() {
        return importSchemaFilename;
    }        
    
    public void setSequenceOctetType(String type) throws Exception {
        XmlSchemaType stype = null;
        if (type.equals(ToolCorbaConstants.CFG_SEQUENCE_OCTET_TYPE_BASE64BINARY)) {
            stype = schemas.getTypeByQName(Constants.XSD_BASE64);
        } else if (type.equals(ToolCorbaConstants.CFG_SEQUENCE_OCTET_TYPE_HEXBINARY)) {
            stype = schemas.getTypeByQName(Constants.XSD_HEXBIN);
        } else {
            throw new ToolException("WSDLASTVisitor: Invalid XmlSchemaType specified " 
                                    + "for idl:sequence<octet> mapping.");
        }
        sequenceOctetType = stype;
    }
    
    public boolean getBoundedStringOverride() {
        return boundedStringOverride;
    }
    
    public void setBoundedStringOverride(boolean value) {
        boundedStringOverride = value;
    }
    
    public Binding[] getCorbaBindings() {
        List<Binding> result = new ArrayList<Binding>();
        Map bindings = definition.getBindings();
        Iterator it = bindings.values().iterator();
        while (it.hasNext()) {
            Binding binding = (Binding) it.next();
            List extElements = binding.getExtensibilityElements();
            for (int i = 0; i < extElements.size(); i++) {
                ExtensibilityElement el = (ExtensibilityElement) extElements.get(i);
                if (el.getElementType().equals(CorbaConstants.NE_CORBA_BINDING)) {
                    result.add(binding);
                    break;
                }
            }
        }
        return (Binding[]) result.toArray(new Binding[result.size()]);
    }

    public boolean writeDefinition(Writer writer) throws Exception {        
        writeDefinition(definition, writer);
        return true;
    }
    
    public boolean writeDefinition(Definition def, Writer writer) throws Exception {       
        WSDLUtils.writeWSDL(def, writer);
        return true;
    }
    
    public boolean writeSchemaDefinition(Definition definit, Writer writer) throws Exception  {
        Definition def = createWsdlDefinition(targetNamespace + "-types");
        def.createTypes();        
        def.setTypes(definit.getTypes());                       
        WSDLUtils.writeSchema(def, writer);
        return true;
    }
    
    // REVISIT - When CXF corrects the wsdlValidator - will switch back on the 
    // validation of the generated wsdls.
    public boolean writeDefinitions(Writer writer, Writer schemaWriter,
                                    Writer logicalWriter, Writer physicalWriter, 
                                    String schemaFilename, String logicalFile, 
                                    String physicalFile) throws Exception {
                        
        Definition logicalDef = getLogicalDefinition(schemaFilename, schemaWriter);
        Definition physicalDef = null;
        // schema only
        if ((schemaFilename != null || importSchemaFilename != null) 
            && (logicalFile == null && physicalFile == null)) {
            physicalDef = getPhysicalDefinition(logicalDef, true);
        } else {
            physicalDef = getPhysicalDefinition(logicalDef, false);
        }                
        
        // write out logical file -L and physical in default
        if (logicalFile != null && physicalFile == null) {
            writeDefinition(logicalDef, logicalWriter);            
            //validateWsdl(logicalFile);
            physicalDef = addWsdlImport(physicalDef, logicalFile);
            writeDefinition(physicalDef, writer);
            //validateWsdl(physicalFile);            
        } else if (logicalFile != null && physicalFile != null) {
            // write both logical -L and physical files -P           
            writeDefinition(logicalDef, logicalWriter);
            //validateWsdl(logicalFile);
            physicalDef = addWsdlImport(physicalDef, logicalFile);
            writeDefinition(physicalDef, physicalWriter);            
            //validateWsdl(physicalFile);
        } else if (logicalFile == null && physicalFile != null) {
            // write pyhsical file -P and logical in default
            writeDefinition(logicalDef, writer);            
            //validateWsdl(getIdlFile());
            physicalDef = addWsdlImport(physicalDef, getIdlFile());            
            writeDefinition(physicalDef, physicalWriter);            
            //validateWsdl(physicalFile);
        } else if ((logicalFile == null && physicalFile == null)
            && (schemaFilename != null || importSchemaFilename != null)) {           
            // write out the schema file -T and default of logical
            // and physical together.
            writeDefinition(physicalDef, writer);               
            //validateWsdl(getIdlFile());   
        } else if (logicalFile == null && physicalFile == null
            && schemaFilename == null) {
            // write out the default file
            writeDefinition(definition, writer);            
            //validateWsdl(getIdlFile());
        }        
      
        return true;
    }
    
    // Writes import into either a logical, physical or schema file.
    private Definition addWsdlImport(Definition def, String filename) {
        Import importDef = def.createImport();
        File file = new File(filename);        
        importDef.setLocationURI(file.toURI().toString());
        importDef.setNamespaceURI(definition.getTargetNamespace());
        def.addImport(importDef);
        return def;
    } 
    
    // Gets the logical definition for a file - an import will be added for the 
    // schema types if -T is used and a separate schema file generated.
    // if -n is used an import will be added for the schema types and no types generated.
    private Definition getLogicalDefinition(String schemaFilename, Writer schemaWriter) 
        throws WSDLException, JAXBException, Exception {        
        Definition def = createWsdlDefinition(targetNamespace);

        // checks for -T option.
        if (schemaFilename != null) {
            writeSchemaDefinition(definition, schemaWriter);            
            def = addSchemaImport(def, schemaFilename);
        } else {
            // checks for -n option
            if (importSchemaFilename == null) {                
                Types types = definition.getTypes();
                def.setTypes(types);
            } else {
                
                def = addSchemaImport(def, importSchemaFilename);
            }
        }            
        
        Iterator iter = definition.getAllPortTypes().values().iterator();
        while (iter.hasNext()) {
            PortType port = (PortType)iter.next();
            def.addPortType(port);
        }
        
        iter = definition.getMessages().values().iterator();
        while (iter.hasNext()) {
            Message msg = (Message)iter.next();
            def.addMessage(msg);
        }                
        
        iter = definition.getNamespaces().values().iterator();       
        while (iter.hasNext()) {
            String namespace = (String)iter.next();
            String prefix = definition.getPrefix(namespace);
            if (!prefix.equals("corba")) {
                def.addNamespace(prefix, namespace);
            } else {
                def.removeNamespace(prefix);
            }
        }
        
        iter = definition.getImports().values().iterator();       
        while (iter.hasNext()) {
            Import importType = (Import)iter.next();
            def.addImport(importType);            
        }
        
        def.setDocumentationElement(definition.getDocumentationElement());
        def.setDocumentBaseURI(definition.getDocumentBaseURI());
        
        return def;
    }
    
    // Write the physical definitions to a file.
    private Definition getPhysicalDefinition(Definition logicalDef, boolean schemaOnly) 
        throws WSDLException, JAXBException {
        
        Definition def = null;        
        if (schemaOnly) {
            def = logicalDef;
        } else {
            def = createWsdlDefinition(targetNamespace);
        }
                
        Iterator iter = definition.getNamespaces().values().iterator();       
        while (iter.hasNext()) {
            String namespace = (String)iter.next();
            String prefix = definition.getPrefix(namespace);
            def.addNamespace(prefix, namespace); 
        }
        
        iter = definition.getAllBindings().values().iterator();
        while (iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            def.addBinding(binding);
        }
        iter = definition.getAllServices().values().iterator();
        while (iter.hasNext()) {
            Service service = (Service)iter.next(); 
            def.addService(service);
        }
        iter = definition.getExtensibilityElements().iterator();
        while (iter.hasNext()) {
            ExtensibilityElement ext = (ExtensibilityElement)iter.next();
            def.addExtensibilityElement(ext);
        }                
        
        def.setExtensionRegistry(definition.getExtensionRegistry());
                
        return def;
    }         

    private boolean validateWsdl(String wsdlFilename) throws Exception {                   
        //String[] args = new String[] {wsdlFilename};
        //WSDLValidator.main(args);
        // REVISIT - When CXF publishes an api for the wsdlvalidator we can then
        // switch to it and delete the files if there is an error.
        // String separator = System.getProperty("file.separator");   
        /* File file = new File(outDir + separator + wsdlFilename);
            if (file.exists()) {
                file.delete();
            }  
        }*/
        // REVISIT - Once the validator is cleaned up in cxf 
        // will switch uncomment this back in 
        /*File file = new File(wsdlFilename);
        URL wsdlURL = file.toURL();        

        try {
            ToolContext context = new ToolContext(); 
            context.put(ToolConstants.CFG_WSDLURL, wsdlURL.toString()); 
            WSDL11Validator wsdlValidator = new WSDL11Validator(null, context);
            return wsdlValidator.isValid();
        } catch (ToolException e) {
         // failed, handle exception here
        }*/

        return true;
    }
        
    private Definition createWsdlDefinition(String tns) throws WSDLException, JAXBException {
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        Definition wsdlDefinition = wsdlFactory.newDefinition();        
        wsdlDefinition.setTargetNamespace(tns);

        // REVISIT when get a new deploy of cxf
        //wsdlDefinition.addNamespace(WSDLConstants.WSDL_PREFIX, WSDLConstants.WSDL11_NAMESPACE);
        wsdlDefinition.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        wsdlDefinition.addNamespace(WSDLConstants.NP_SCHEMA_XSD, WSDLConstants.NU_SCHEMA_XSD);
        wsdlDefinition.addNamespace(WSDLConstants.SOAP11_PREFIX, WSDLConstants.SOAP11_NAMESPACE);
       // wsdlDefinition.addNamespace(WSDLConstants.TNS_PREFIX, tns);
        wsdlDefinition.addNamespace("tns", tns);
        wsdlDefinition.addNamespace(CorbaConstants.NP_WSDL_CORBA, CorbaConstants.NU_WSDL_CORBA);
        addCorbaExtensions(wsdlDefinition.getExtensionRegistry());
        return wsdlDefinition;
    }
    
    private XmlSchema createSchema(String schemans) {
        // if no XmlSchema target namespace was specified, default to the 
        // definition target namespace
        if (schemans ==  null) {
            schemans = definition.getTargetNamespace();
        }
        XmlSchema xmlSchema = new XmlSchema(schemans, schemas);        
        return xmlSchema;
    }
    
    private void createCorbaTypeMap(String corbatypemaptns) throws WSDLException { 
        typeMap = (TypeMappingType)
            definition.getExtensionRegistry().createExtension(Definition.class,
                                                              CorbaConstants.NE_CORBA_TYPEMAPPING);
        if (corbatypemaptns == null) {
            typeMap.setTargetNamespace(definition.getTargetNamespace()
                + "/"
                + CorbaConstants.NS_CORBA_TYPEMAP);
        } else {
            typeMap.setTargetNamespace(corbatypemaptns);
        }
        definition.addExtensibilityElement(typeMap);
    }

    private void addCorbaExtensions(ExtensionRegistry extReg) throws JAXBException {
        try {                      
            JAXBExtensionHelper.addExtensions(extReg, Binding.class, BindingType.class);
            JAXBExtensionHelper.addExtensions(extReg, BindingOperation.class, OperationType.class);
            JAXBExtensionHelper.addExtensions(extReg, Definition.class, TypeMappingType.class);
            JAXBExtensionHelper.addExtensions(extReg, Port.class, AddressType.class);

            extReg.mapExtensionTypes(Binding.class, CorbaConstants.NE_CORBA_BINDING, BindingType.class);
            extReg.mapExtensionTypes(BindingOperation.class, CorbaConstants.NE_CORBA_OPERATION,
                                     org.apache.schemas.yoko.bindings.corba.OperationType.class);
            extReg.mapExtensionTypes(Definition.class, CorbaConstants.NE_CORBA_TYPEMAPPING,
                                     TypeMappingType.class);
            extReg.mapExtensionTypes(Port.class, CorbaConstants.NE_CORBA_ADDRESS,
                                     org.apache.schemas.yoko.bindings.corba.AddressType.class);
        } catch (javax.xml.bind.JAXBException ex) {
            throw new JAXBException(ex.getMessage());
        }
    }    

    private void attachSchema() throws Exception {
        Types types = definition.createTypes();
        Schema wsdlSchema = (Schema) 
            definition.getExtensionRegistry().createExtension(Types.class,
                                                              new QName(Constants.URI_2001_SCHEMA_XSD,
                                                                        "schema"));

        // See if a NamespaceMap has already been added to the schema (this can be the case with object 
        // references.  If so, simply add the XSD URI to the map.  Otherwise, create a new one.
        NamespaceMap nsMap = null;
        try {
            nsMap = (NamespaceMap)schema.getNamespaceContext();
        } catch (ClassCastException ex) {
            // Consume.  This will mean that the context has not been set.
        }
        if (nsMap == null) {
            nsMap = new NamespaceMap();
            nsMap.add("xs", Constants.URI_2001_SCHEMA_XSD);
            schema.setNamespaceContext(nsMap);
        } else {
            nsMap.add("xs", Constants.URI_2001_SCHEMA_XSD);
        }
        if (isSchemaGenerated()) {
            nsMap.add("tns", schema.getTargetNamespace());
        }
        org.w3c.dom.Element el = XmlSchemaSerializer.serializeSchema(schema, true)[0].getDocumentElement();
        wsdlSchema.setElement(el);
                
        types.addExtensibilityElement(wsdlSchema);

        definition.setTypes(types);
    }
    
    private Definition addSchemaImport(Definition def, String schemaFilename) throws Exception {
                        
        Types types = def.createTypes();
        Schema wsdlSchema = (Schema) 
            def.getExtensionRegistry().createExtension(Types.class,
                                                   new QName(Constants.URI_2001_SCHEMA_XSD,
                                                   "schema"));
        
        SchemaImport schemaimport =  wsdlSchema.createImport();
        schemaimport.setNamespaceURI(schema.getTargetNamespace());
        schemaimport.setSchemaLocationURI(schemaFilename);
        wsdlSchema.addImport(schemaimport);               
        types.addExtensibilityElement(wsdlSchema);
        def.setTypes(types);
        return def;
    }

    private void addAnyType() {
        XmlSchema[] schemaList = schemas.getXmlSchemas();
        if (schemaList != null) {
            for (int i = 0; i < schemaList.length; i++) {
                if (schemaList[i].getTargetNamespace().equals(Constants.URI_2001_SCHEMA_XSD)) {
                    XmlSchemaType anyType = new XmlSchemaType(schemaList[0]);
                    anyType.setName(Constants.XSD_ANYTYPE.getLocalPart());
                    schemaList[i].addType(anyType);
                    break;
                }
            }
        }
    }       
    
    public boolean getDeclaredWSAImport() {
        return declaredWSAImport;
    }
    
    public void setDeclaredWSAImport(boolean declaredImport) {
        declaredWSAImport = declaredImport;        
    }        

}

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

package org.apache.yoko.tools.processors.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.tools.common.Processor;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.ToolException;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection; 

import org.apache.yoko.tools.common.ProcessorEnvironment;

public class WSDLToProcessor implements Processor {
    
    protected static final Logger LOG = 
        LogUtils.getL7dLogger(WSDLToProcessor.class);
    protected Definition wsdlDefinition;    
    protected ToolContext toolContext;
    protected WSDLFactory wsdlFactory;
    protected WSDLReader wsdlReader;
    
    private XmlSchema schematype;
    private ProcessorEnvironment env;
    private ExtensionRegistry extReg;        
    private List<Definition> importedDefinitions = new ArrayList<Definition>();    
    private XmlSchemaCollection schemaCol = new XmlSchemaCollection();
    private List<XmlSchema> schematypeList = new ArrayList<XmlSchema>();
    private List<Schema> schemaList;  
    private List<String> schemaTargetNamespaces = new ArrayList<String>();
    
    
    
    public WSDLToProcessor() {
        schemaList = new ArrayList<Schema>();
    }

    public void setEnvironment(ToolContext toolCtx) {
        toolContext = toolCtx;
    } 

    public void parseWSDL(String wsdlUrl) {
        try {           
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);            
            if (getExtensionRegistry() != null) {
                reader.setExtensionRegistry(extReg);
            }
            wsdlDefinition = reader.readWSDL(wsdlUrl);
            schemaCol.setBaseUri(wsdlDefinition.getDocumentBaseURI());
            parseImports(wsdlDefinition);
            buildWSDLDefinition();    
        } catch (WSDLException we) {
            org.apache.cxf.common.i18n.Message msg = 
                    new org.apache.cxf.common.i18n.Message(
                    "FAIL_TO_CREATE_WSDL_DEFINITION", LOG);
            throw new ToolException(msg, we);
        } 
    }
            
    private void buildWSDLDefinition() {
        for (Definition def : importedDefinitions) {
            this.wsdlDefinition.addNamespace(def.getPrefix(def.getTargetNamespace()), def
                .getTargetNamespace());
            Object[] services = def.getServices().values().toArray();
            for (int i = 0; i < services.length; i++) {
                this.wsdlDefinition.addService((Service)services[i]);
            }

            Object[] messages = def.getMessages().values().toArray();
            for (int i = 0; i < messages.length; i++) {
                this.wsdlDefinition.addMessage((Message)messages[i]);
            }

            Object[] bindings = def.getBindings().values().toArray();
            for (int i = 0; i < bindings.length; i++) {
                this.wsdlDefinition.addBinding((Binding)bindings[i]);
            }

            Object[] portTypes = def.getPortTypes().values().toArray();
            for (int i = 0; i < portTypes.length; i++) {
                this.wsdlDefinition.addPortType((PortType)portTypes[i]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void parseImports(Definition def) {
        List<Import> importList = new ArrayList<Import>();
        Map imports = def.getImports();
        for (Iterator iter = imports.keySet().iterator(); iter.hasNext();) {
            String uri = (String)iter.next();
            importList.addAll((List<Import>)imports.get(uri));
        }
        for (Import impt : importList) {
            parseImports(impt.getDefinition());
            importedDefinitions.add(impt.getDefinition());
        }
    }

    private void extractSchema(Definition def) {
        Types typesElement = def.getTypes();
        if (typesElement != null) {
            Iterator ite = typesElement.getExtensibilityElements().iterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                org.w3c.dom.Element schemaElem = null;
                if (obj instanceof Schema) {
                    Schema schema = (Schema) obj;
                    addSchema(schema);
                    schemaElem = schema.getElement();
                } else if (obj instanceof UnknownExtensibilityElement) {
                    org.w3c.dom.Element elem = ((UnknownExtensibilityElement) obj).getElement();
                    if (elem.getLocalName().equals("schema")) {
                        schemaElem = elem;
                    }
                }
                if (schemaElem != null) {
                    schematype = schemaCol.read(schemaElem);                    
                    schematypeList.add(schematype);
                }
            }
        }
    }

    public void process() throws ToolException {
        if (env == null) {
            env = new ProcessorEnvironment();
            env.put("wsdlurl", wsdlDefinition.getDocumentBaseURI());
        }

        schemaTargetNamespaces.clear();
        extractSchema(wsdlDefinition);
        for (Definition def : importedDefinitions) {
            extractSchema(def);
        }
        schemaTargetNamespaces.clear();     
    }
    
    private boolean isSchemaImported(Schema schema) {
        return schemaList.contains(schema);
    }
    
    private boolean isSchemaParsed(String targetNamespace) {
        if (!schemaTargetNamespaces.contains(targetNamespace)) {
            schemaTargetNamespaces.add(targetNamespace);
            return false;
        } else {
            return true;
        }
    }
    
    @SuppressWarnings("unchecked")
    private void addSchema(Schema schema) {
        Map<String, List> imports = schema.getImports();
        if (imports != null && imports.size() > 0) {
            Collection<String> importKeys = imports.keySet();
            for (String importNamespace : importKeys) {
                if (!isSchemaParsed(importNamespace + "?file=" + schema.getDocumentBaseURI())) {
                    List<SchemaImport> schemaImports = imports.get(importNamespace);
                    for (SchemaImport schemaImport : schemaImports) {
                        Schema tempImport = schemaImport.getReferencedSchema();
                        if (tempImport != null && !isSchemaImported(tempImport)) {
                            addSchema(tempImport);
                        }
                    }
                }
            }
        }
        if (!isSchemaImported(schema)) {
            schemaList.add(schema);            
        }
    }

    public Definition getWSDLDefinition() {
        return this.wsdlDefinition;
    }

    public void setWSDLDefinition(Definition definition) {
        wsdlDefinition = definition;
    }

    public XmlSchema getXmlSchemaType() {
        return this.schematype;
    }

    public List<XmlSchema> getXmlSchemaTypes() {
        return this.schematypeList;
    }
        
    public void setEnvironment(ProcessorEnvironment environement) {
        this.env = environement;
    }

    public ProcessorEnvironment getEnvironment() {
        return this.env;
    }

    public void setExtensionRegistry(ExtensionRegistry reg) {
        extReg = reg;
    }
    
    public ExtensionRegistry getExtensionRegistry() {
        return extReg;
    }   
}

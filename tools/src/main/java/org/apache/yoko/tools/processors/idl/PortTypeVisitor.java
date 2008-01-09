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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;

import javax.xml.namespace.QName;

import antlr.collections.AST;

import org.apache.schemas.yoko.bindings.corba.BindingType;

import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.yoko.wsdl.CorbaConstants;
import org.apache.yoko.wsdl.CorbaTypeImpl;

public class PortTypeVisitor extends VisitorBase {   

    Definition definition;
    ExtensionRegistry extReg;
    PortType portType;
    String module;

    public PortTypeVisitor(Scope scope,
                           WSDLASTVisitor wsdlASTVisitor) {
        super(scope, wsdlASTVisitor);
        definition = wsdlVisitor.getDefinition();
        extReg = definition.getExtensionRegistry();
    }

    public static boolean accept(AST node) {
        if (node.getType() == IDLTokenTypes.LITERAL_interface) {
            return true;
        }
        return false;
    }
    
    public void visit(AST node) {
        // <interface> ::= <interface_dcl>
        //               | <forward_dcl>
        // <interface_dcl> ::= <interface_header> "{" <interface_body> "}"
        // <forward_dcl> ::= ["abstract" | "local"] "interface" <identifier>
        // <interface_header> ::= ["abstract" | "local"] "interface" <identifier>
        //                        [<interface_inheritance_spec>]
        // <interface_body> ::= <export>*
        // <export> ::= <type_dcl> ";"
        //            | <const_dcl> ";"
        //            | <except_dcl> ";"
        //            | <attr_dcl> ";"
        //            | <op_dcl> ";"
        // <interface_inheritance_spec> ::= ":" <interface_name> { "," <interface_name> }*
        // <interface_name> ::= <scoped_name>
        
        
        AST identifierNode = node.getFirstChild();        
        
        // Check if its a forward declaration
        if (identifierNode.getFirstChild() == null && identifierNode.getNextSibling() == null) {
            visitForwardDeclaredInterface(identifierNode);        
        } else {
            visitInterface(identifierNode);                       
        }       
    }

    // Visits a fully declared interface
    private void visitInterface(AST identifierNode) {
        String interfaceName = identifierNode.toString();        
        Scope interfaceScope = new Scope(getScope(), interfaceName);        
        
        portType = definition.createPortType();
        portType.setQName(new QName(definition.getTargetNamespace(), interfaceScope.toString()));
        portType.setUndefined(false);
        definition.addPortType(portType);
        Binding binding = createBinding();
        
        AST specNode = identifierNode.getNextSibling();        
        if  (specNode.getType() == IDLTokenTypes.LCURLY) {
            specNode = specNode.getNextSibling();
        }
        
        AST exportNode = null;        
        if (specNode.getType() == IDLTokenTypes.RCURLY) {
            exportNode = specNode.getNextSibling();        
        } else if (specNode.getType() == IDLTokenTypes.COLON) {
            exportNode = visitInterfaceInheritanceSpec(specNode, binding);
            exportNode = exportNode.getNextSibling();
        } else {            
            exportNode = specNode;
        }
           
        while (exportNode != null  
            && exportNode.getType() != IDLTokenTypes.RCURLY) {
            
            if (TypeDclVisitor.accept(exportNode)) {
                TypeDclVisitor visitor = new TypeDclVisitor(interfaceScope,
                                                            wsdlVisitor);
                visitor.visit(exportNode);
            } else if (ConstVisitor.accept(exportNode)) {
                ConstVisitor visitor = new ConstVisitor(interfaceScope,
                                                        wsdlVisitor);
                visitor.visit(exportNode);
            } else if (ExceptionVisitor.accept(exportNode)) {
                ExceptionVisitor visitor = new ExceptionVisitor(interfaceScope,
                                                                wsdlVisitor);
                visitor.visit(exportNode);
            } else if (AttributeVisitor.accept(exportNode)) {
                AttributeVisitor attributeVisitor = new AttributeVisitor(interfaceScope,
                                                                         wsdlVisitor,
                                                                         portType,
                                                                         binding);
                attributeVisitor.visit(exportNode);                

            } else if (OperationVisitor.accept(interfaceScope, schemas, schema, 
                                               typeMap, wsdlVisitor.getDefinition(),
                                               exportNode, wsdlVisitor)) {
                OperationVisitor visitor = new OperationVisitor(interfaceScope,
                                                                wsdlVisitor,
                                                                portType,
                                                                binding);
                visitor.visit(exportNode);                     
            } else {
                throw new RuntimeException("[InterfaceVisitor] Invalid IDL: unknown element "
                                           + exportNode.toString());
            }
            
            exportNode = exportNode.getNextSibling();
        }

        // Once we've finished declaring the interface, we should make sure it has been removed 
        // from the list of scopedNames so that we indicate that is no longer simply forward
        // declared.
        Scope scopedName = new Scope(getScope(), identifierNode);
        scopedNames.remove(scopedName);
        
        if (wsdlVisitor.getDeferredActions() != null) {
            DeferredActionCollection deferredActions = wsdlVisitor.getDeferredActions();
            List list = deferredActions.getActionsList(scopedName);
            if (!list.isEmpty()) {
                if (ObjectReferenceVisitor.accept(getScope(), schema, 
                                                  wsdlVisitor.getDefinition(), identifierNode)) {
                    ObjectReferenceVisitor visitor = new ObjectReferenceVisitor(getScope(), wsdlVisitor);
                    visitor.visit(identifierNode);
                    XmlSchemaType stype = visitor.getSchemaType();
                    CorbaTypeImpl ctype = visitor.getCorbaType();
                    Iterator iterator = list.iterator();
                    
                    while (iterator.hasNext()) {
                        DeferredAction action = (DeferredAction)iterator.next();
                        action.doDeferredAction(stype, ctype);                       
                    }
                    iterator = list.iterator();                    
                    while (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();                       
                    }                      
                }    
            }            
        }   
    }
    
    public Binding createBinding() {
        String bname = portType.getQName().getLocalPart() + "CORBABinding";
        QName bqname = new QName(definition.getTargetNamespace(),
                                 bname);
        int count = 0;
        while (queryBinding(bqname)) {
            bname = bname + count;
            bqname = new QName(definition.getTargetNamespace(), bname);
        }
        Binding binding = definition.createBinding();
        binding.setPortType(portType);
        binding.setQName(bqname);

        try {
            BindingType bindingType = (BindingType)
                extReg.createExtension(Binding.class, CorbaConstants.NE_CORBA_BINDING);
            bindingType.setRepositoryID(CorbaConstants.REPO_STRING
                                        + portType.getQName().getLocalPart().replace('.', '/')
                                        + CorbaConstants.IDL_VERSION);
            binding.addExtensibilityElement(bindingType);
        } catch (WSDLException ex) {
            throw new RuntimeException(ex);
        }
        binding.setUndefined(false);
        definition.addBinding(binding);
        return binding;
    }

    private boolean queryBinding(QName bqname) {
        Map bindings = definition.getBindings();
        Iterator i = bindings.values().iterator();
        while (i.hasNext()) {
            Binding binding = (Binding)i.next();
            if (binding.getQName().getLocalPart().equals(bqname.getLocalPart())) {
                return true;
            }
        }
        return false;
    }
    
    private AST visitInterfaceInheritanceSpec(AST interfaceInheritanceSpecNode, Binding binding) {
        // <interface_inheritance_spec> ::= ":" <interface_name> { "," <interface_name> }*
        
        Scope inheritanceScope = null;
        AST interfaceNameNode = interfaceInheritanceSpecNode.getFirstChild();
        BindingType corbaBinding = findCorbaBinding(binding);
        while (interfaceNameNode != null) {            
            // TODO
            // add interface inheritance information to XmlSchema and CorbaTypeMap here  
            
            //check for porttypes in current & parent scopes
            PortType intf = null;
            if (ScopedNameVisitor.isFullyScopedName(interfaceNameNode)) {
                Scope interfaceScope = ScopedNameVisitor.getFullyScopedName(new Scope(), interfaceNameNode);
                intf = findPortType(interfaceScope.toString());
            }
            Scope currentScope = getScope();
            while (intf == null
                   && currentScope != currentScope.getParent()) {
                Scope interfaceScope;
                if (ScopedNameVisitor.isFullyScopedName(interfaceNameNode)) {
                    interfaceScope = ScopedNameVisitor.getFullyScopedName(currentScope, interfaceNameNode);
                } else {
                    interfaceScope = new Scope(currentScope, interfaceNameNode.toString());
                }
                intf = findPortType(interfaceScope.toString());
                currentScope = currentScope.getParent();
            }
            
            if (intf == null) {
                throw new RuntimeException("[InterfaceVisitor] Unknown Interface: "
                                           + interfaceNameNode.toString());
            }
            BindingType inheritedCorbaBinding = findCorbaBinding(findBinding(intf));
            corbaBinding.getBases().add(inheritedCorbaBinding.getRepositoryID());
            interfaceNameNode = interfaceNameNode.getNextSibling();
        }
        
        return interfaceInheritanceSpecNode.getNextSibling();
    }
    
    private void visitForwardDeclaredInterface(AST identifierNode) {
        String interfaceName = identifierNode.toString();        
        Scope interfaceScope = new Scope(getScope(), interfaceName);
        
        ScopeNameCollection scopedNames = wsdlVisitor.getScopedNames();
        if (scopedNames.getScope(interfaceScope) == null) {
            scopedNames.add(interfaceScope);
        }
        
    }


    private PortType findPortType(String intfName) {
        QName name = new QName(definition.getTargetNamespace(), intfName);
        return definition.getPortType(name);
    }

    private Binding findBinding(PortType intf) {
        Object[] bindings = definition.getBindings().values().toArray();   
        for (int i = 0; i < bindings.length; i++) {
            Binding binding = (Binding) bindings[i];
            if (binding.getPortType().getQName().equals(intf.getQName())) {
                return binding;
            }
        }
        throw new RuntimeException("[InterfaceVisitor] Couldn't find binding for porttype "
                                   + intf.getQName());
    }

    private BindingType findCorbaBinding(Binding binding) {
        java.util.List list = binding.getExtensibilityElements();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof BindingType) {
                return (BindingType) list.get(i);
            }
        }
        throw new RuntimeException("[InterfaceVisitor] Couldn't find Corba binding in Binding "
                                   + binding.getQName());
    }
}

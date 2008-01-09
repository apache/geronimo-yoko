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

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import antlr.collections.AST;

import org.apache.schemas.yoko.bindings.corba.Alias;
import org.apache.schemas.yoko.bindings.corba.Anonsequence;
import org.apache.schemas.yoko.bindings.corba.Sequence;
import org.apache.schemas.yoko.bindings.corba.TypeMappingType;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;
import org.apache.yoko.tools.common.XmlSchemaPrimitiveMap;
import org.apache.yoko.wsdl.CorbaConstants;
import org.apache.yoko.wsdl.CorbaTypeImpl;

public class ScopedNameVisitor extends VisitorBase {        
    private static boolean exceptionMode;
    private static XmlSchemaPrimitiveMap xmlSchemaPrimitiveMap = new XmlSchemaPrimitiveMap();          

    public ScopedNameVisitor(Scope scope,
                             WSDLASTVisitor wsdlVisitor) {
        super(scope, wsdlVisitor);             
    }
    
    public void setExceptionMode(boolean value) {
        exceptionMode = value;
    }
    
    public static boolean accept(Scope scope,
                                 XmlSchemaCollection schemas,
                                 XmlSchema schema,
                                 TypeMappingType typeMap,
                                 Definition def,
                                 AST node,                                 
                                 WSDLASTVisitor wsdlVisitor) {
        boolean result = false;
        if (PrimitiveTypesVisitor.accept(node)) {
            result = true; 
        } else if (isforwardDeclared(scope, schema, node, getScopedNames(), wsdlVisitor)) {
            result = true;
        } else if (ObjectReferenceVisitor.accept(scope, schema, def, node)) {
            result = true;
        } else if (findSchemaType(scope, schemas, schema, typeMap, node, null)) {
            result = true;
        }
        return result;
    }

    public void visit(AST node) {
        // <scoped_name> ::= <identifier>
        //                 | :: <identifier>
        //                 | <scoped_name> "::" <identifier>

        XmlSchemaType stype = null;
        CorbaTypeImpl ctype = null;        
        
        if (PrimitiveTypesVisitor.accept(node)) {
            // primitive type            
            PrimitiveTypesVisitor primitiveVisitor = new PrimitiveTypesVisitor(null, schemas);
            primitiveVisitor.visit(node);
            
            stype = primitiveVisitor.getSchemaType();
            ctype = primitiveVisitor.getCorbaType();            
        } else if (isforwardDeclared(getScope(), schema, node, scopedNames, wsdlVisitor)) {
            // forward declaration
            Scope scope = forwardDeclared(getScope(), schema, node, scopedNames, wsdlVisitor);
            setFullyQualifiedName(scope);
            // how will we create the corbatype ????
        } else if (ObjectReferenceVisitor.accept(getScope(), schema, wsdlVisitor.getDefinition(), node)) {
            ObjectReferenceVisitor objRefVisitor = new ObjectReferenceVisitor(getScope(),
                                                                              wsdlVisitor);
            objRefVisitor.visit(node);

            stype = objRefVisitor.getSchemaType();
            ctype = objRefVisitor.getCorbaType();            
        } else {
            VisitorTypeHolder holder = new VisitorTypeHolder();
            boolean found = findSchemaType(getScope(),
                                           schemas,
                                           schema,
                                           typeMap,
                                           node,
                                           holder);
            if (found) {
                ctype = holder.getCorbaType();
                stype = holder.getSchemaType();
            } else {
                Scope scopedName = new Scope(getScope(), node);
                QName qname = new QName(schema.getTargetNamespace(), scopedName.toString());
                throw new RuntimeException("[ScopedNameVisitor:  Corba type "
                                           + qname
                                           + " not found in typeMap]");
            }
        }       
        
        setSchemaType(stype);
        setCorbaType(ctype);        
        
    }

    private static CorbaTypeImpl getCorbaSchemaType(XmlSchema schema,
                                                    TypeMappingType typeMap,
                                                    XmlSchemaType stype) {       
        CorbaTypeImpl ctype = null;
        if (stype.getQName().equals(Constants.XSD_STRING)) {
            ctype = new CorbaTypeImpl();
            ctype.setName(CorbaConstants.NT_CORBA_STRING.getLocalPart());
            ctype.setQName(CorbaConstants.NT_CORBA_STRING);
            ctype.setType(Constants.XSD_STRING);
        } else {                    
            QName qname = null; 
            // Revisit: Exceptions are treated as a special case.
            // we should be able to do this in a better way.
            if (exceptionMode) {
                String name = null;
                if (stype.getName().endsWith("Type")) {
                    name = stype.getName().substring(0, stype.getName().length() - 4);
                } else {
                    name = stype.getName();
                }
                qname = new QName(schema.getTargetNamespace(), name);                     
            } else {
                qname = stype.getQName();      
            }
            ctype = findCorbaType(typeMap, qname);
        }
        return ctype;
    }
         
    protected static boolean isforwardDeclared(Scope scope, XmlSchema schema, AST node,
                                               ScopeNameCollection scopedNames, WSDLASTVisitor wsdlVisitor) {
        boolean isForward = false;
        Scope currentScope = scope;

        // Check for forward declaration from local scope outwards
        if ((node.getFirstChild() == null)
            || (node.getFirstChild() != null && node.getFirstChild().getType() != IDLTokenTypes.SCOPEOP)) {
            while (!isForward && currentScope != currentScope.getParent()) {
                Scope scopedName = null;
                if (isFullyScopedName(node)) {
                    scopedName = getFullyScopedName(currentScope, node);
                } else {
                    scopedName = new Scope(currentScope, node);
                }
                if (scopedNames.getScope(scopedName) != null) {
                    isForward = true;
                }
                currentScope = currentScope.getParent();
            }
        }
        // Check for forward declaration in global scope
        if (!isForward) {
            Scope scopedName = null;
            if (isFullyScopedName(node)) {
                scopedName = getFullyScopedName(new Scope(), node);
            } else {                
                scopedName = new Scope(new Scope(), node);
            }
                        
            if (scopedNames.getScope(scopedName) != null) {
                isForward = true;
            }
        }

        return isForward;
    }
     
    
    protected static Scope forwardDeclared(Scope scope, XmlSchema schema, AST node,
                                           ScopeNameCollection scopedNames, 
                                           WSDLASTVisitor wsdlVisitor) {
        //XmlSchemaType result = null;
        Scope result = null;
        Scope currentScope = scope;

        // Check for forward declaration from local scope outwards
        if ((node.getFirstChild() == null)
            || (node.getFirstChild() != null && node.getFirstChild().getType() != IDLTokenTypes.SCOPEOP)) {
            while (result == null && currentScope != currentScope.getParent()) {
                Scope scopedName = null;
                if (isFullyScopedName(node)) {
                    scopedName = getFullyScopedName(currentScope, node);
                } else {
                    scopedName = new Scope(currentScope, node);
                }

                if (scopedNames.getScope(scopedName) != null) {
                    if (ObjectReferenceVisitor.accept(scope, schema, wsdlVisitor.getDefinition(), node)) {
                        // checks if its a forward
                        Visitor visitor = new ObjectReferenceVisitor(scope, wsdlVisitor);
                        visitor.visit(node);                    
                    }
                    result = scopedName;
                }
                currentScope = currentScope.getParent();
            }
        }
        // Check for forward declaration in global scope
        if (result == null) {
            Scope scopedName = null;
            if (isFullyScopedName(node)) {
                scopedName = getFullyScopedName(new Scope(), node);
            } else {
                scopedName = new Scope(new Scope(), node);
            }
            if (scopedNames.getScope(scopedName) != null) { 
                if (ObjectReferenceVisitor.accept(scope, schema, wsdlVisitor.getDefinition(), node)) {
                    // checks if an object ref
                    Visitor visitor = new ObjectReferenceVisitor(scope, wsdlVisitor);
                    visitor.visit(node);
                }
                result = scopedName;
            }
        }

        return result;
    }
    
    
    protected static boolean findSchemaType(Scope scope,
                                            XmlSchemaCollection schemas,
                                            XmlSchema schema,
                                            TypeMappingType typeMap,
                                            AST node,
                                            VisitorTypeHolder holder) {
                                                
        boolean result = false;
        Scope currentScope = scope;
        
        // checks from innermost local scope outwards
        if ((node.getFirstChild() == null)
            || (node.getFirstChild() != null && node.getFirstChild().getType() != IDLTokenTypes.SCOPEOP)) {
            while (!result && currentScope != currentScope.getParent()) {
                // A name can be used in an unqualified form within a particular
                // scope;
                // it will be resolved by successvely n searching farther out in
                // enclosing scopes, while taking into consideration 
                // inheritance relationships among interfaces.
                Scope scopedName = null;
                if (isFullyScopedName(node)) {
                    scopedName = getFullyScopedName(currentScope, node);
                } else {
                    scopedName = new Scope(currentScope, node);
                }
                
                result = findNonSchemaType(schemas, schema, typeMap, scopedName.toString(), holder);
                if (!result) {
                    QName qname = null;

                    // Exceptions are treated as a special case as for the
                    // doc/literal style
                    // in the schema we will have an element and a complextype
                    // so the name
                    // and the typename will be different.
                    if (exceptionMode) {
                        qname = new QName(schema.getTargetNamespace(), scopedName.toString() + "Type");
                    } else {
                        qname = new QName(schema.getTargetNamespace(), scopedName.toString());
                    }
                    XmlSchemaType stype = schema.getTypeByName(qname);
                    if (stype == null) {
                        stype = schemas.getTypeByQName(qname);
                    }
                    if (stype != null) {
                        result = true;
                        if (holder != null) {
                            holder.setSchemaType(stype);
                            holder.setCorbaType(getCorbaSchemaType(schema, typeMap, stype));
                        }
                    }
                }
                currentScope = currentScope.getParent();
            }

        }
        if (!result) {
            // Global scope is our last chance to resolve the node
            Scope scopedName = scope;
            String name = node.toString();
            if (isFullyScopedName(node)) {
                scopedName = getFullyScopedName(new Scope(), node);
                name = scopedName.toString();
            }
            
            result = findNonSchemaType(schemas, schema, typeMap, name, holder);
            if (!result) {
                QName qname = new QName(schema.getTargetNamespace(), name);

                // Exceptions are treated as a special case as above
                if (exceptionMode) {
                    qname = new QName(schema.getTargetNamespace(), qname.getLocalPart() + "Type");
                }
                XmlSchemaType stype = schema.getTypeByName(qname);
                if (stype == null) {
                    stype = schemas.getTypeByQName(qname);
                }
                if (stype != null) {
                    result = true;
                    if (holder != null) {
                        holder.setSchemaType(stype);
                        holder.setCorbaType(getCorbaSchemaType(schema, typeMap, stype));
                    }
                }
            }
        }
        return result;
    }
    
    public static CorbaTypeImpl findCorbaType(TypeMappingType typeMap, QName schemaTypeName) {
        CorbaTypeImpl result = null;
        Iterator corbaTypes = typeMap.getStructOrExceptionOrUnion().iterator();
        while (corbaTypes.hasNext()) {
            CorbaTypeImpl type = (CorbaTypeImpl) corbaTypes.next();
            if (type.getQName().getLocalPart().equals(schemaTypeName.getLocalPart())) {
                result = type;
                break;
            }
        }
        return result;
    }       
    
    protected static boolean isFullyScopedName(AST node) {
        if (node.getType() == IDLTokenTypes.IDENT) {
            if (node.getFirstChild() != null) {
                if ((node.getFirstChild().getType() == IDLTokenTypes.SCOPEOP)
                    || (node.getFirstChild().getType() == IDLTokenTypes.IDENT)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected static Scope getFullyScopedName(Scope currentScope, AST node) {
        Scope scopedName = new Scope();
        if (!currentScope.toString().equals(node.getText())) {
            scopedName = new Scope(currentScope);
        }
        scopedName = new Scope(scopedName, node);
        AST scopeNode = node.getFirstChild();
        if (node.getFirstChild().getType() == IDLTokenTypes.IDENT) {
            scopedName = new Scope(scopedName, scopeNode);
        }                                
        while (scopeNode.getNextSibling() != null) {
            scopeNode = scopeNode.getNextSibling(); 
            scopedName = new Scope(scopedName, scopeNode);                            
        }   
        
        return scopedName;
    }

    protected static boolean findNonSchemaType(XmlSchemaCollection schemas,
                                               XmlSchema schema,
                                               TypeMappingType typeMap,
                                               String name,
                                               VisitorTypeHolder holder) {
        boolean result = false;
        QName qname = new QName(typeMap.getTargetNamespace(), name);
        CorbaTypeImpl corbaType = findCorbaType(typeMap, qname);
        if (corbaType != null) {
            if (corbaType instanceof Alias) {
                result = true;
                if (holder != null) {
                    holder.setCorbaType(corbaType);
                    Alias alias = (Alias) corbaType;
                    //loop through alias base types, till you get a non-alias corba type
                    CorbaTypeImpl type = findCorbaType(typeMap, alias.getBasetype());
                    while ((type != null) && (type instanceof Alias)) {
                        alias = (Alias) type;
                        type = findCorbaType(typeMap, alias.getBasetype());
                    }
                    QName tname;
                    if (type == null) {
                        //it must be a primitive type
                        tname = xmlSchemaPrimitiveMap.get(alias.getBasetype());
                    } else {
                        tname = type.getType();
                    }         
                    XmlSchemaType stype = schemas.getTypeByQName(tname);
                    if (stype == null) {
                        stype = schema.getTypeByName(tname);
                    }
                    holder.setSchemaType(stype);
                }
            } else if (((corbaType instanceof Sequence) || (corbaType instanceof Anonsequence))
                       && ((corbaType.getType().equals(Constants.XSD_BASE64))
                           || (corbaType.getType().equals(Constants.XSD_BASE64)))) {
                //special case of sequence of octets
                result = true;
                if (holder != null) {
                    holder.setCorbaType(corbaType);
                    holder.setSchemaType(schemas.getTypeByQName(corbaType.getType()));
                }
            }
        }
        return result;
    }
        
}

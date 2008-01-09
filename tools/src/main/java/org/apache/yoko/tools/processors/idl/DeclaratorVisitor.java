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

import javax.xml.namespace.QName;

import antlr.collections.AST;

import org.apache.schemas.yoko.bindings.corba.Alias;
import org.apache.schemas.yoko.bindings.corba.Fixed;
import org.apache.schemas.yoko.bindings.corba.Sequence;

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.apache.yoko.wsdl.CorbaTypeImpl;

public class DeclaratorVisitor extends VisitorBase {
    // <declarators> ::= <declarator> {"," <declarator> }*
    // <declarator> ::= <simple_declarator>
    //                | <complex_declarator>
    // <simple_declarator> ::= <identifier>
    // <complex_declarator> ::= <array_declarator>
    
    public DeclaratorVisitor(Scope scope,
                             WSDLASTVisitor wsdlASTVisitor,
                             XmlSchemaType schemaTypeRef,
                             CorbaTypeImpl corbaTypeRef,
                             Scope fQName) {
        super(scope, wsdlASTVisitor);
        setSchemaType(schemaTypeRef);
        setCorbaType(corbaTypeRef);
        setFullyQualifiedName(fQName);
    }
    
    public void visit(AST node) {
    
        if (ArrayVisitor.accept(node)) {
            ArrayVisitor arrayVisitor = new ArrayVisitor(getScope(),
                                                         wsdlVisitor,
                                                         getSchemaType(),
                                                         getCorbaType(),
                                                         node,
                                                         getFullyQualifiedName()); 
            arrayVisitor.visit(node);

        } else {
            // add schemaType
            if ((getSchemaType() != null)
                && (schemas.getTypeByQName(getSchemaType().getQName()) == null)
                && (schema.getTypeByName(getSchemaType().getQName()) == null)) {
                schema.getItems().add(getSchemaType());
                schema.addType(getSchemaType());
            }
            // add corbaType
            typeMap.getStructOrExceptionOrUnion().add(getCorbaType());
        }
        
        AST nextDecl = node.getNextSibling(); 
        while (nextDecl != null) {
            Scope newScope = new Scope(getScope().getParent(), nextDecl);

            if (ArrayVisitor.accept(nextDecl)) {
                ArrayVisitor arrayVisitor = new ArrayVisitor(newScope,
                                                             wsdlVisitor,
                                                             getSchemaType(),
                                                             getCorbaType(),
                                                             nextDecl,
                                                             getFullyQualifiedName()); 
                arrayVisitor.visit(nextDecl);

            } else {
                CorbaTypeImpl nextCorbaType = null;
                XmlSchemaType nextSchemaType = null;

                CorbaTypeImpl oldCorbaType = getCorbaType();

                QName newQname = new QName(getCorbaType().getQName().getNamespaceURI(), newScope.toString());

                if (oldCorbaType instanceof Alias) {
                    // Alias
                    //
                    Alias oldAlias = (Alias) oldCorbaType;
                    Alias alias = new Alias();

                    alias.setQName(newQname);
                    alias.setBasetype(oldAlias.getBasetype());
                    alias.setType(oldAlias.getType());
                    alias.setRepositoryID(newScope.toIDLRepositoryID());

                    nextCorbaType = alias;
                } else if (oldCorbaType instanceof Sequence) {
                    // Sequence
                    //

                    nextSchemaType = duplicateXmlSchemaComplexType(newScope);

                    Sequence oldSequence = (Sequence) oldCorbaType;
                    Sequence newSequence = new Sequence();

                    newSequence.setQName(newQname);
                    newSequence.setType(nextSchemaType.getQName());
                    newSequence.setElemtype(oldSequence.getElemtype());
                    newSequence.setElemname(oldSequence.getElemname());
                    newSequence.setBound(oldSequence.getBound());
                    newSequence.setRepositoryID(newScope.toIDLRepositoryID());

                    nextCorbaType = newSequence;
                } else if (oldCorbaType instanceof Fixed) {
                    // Fixed
                    //

                    nextSchemaType = duplicateXmlSchemaSimpleType(newScope);

                    Fixed oldFixed = (Fixed) getCorbaType();
                    Fixed newFixed = new Fixed();

                    newFixed.setQName(newQname);
                    newFixed.setDigits(oldFixed.getDigits());
                    newFixed.setScale(oldFixed.getScale());
                    newFixed.setType(oldFixed.getType());
                    newFixed.setRepositoryID(newScope.toIDLRepositoryID());

                    nextCorbaType = newFixed;
                } else {
                    System.err.println("[DeclaratorVisitor: Unexpected CORBA type error!]");
                    System.exit(1);
                }

                if (nextCorbaType != null) {
                    typeMap.getStructOrExceptionOrUnion().add(nextCorbaType);
                }
                if (nextSchemaType != null) {
                    schema.getItems().add(nextSchemaType);
                    schema.addType(nextSchemaType);                    
                }
            }
            
            nextDecl = nextDecl.getNextSibling();
        }

    }
    
    private XmlSchemaComplexType duplicateXmlSchemaComplexType(Scope newScope) {
        XmlSchemaComplexType oldSchemaType = (XmlSchemaComplexType) getSchemaType();
        XmlSchemaComplexType newSchemaType = new XmlSchemaComplexType(schema);

        newSchemaType.setName(newScope.toString());
        newSchemaType.setParticle(oldSchemaType.getParticle());

        return newSchemaType;
    }
    
    private XmlSchemaSimpleType duplicateXmlSchemaSimpleType(Scope newScope) {
        XmlSchemaSimpleType oldSimpleType = (XmlSchemaSimpleType) getSchemaType();
        XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(schema);
        simpleType.setContent(oldSimpleType.getContent());
        simpleType.setName(newScope.toString());
        return simpleType;
    }

}

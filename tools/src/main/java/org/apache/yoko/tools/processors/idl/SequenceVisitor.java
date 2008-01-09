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

//import org.apache.schemas.yoko.bindings.corba.Alias;
import org.apache.schemas.yoko.bindings.corba.Anonsequence;
import org.apache.schemas.yoko.bindings.corba.Sequence;

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;

import org.apache.yoko.tools.common.ReferenceConstants;
import org.apache.yoko.wsdl.CorbaTypeImpl;

public class SequenceVisitor extends VisitorBase {

    private static final String ELEMENT_NAME = "item";

    private AST identifierNode;
    
    public SequenceVisitor(Scope scope,
                           WSDLASTVisitor wsdlVisitor,
                           AST identifierNodeRef) {
        super(scope, wsdlVisitor);
        identifierNode = identifierNodeRef;
    }
    
    public static boolean accept(AST node) {
        if (node.getType() == IDLTokenTypes.LITERAL_sequence) {
            return true;
        }
        return false;
    }
    
    public void visit(AST seq) {
        // <sequence_type> ::= "sequence" "<" <simple_type_spec> "," <positive_int_const> ">"
        //                   | "sequence" "<" <simple_type_spec> ">"
        
        
        AST simpleTypeSpecNode = seq.getFirstChild();
        // REVISIT: TypesUtils.getPrimitiveCorbaTypeNameNode should be renamed
        // to something more suitable and should be made more general.
        AST boundNode = TypesUtils.getCorbaTypeNameNode(simpleTypeSpecNode); 

        
        SimpleTypeSpecVisitor visitor = new SimpleTypeSpecVisitor(new Scope(getScope(), identifierNode),
                                                                  wsdlVisitor,
                                                                  null);
        visitor.visit(simpleTypeSpecNode);
        
        XmlSchemaType stype = visitor.getSchemaType();
        CorbaTypeImpl ctype = visitor.getCorbaType();
        Scope fullyQualifiedName = visitor.getFullyQualifiedName();
        

        long bound = -1;
        if (boundNode != null) {
            bound = Long.parseLong(boundNode.toString());
        }

        Scope scopedName = null;
        if (identifierNode == null) {
            // anonymous type
            scopedName = TypesUtils.generateAnonymousScopedName(getScope(), schema);
        } else {
            scopedName = new Scope(getScope(), identifierNode);
        }
              
        XmlSchemaType schemaType = null;                
        
        // According to CORBA Binding for WSDL specification,
        // idl:sequence<octet> maps to xs:base64Binary by default.
        // 
        // wsdlVisitor.getSequenceOctetType() returns the XmlSchema type
        // that idl:sequence<octet> should map to, as specified by the 
        // -s command line option or the default type xsd:base64Binary.
        //
        if (stype != null) {
            if (!stype.getQName().equals(Constants.XSD_UNSIGNEDBYTE)) {
                schemaType = generateSchemaType(stype, scopedName, bound, fullyQualifiedName);
            } else {
                schemaType = wsdlVisitor.getSequenceOctetType();
            }
        } else {
            schemaType = generateSchemaType(stype, scopedName, bound, fullyQualifiedName);
        }
        
        CorbaTypeImpl corbaType = null;
        if (identifierNode == null) {
            corbaType = generateCorbaAnonsequence(ctype,
                                                  schemaType,
                                                  scopedName,
                                                  bound,
                                                  fullyQualifiedName);
        } else {
            corbaType = generateCorbaSequence(ctype,
                                              schemaType,
                                              scopedName,
                                              bound,
                                              fullyQualifiedName);
        }


        setSchemaType(schemaType); 
        setCorbaType(corbaType);
        setFullyQualifiedName(fullyQualifiedName);
    }

    private XmlSchemaType generateSchemaType(XmlSchemaType stype, Scope scopedName, 
                                             long bound, Scope fullyQualifiedName) {
        XmlSchemaComplexType ct = new XmlSchemaComplexType(schema);
        ct.setName(scopedName.toString());
        XmlSchemaSequence sequence = new XmlSchemaSequence();
        XmlSchemaElement el = new XmlSchemaElement();
        el.setName(ELEMENT_NAME);
        el.setMinOccurs(0);
        if (bound != -1) {
            el.setMaxOccurs(bound);
        } else {
            el.setMaxOccurs(Long.MAX_VALUE);
        }
        if (stype != null) {
            el.setSchemaTypeName(stype.getQName());
            if (stype.getQName().equals(ReferenceConstants.WSADDRESSING_TYPE)) {
                el.setNillable(true);
            }
        } else {
            SequenceDeferredAction elementAction = 
                new SequenceDeferredAction(el, fullyQualifiedName);
            wsdlVisitor.getDeferredActions().add(elementAction); 
        }
        sequence.getItems().add(el);
        ct.setParticle(sequence);
        return ct;
    }
    
    private CorbaTypeImpl generateCorbaSequence(CorbaTypeImpl ctype,
                                                XmlSchemaType schemaType,
                                                Scope scopedName,
                                                long bound,
                                                Scope fullyQualifiedName) {
        //create the corba sequence
        Sequence corbaSeq = new Sequence();
        if (bound == -1) {
            bound = 0;
        }                
        corbaSeq.setBound(bound);
        corbaSeq.setQName(new QName(typeMap.getTargetNamespace(), scopedName.toString()));
        corbaSeq.setType(schemaType.getQName());
        //REVISIT, if we add qualification then change the below.
        corbaSeq.setElemname(new QName("", ELEMENT_NAME));
        if (ctype != null) {
            corbaSeq.setElemtype(ctype.getQName());
        } else {
            SequenceDeferredAction seqAction = 
                new SequenceDeferredAction(corbaSeq, fullyQualifiedName);
            wsdlVisitor.getDeferredActions().add(seqAction);
        }
        corbaSeq.setRepositoryID(scopedName.toIDLRepositoryID());

        return corbaSeq;
    }

    private Anonsequence generateCorbaAnonsequence(CorbaTypeImpl ctype,
                                                   XmlSchemaType schemaType,
                                                   Scope scopedName,
                                                   long bound,
                                                   Scope fullyQualifiedName) {
        // create corba anonsequence
        Anonsequence result = new Anonsequence();
        if (bound == -1) {
            bound = 0;
        }                
        result.setBound(bound);
        result.setQName(new QName(typeMap.getTargetNamespace(), scopedName.toString()));
        //REVISIT, if we add qualification then change the below.
        result.setElemname(new QName("", ELEMENT_NAME));
        if (schemaType == null || ctype == null) {
            SequenceDeferredAction anonSeqAction = 
                new SequenceDeferredAction(result, fullyQualifiedName);
            wsdlVisitor.getDeferredActions().add(anonSeqAction);
        } else {
            result.setType(schemaType.getQName());
            result.setElemtype(ctype.getQName());        
        }

        // Need to create an action if the type was forward declared.
        if (schemaType != null) {
            if (schemas.getTypeByQName(schemaType.getQName()) == null) {
                schema.getItems().add(schemaType);
                schema.addType(schemaType);
            }
        } else {
            SequenceDeferredAction anonSeqAction = 
                new SequenceDeferredAction(schemas, schema, fullyQualifiedName);
            wsdlVisitor.getDeferredActions().add(anonSeqAction);
        }
        
        // add corbaType
        typeMap.getStructOrExceptionOrUnion().add(result);
        
        return result;
    }
    
}

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

import antlr.collections.AST;

import org.apache.schemas.yoko.bindings.corba.ModeType;
import org.apache.schemas.yoko.bindings.corba.OperationType;
import org.apache.schemas.yoko.bindings.corba.ParamType;

import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.apache.yoko.tools.common.ReferenceConstants;
import org.apache.yoko.wsdl.CorbaTypeImpl;

public class ParamDclVisitor extends VisitorBase {

    private XmlSchemaSequence inWrappingSequence; 
    private XmlSchemaSequence outWrappingSequence;
    private OperationType corbaOperation;
    
    public ParamDclVisitor(Scope scope,
                           WSDLASTVisitor wsdlVisitor,
                           XmlSchemaSequence inWrapSeq,
                           XmlSchemaSequence outWrapSeq,
                           OperationType corbaOp) {
        super(scope, wsdlVisitor);
        inWrappingSequence = inWrapSeq;
        outWrappingSequence = outWrapSeq;
        corbaOperation = corbaOp;
    }

    public static boolean accept(AST node) {
        boolean result = false;
        if (node != null) {
            int type = node.getType();
            result = type == IDLTokenTypes.LITERAL_in
                || type == IDLTokenTypes.LITERAL_out
                || type == IDLTokenTypes.LITERAL_inout;
        }
        return result;
    }
    
    public void visit(AST node) {
        // <param_dcl> ::= <param_attribute> <param_type_spec> <simple_declarator>
        // <param_attribute> ::= "in"
        //                     | "out"
        //                     | "inout"

        AST typeNode = node.getFirstChild();
        AST nameNode = TypesUtils.getCorbaTypeNameNode(typeNode);
        
        ParamTypeSpecVisitor visitor = new ParamTypeSpecVisitor(getScope(), 
                                                                wsdlVisitor);
        visitor.visit(typeNode);
        XmlSchemaType schemaType = visitor.getSchemaType();
        CorbaTypeImpl corbaType = visitor.getCorbaType();
        Scope fullyQualifiedName = visitor.getFullyQualifiedName();
        
        switch (node.getType()) {
        case IDLTokenTypes.LITERAL_in: 
            addElement(inWrappingSequence, schemaType, nameNode.toString(), fullyQualifiedName);
            addCorbaParam(corbaType, ModeType.IN, nameNode.toString(), fullyQualifiedName);
            break;
        case IDLTokenTypes.LITERAL_out:
            addElement(outWrappingSequence, schemaType, nameNode.toString(), fullyQualifiedName); 
            addCorbaParam(corbaType, ModeType.OUT, nameNode.toString(), fullyQualifiedName);
            break;
        case IDLTokenTypes.LITERAL_inout:
            addElement(inWrappingSequence, schemaType, nameNode.toString(), fullyQualifiedName);
            addElement(outWrappingSequence, schemaType, nameNode.toString(), fullyQualifiedName);
            addCorbaParam(corbaType, ModeType.INOUT, nameNode.toString(), fullyQualifiedName);
            break;
        default:
            throw new RuntimeException("[ParamDclVisitor: illegal IDL!]");
        }
        
        setSchemaType(schemaType);
        setCorbaType(corbaType);
    }

    private XmlSchemaElement addElement(XmlSchemaSequence schemaSequence,
                                        XmlSchemaType schemaType,
                                        String name,
                                        Scope fullyQualifiedName) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setName(name);
        if (schemaType == null) {
            ParamDeferredAction elementAction = 
                new ParamDeferredAction(element, fullyQualifiedName);
            wsdlVisitor.getDeferredActions().add(elementAction);
        } else {
            element.setSchemaTypeName(schemaType.getQName());
            if (schemaType.getQName().equals(ReferenceConstants.WSADDRESSING_TYPE)) {
                element.setNillable(true);
            }
        }
        schemaSequence.getItems().add(element);
        return element;
    }

    private void addCorbaParam(CorbaTypeImpl corbaType, ModeType mode, 
                               String partName, Scope fullyQualifiedName) {
        ParamType param = new ParamType();
        param.setName(partName);
        param.setMode(mode);
        if (corbaType ==  null) {            
            ParamDeferredAction paramAction = 
                new ParamDeferredAction(param, fullyQualifiedName);
            wsdlVisitor.getDeferredActions().add(paramAction);
        } else {
            param.setIdltype(corbaType.getQName());
        }
        corbaOperation.getParam().add(param);
    }

}

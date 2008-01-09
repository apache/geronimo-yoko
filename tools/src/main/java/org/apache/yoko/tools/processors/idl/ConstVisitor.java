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

import org.apache.schemas.yoko.bindings.corba.Const;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.apache.yoko.wsdl.CorbaTypeImpl;

public class ConstVisitor extends VisitorBase {

    public ConstVisitor(Scope scope,
                        WSDLASTVisitor wsdlVisitor) {
        super(scope, wsdlVisitor);
    }
    
    public static boolean accept(AST node) {
        if (node.getType() == IDLTokenTypes.LITERAL_const) {
            return true;
        }
        return false;
    }
    
    public void visit(AST constNode) {
        // <const_dcl> ::= "const" <const_type> <identifier> "=" <const_exp>
        // <const_type> ::= <integer_type>
        //                | <char_type>
        //                | <wide_char_type>
        //                | <boolean_type>
        //                | <floating_pt_type>
        //                | <string_type>
        //                | <wide_string_type>
        //                | <fixed_pt_const_type>
        //                | <scoped_name>
        //                | <octet_type>
        
        
        AST constTypeNode = constNode.getFirstChild();
        AST constNameNode = TypesUtils.getCorbaTypeNameNode(constTypeNode);
        AST constValueNode = constNameNode.getNextSibling();
        
        // build value string
        String constValue = constValueNode.toString();
        constValueNode = constValueNode.getFirstChild();
        while (constValueNode != null) {
            constValue = constValue + constValueNode.toString();
            constValueNode = constValueNode.getFirstChild();
        }
        
        QName constQName = new QName(typeMap.getTargetNamespace(),
                                     new Scope(getScope(), constNameNode).toString());

        Visitor visitor = null;
        if (PrimitiveTypesVisitor.accept(constTypeNode)) {           
            visitor = new PrimitiveTypesVisitor(getScope(), schemas);           
        } else if (StringVisitor.accept(constTypeNode)) {
            // string_type_spec
            // wstring_type_spec
            visitor = new StringVisitor(getScope(), wsdlVisitor, constTypeNode); 
        } else if (FixedPtConstVisitor.accept(constTypeNode)) {           
            visitor = new FixedPtConstVisitor(getScope(), schemas); 
        } else if (ScopedNameVisitor.accept(getScope(), schemas, schema, 
                                            typeMap, wsdlVisitor.getDefinition(),
                                            constTypeNode, wsdlVisitor)) {
            visitor = new ScopedNameVisitor(getScope(), wsdlVisitor);            
        }
        
        visitor.visit(constTypeNode);                
        XmlSchemaType constSchemaType = visitor.getSchemaType();
        CorbaTypeImpl constCorbaType = visitor.getCorbaType();        
        
        // corba:const        
        Const corbaConst = new Const();
        corbaConst.setQName(constQName);
        corbaConst.setValue(constValue);        
        corbaConst.setType(constSchemaType.getQName());
        corbaConst.setIdltype(constCorbaType.getQName());        
        
        typeMap.getStructOrExceptionOrUnion().add(corbaConst);
    }
}

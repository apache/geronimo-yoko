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

import org.apache.schemas.yoko.bindings.corba.ArgType;

import org.apache.ws.commons.schema.XmlSchemaObject;

public class TypesVisitor extends VisitorBase {
    
    static final int PRIMITIVE = 0;
    
    XmlSchemaObject currentType;

    ArgType currentParam;

    private AST identifierNode;

    // identifierNode null if anonymous type
    public TypesVisitor(Scope scope,
                        WSDLASTVisitor wsdlVisitor,
                        AST identifierNodeRef) {
        super(scope, wsdlVisitor);
        identifierNode = identifierNodeRef;
    }

    public void visit(AST node) {
        // <type_spec> ::= <simple_type_spec>
        //               | <constr_type_spec>

        Visitor visitor = null;
        
        
        if (ConstrTypeSpecVisitor.accept(node)) {
            // type_spec - constr_type_spec
            visitor = new ConstrTypeSpecVisitor(getScope(), wsdlVisitor, identifierNode);
        } else if (SimpleTypeSpecVisitor.accept(node)) {
            // type_spec - simple_type_spec
            visitor = new SimpleTypeSpecVisitor(getScope(), wsdlVisitor, identifierNode);
        } else if (visitor == null) {
            // REVISIT: !!!!!
            // This is ugly. It should be done in the SimpleTypeSpecVisitor.accept(node) method.
            // More precisely, that accept method should contained an ORed 
            // ScopedNameVisitor.accept(schemas, schema, node)
            // It is not done currently because that would require changing accept method signature 
            // to accept(schemas, schema, node).
            // Perhaps passing a pointer to DefinitionVisitor or some other class (to be designed)
            // would be a better solution.
            //
            // To work around that redesign and get things working now, I am assuming that if visitor
            // is null at this point, then it has to be a scoped_name.
            // REVISIT!!!
            visitor = new ScopedNameVisitor(getScope(), wsdlVisitor);
        }
        visitor.visit(node);

        setSchemaType(visitor.getSchemaType());
        setCorbaType(visitor.getCorbaType());
        setFullyQualifiedName(visitor.getFullyQualifiedName());
        
    }

}

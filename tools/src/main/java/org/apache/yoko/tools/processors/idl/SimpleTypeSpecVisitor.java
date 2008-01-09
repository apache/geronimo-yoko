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

public class SimpleTypeSpecVisitor extends VisitorBase {

    private AST identifierNode;
    
    public SimpleTypeSpecVisitor(Scope scope,
                                 WSDLASTVisitor wsdlVisitor,
                                 AST identifierNodeRef) {
        super(scope, wsdlVisitor);
        identifierNode = identifierNodeRef;
    }

    public static boolean accept(AST node) {
        boolean result = 
            PrimitiveTypesVisitor.accept(node)
            || TemplateTypeSpecVisitor.accept(node);
        return result;
    }
    
    public void visit(AST node) {
        // <simple_type_spec> ::= <base_type_spec>
        //                      | <template_type_spec>
        //                      | <scoped_name>

        
        Visitor visitor = null;
        
        
        if (PrimitiveTypesVisitor.accept(node)) {
            // simple_type_spec - base_type_spec
            visitor = new PrimitiveTypesVisitor(getScope(), schemas);
            
        } else if (TemplateTypeSpecVisitor.accept(node)) {
            // simple_type_spec - template_type_spec
            visitor = new TemplateTypeSpecVisitor(getScope(), wsdlVisitor, identifierNode);

        } else if (ScopedNameVisitor.accept(getScope(), schemas, schema, 
                                            typeMap, wsdlVisitor.getDefinition(),
                                            node, wsdlVisitor)) {

            // simple_type_spec - scoped_name
            visitor = new ScopedNameVisitor(getScope(), wsdlVisitor);
        
        }
        
        
        visitor.visit(node);

        setSchemaType(visitor.getSchemaType());
        setCorbaType(visitor.getCorbaType());
        setFullyQualifiedName(visitor.getFullyQualifiedName());
    }
}

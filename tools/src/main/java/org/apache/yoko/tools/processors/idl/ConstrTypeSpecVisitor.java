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

public class ConstrTypeSpecVisitor extends VisitorBase {

    private AST identifierNode;
    
    public ConstrTypeSpecVisitor(Scope scope,
                                 WSDLASTVisitor wsdlASTVisitor,
                                 AST identifierNodeRef) {
        super(scope, wsdlASTVisitor);
        identifierNode = identifierNodeRef;
    }
    
    public static boolean accept(AST node) {
        boolean result = 
            StructVisitor.accept(node)
            || UnionVisitor.accept(node)
            || EnumVisitor.accept(node);
        return result;
    }
    
    public void visit(AST node) {
        // <constr_type_spec> ::= <struct_type>
        //                      | <union_type>
        //                      | <enum_type>
        
        Visitor visitor = null;
        
        if (StructVisitor.accept(node)) {
            visitor = new StructVisitor(getScope(), wsdlVisitor);
        }

        if (UnionVisitor.accept(node)) {
            visitor = new UnionVisitor(getScope(), wsdlVisitor);
        }

        if (EnumVisitor.accept(node)) {
            visitor = new EnumVisitor(getScope(), wsdlVisitor);
        }

        visitor.visit(node);
        
        setSchemaType(visitor.getSchemaType());
        setCorbaType(visitor.getCorbaType());
    }
    
}

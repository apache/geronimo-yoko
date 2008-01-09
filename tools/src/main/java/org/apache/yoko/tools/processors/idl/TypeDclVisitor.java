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

public class TypeDclVisitor extends VisitorBase {
    
    public TypeDclVisitor(Scope scope,
                          WSDLASTVisitor wsdlVisitor) {
        super(scope, wsdlVisitor);
    }
    
    public static boolean accept(AST node) {
        boolean result =
            TypedefVisitor.accept(node)
            || StructVisitor.accept(node)
            || UnionVisitor.accept(node)
            || EnumVisitor.accept(node);
        return result;
    }
    
    public void visit(AST node) {
        // <type_dcl> ::= "typedef" <type_declarator>
        //              | <struct_type>
        //              | <union_type>
        //              | <enum_type>
        //              | "native" <simple_declarator>
        //              | <constr_forward_decl>

        Visitor visitor = null;
        
        if (TypedefVisitor.accept(node)) {
            // "typedef" <type_declarator>
            visitor = new TypedefVisitor(getScope(), wsdlVisitor);
        } else if (StructVisitor.accept(node)) {
            // <struct_type>
            visitor = new StructVisitor(getScope(), wsdlVisitor);
        } else if (UnionVisitor.accept(node)) {
            // <union_type>
            visitor = new UnionVisitor(getScope(), wsdlVisitor);
        } else if (EnumVisitor.accept(node)) {
            // <enum_type>
            visitor = new EnumVisitor(getScope(), wsdlVisitor);
        } else if (node.getType() == IDLTokenTypes.LITERAL_native) {
            // "native" <simple_declarator>
            //
            // native type not supported
            throw new RuntimeException("[TypeDclVisitor: native type not supported!]");
        }

        // TODO forward declaration <constr_forward_declaration>
        
        visitor.visit(node);
    }

}

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

public class TemplateTypeSpecVisitor extends VisitorBase {
    
    private AST identifierNode;
    
    public TemplateTypeSpecVisitor(Scope scope,
                                   WSDLASTVisitor wsdlVisitor,
                                   AST identifierNodeRef) {
        super(scope, wsdlVisitor);
        identifierNode = identifierNodeRef;
    }

    public static boolean accept(AST node) {
        boolean result = 
            SequenceVisitor.accept(node)
            || StringVisitor.accept(node)
            || FixedVisitor.accept(node);
        return result;
    }
    
    public void visit(AST node) {
        // <template_type_spec> ::= <sequence_type>
        //                        | <string_type>
        //                        | <wide_string_type>
        //                        | <fixed_pt_type>


        Visitor visitor = null;
        
        if (SequenceVisitor.accept(node)) {
            // <sequence_type>
            visitor = new SequenceVisitor(getScope(), wsdlVisitor, identifierNode);
        } else if (StringVisitor.accept(node)) {
            // <string_type>
            // <wstring_type>
            visitor = new StringVisitor(getScope(), wsdlVisitor, identifierNode);
        } else if (FixedVisitor.accept(node)) {
            // <fixed_pt_type>
            visitor = new FixedVisitor(getScope(), wsdlVisitor, identifierNode);
        }

        visitor.visit(node);

        setSchemaType(visitor.getSchemaType());
        setCorbaType(visitor.getCorbaType());
        setFullyQualifiedName(visitor.getFullyQualifiedName());
    }

}

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

import javax.wsdl.Definition;

import antlr.collections.AST;

public class ParamTypeSpecVisitor extends VisitorBase {

    private Definition wsdlDefinition;
    
    public ParamTypeSpecVisitor(Scope scope,
                                WSDLASTVisitor wsdlVisitor) {
        super(scope, wsdlVisitor);
        wsdlDefinition = wsdlVisitor.getDefinition();
    }

    public void visit(AST node) {
        // <param_type_spec> ::= <base_type_spec>
        //                     | <string_type>
        //                     | <wstring_type>
        //                     | <scoped_name>

        
        Visitor visitor = null;
        
        
        if (PrimitiveTypesVisitor.accept(node)) {
            // base_type_spec
            visitor = new PrimitiveTypesVisitor(getScope(), schemas);
            
        } else if (StringVisitor.accept(node)) {
            // string_type_spec
            // wstring_type_spec
            visitor = new StringVisitor(getScope(), wsdlVisitor, null);

        } else if (ScopedNameVisitor.accept(getScope(), schemas, schema, 
                                            typeMap, 
                                            wsdlVisitor.getDefinition(),
                                            node, wsdlVisitor)) {
            // scoped_name
            visitor = new ScopedNameVisitor(getScope(), wsdlVisitor);
               
        } else {
            throw new RuntimeException("[ParamTypeSpecVisitor] Invalid IDL: unknown element "
                                       + node.toString());
        }
        
        visitor.visit(node);

        setSchemaType(visitor.getSchemaType());
        setCorbaType(visitor.getCorbaType());
        setFullyQualifiedName(visitor.getFullyQualifiedName());
    }
}

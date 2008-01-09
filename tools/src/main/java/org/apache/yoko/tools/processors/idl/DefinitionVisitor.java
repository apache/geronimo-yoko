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

public class DefinitionVisitor extends VisitorBase {
    
    private Definition definition;

    public DefinitionVisitor(Scope scope,
                             WSDLASTVisitor wsdlVisitor) {
        super(scope, wsdlVisitor);
        definition = wsdlVisitor.getDefinition();
    }

    public void visit(AST node) {
        // <definition> ::= <type_dcl> ";"
        //                | <const_dcl> ";"
        //                | <except_dcl> ";"
        //                | <interface> ";"
        //                | <module> ";"
        //                | <value> ";"
        
        switch (node.getType()) {
        case IDLTokenTypes.LITERAL_custom:
        case IDLTokenTypes.LITERAL_valuetype: {
            System.out.println("Valuetypes not supported");
            System.exit(1);
            break;
        }
        case IDLTokenTypes.LITERAL_module: {
            ModuleVisitor moduleVisitor = new ModuleVisitor(getScope(),
                                                            wsdlVisitor);
            moduleVisitor.visit(node);
            break;
        }
        case IDLTokenTypes.LITERAL_interface: {
            PortTypeVisitor portTypeVisitor = new PortTypeVisitor(getScope(),
                                                                  wsdlVisitor);
            portTypeVisitor.visit(node);
            break;
        }
        case IDLTokenTypes.LITERAL_exception: {
            ExceptionVisitor exceptionVisitor = new ExceptionVisitor(getScope(),
                                                                     wsdlVisitor);
            exceptionVisitor.visit(node);
            break;
        }
        case IDLTokenTypes.LITERAL_const: {
            ConstVisitor constVisitor = new ConstVisitor(getScope(),
                                                         wsdlVisitor);
            constVisitor.visit(node);
            break;
        }
        default: {
            TypeDclVisitor typeDclVisitor = new TypeDclVisitor(getScope(),
                                                               wsdlVisitor);
            typeDclVisitor.visit(node);
        }
        
        }
    }
}

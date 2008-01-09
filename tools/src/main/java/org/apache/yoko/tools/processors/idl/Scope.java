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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import antlr.collections.AST;

import org.apache.yoko.wsdl.CorbaConstants;

public final class Scope {

    private static final String SEPARATOR = ".";
    private List<String> scope;
    private Scope parent;
    
    public Scope() {
        scope = new ArrayList<String>();
        parent = this;
    }
    
    public Scope(Scope containingScope) {
        scope = new ArrayList<String>(containingScope.scope);
        parent = containingScope;
    }
    
    public Scope(Scope containingScope, String str) {
        scope = new ArrayList<String>(containingScope.scope);
        scope.add(str);
        parent = containingScope;
    }

    // This is used for interface inheritance
    public Scope(Scope containingScope, Scope prefixScope, String str) {
        scope = new ArrayList<String>(containingScope.scope);
        scope.addAll(prefixScope.scope);
        scope.add(str);
        parent = containingScope;
    }
    
    public Scope(Scope containingScope, AST node) {
        scope = new ArrayList<String>(containingScope.scope);
        if (node != null) { 
            scope.add(node.toString());
        }
        parent = containingScope;
    }
    
    public String tail() {
        int size = scope.size();
        if (size > 0) {
            return scope.get(size - 1);
        } else {
            return "";
        }
    }
    
    public Scope getParent() {
        return parent;
    }
    
    private String toString(String separator) {
        StringBuffer result = new StringBuffer();
        Iterator<String> it = scope.iterator();
        while (it.hasNext()) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(separator);
            }
        }
        return result.toString();
    }
    
    public String toString() {
        return toString(SEPARATOR);
    }

    public String toIDLRepositoryID() {
        StringBuffer result = new StringBuffer();
        result.append(CorbaConstants.REPO_STRING);
        result.append(toString("/"));
        result.append(CorbaConstants.IDL_VERSION);
        return result.toString();
    }
    
}

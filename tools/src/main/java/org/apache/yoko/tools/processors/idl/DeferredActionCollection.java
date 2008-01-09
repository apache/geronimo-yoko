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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// Class that holds a fully qualified name as the key that represents
// a type that was forward declared. 
// Associated with each fully qualified name is a list of actions.
// Each action represents a task that is deferred until 
// the type is really declared. 
public final class DeferredActionCollection {
    
    Map deferredActions = new HashMap<String, List>();
    
    public void add(DeferredActionBase action) {
        Object obj = deferredActions.get(action.getFullyQualifiedName().toString());
        List list = null;
        if (obj == null) {
            // create a new list and add first action
            list = new ArrayList();
            list.add(action);
        } else {
            // add action to list of actions for that scope
            list = (ArrayList)obj;
            list.add(action);
        }
        deferredActions.put(action.getFullyQualifiedName().toString(), list);
    }
    
    public void remove(DeferredActionBase action) {
        deferredActions.remove(action.getFullyQualifiedName().toString());
    }
                   
    public int getSize() {
        return deferredActions.size();   
    }
    
    public List getActionsList(Scope scope) {        
    
        List list = new ArrayList();
        if (deferredActions.size() > 0) {
            for (Iterator iter = deferredActions.keySet().iterator(); iter.hasNext();) {
                //Scope key = (Scope)iter.next();
                String key = (String)iter.next();
                if (key.equals(scope.toString())) {
                    return list = (List)deferredActions.get(key);                    
                }
            }
        }           
        return list;
    }
}

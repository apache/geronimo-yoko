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

import org.apache.schemas.yoko.bindings.corba.Anonsequence;
import org.apache.schemas.yoko.bindings.corba.Sequence;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.yoko.tools.common.ReferenceConstants;
import org.apache.yoko.wsdl.CorbaTypeImpl;

public class SequenceDeferredAction extends DeferredActionBase {

    protected Anonsequence anonSequence;
    protected Sequence sequence;
    protected XmlSchemaElement element;
    protected XmlSchema schema;
    protected XmlSchemaCollection schemas;    
    
    public SequenceDeferredAction(Sequence sequenceType, Anonsequence anonSequenceType, 
                                  XmlSchemaElement elem, Scope scope) {                           
        super(scope);
        anonSequence = anonSequenceType;
        sequence = sequenceType;
        element = elem;        
    }
    
    public SequenceDeferredAction(Anonsequence anonSequenceType, Scope scope) {                           
        super(scope);
        anonSequence = anonSequenceType;         
    }
    
    public SequenceDeferredAction(Sequence sequenceType, Scope scope) {                           
        super(scope);
        sequence = sequenceType;         
    }
    
    public SequenceDeferredAction(XmlSchemaElement elem, Scope scope) {                           
        super(scope);
        element = elem;               
    }
    
    public SequenceDeferredAction(XmlSchemaCollection xmlSchemas,
                                  XmlSchema xmlSchema,
                                  Scope scope) {                           
        super(scope);
        schemas = xmlSchemas;
        schema = xmlSchema;                       
    }
    
    public void doDeferredAction(XmlSchemaType stype, CorbaTypeImpl ctype) {
        if (anonSequence != null) {
            anonSequence.setElemtype(ctype.getQName());
            // This is needed for recursive types
            anonSequence.setType(stype.getQName());
        }
        if (sequence != null) {
            sequence.setElemtype(ctype.getQName());
            // This is needed for recursive types
            sequence.setType(stype.getQName());
        }
        if (element != null) {
            element.setSchemaTypeName(stype.getQName());
            if (stype.getQName().equals(ReferenceConstants.WSADDRESSING_TYPE)) {
                element.setNillable(true);
            }
        }        
        if (schemas != null) {
            if (schemas.getTypeByQName(stype.getQName()) == null) {
                schema.getItems().add(stype);
                schema.addType(stype);
            }
        }
    }
        
}




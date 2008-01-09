/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.yoko.bindings.corba.types;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import org.apache.yoko.wsdl.W3CConstants;

public class CorbaOctetSequenceEventProducer implements CorbaTypeEventProducer {

    int state;
    final int[] states = {XMLStreamReader.START_ELEMENT,
                          XMLStreamReader.CHARACTERS,
                          XMLStreamReader.END_ELEMENT};    
    final CorbaSequenceHandler seqHandler;
    final QName name;
    final boolean isBase64Octets;

    public CorbaOctetSequenceEventProducer(CorbaObjectHandler h) {
        seqHandler = (CorbaSequenceHandler) h;
        name = seqHandler.getName();
        isBase64Octets = seqHandler.getType().getType().equals(W3CConstants.NT_SCHEMA_BASE64);
    }

    public String getLocalName() {        
        return seqHandler.getSimpleName();
    }

    public QName getName() {
        return name;
    }

    public String getText() {
        List<CorbaObjectHandler> elements = seqHandler.getElements();
        byte[] bytes = new byte[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            CorbaPrimitiveHandler handler = (CorbaPrimitiveHandler) elements.get(i);
            bytes[i] = ((Byte) handler.getValue()).byteValue();
        }
        String result;
        if (isBase64Octets) {
            result = new String(Base64.encodeBase64(bytes));
        } else {
            result = new String(Hex.encodeHex(bytes));
        }
        return result;
    }

    public int next() {
        return states[state++];
    }

    public boolean hasNext() {
        return state < states.length;
    }

    public List<Attribute> getAttributes() {
        return null;
    }

    public List<Namespace> getNamespaces() {
        return null;
    }
}

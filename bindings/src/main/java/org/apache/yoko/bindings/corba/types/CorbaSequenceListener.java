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

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import org.apache.cxf.service.model.ServiceInfo;
import org.apache.schemas.yoko.bindings.corba.Anonsequence;
import org.apache.schemas.yoko.bindings.corba.Sequence;

import org.apache.yoko.bindings.corba.CorbaBindingException;
import org.apache.yoko.bindings.corba.CorbaTypeMap;
import org.apache.yoko.wsdl.CorbaTypeImpl;
import org.apache.yoko.wsdl.W3CConstants;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;

public class CorbaSequenceListener extends AbstractCorbaTypeListener {

    private final CorbaSequenceHandler value;
    private final QName seqElementType;
    private final ORB orb;
    private final CorbaTypeMap typeMap;
    private final boolean isBase64Octets;
    private final boolean isHexBinOctets;
    private CorbaTypeListener currentTypeListener;
    private ServiceInfo serviceInfo;
    private int depth;

    public CorbaSequenceListener(CorbaObjectHandler handler,
                                 CorbaTypeMap map,
                                 ORB orbRef, 
                                 ServiceInfo sInfo) {
        super(handler);
        value = (CorbaSequenceHandler) handler;
        orb = orbRef;
        typeMap = map;
        serviceInfo = sInfo;
        CorbaTypeImpl seqType = handler.getType();
        QName elementName;
        if (seqType instanceof Anonsequence) {
            Anonsequence anonSeqType = (Anonsequence) seqType;
            seqElementType = anonSeqType.getElemtype();
            elementName = anonSeqType.getElemname();
        } else {
            Sequence type = (Sequence) seqType;
            seqElementType = type.getElemtype();
            elementName = type.getElemname();
        }
        isBase64Octets = seqType.getType().equals(W3CConstants.NT_SCHEMA_BASE64);
        if (!isBase64Octets) {
            isHexBinOctets = seqType.getType().equals(W3CConstants.NT_SCHEMA_HBIN);
        } else {
            isHexBinOctets = false;
        }
        CorbaObjectHandler template;
        if (isBase64Octets || isHexBinOctets) {
            QName valueQName = new QName("value");
            TypeCode valueTC = orb.get_primitive_tc(TCKind.from_int(TCKind._tk_octet));
            template = new CorbaPrimitiveHandler(valueQName,
                                                 seqElementType,
                                                 valueTC,
                                                 null);
        } else {
            template = CorbaHandlerUtils.initializeObjectHandler(orb,
                                                                 elementName,
                                                                 seqElementType,
                                                                 typeMap,
                                                                 serviceInfo);
        }
        value.setTemplateElement(template);
    }

    public void processStartElement(QName name) {
        depth++;
        if (currentTypeListener == null) {
            currentElement = name;
            currentTypeListener =
                CorbaHandlerUtils.getTypeListener(name,
                                                  seqElementType,
                                                  typeMap,
                                                  orb,
                                                  serviceInfo);
            value.addElement(currentTypeListener.getCorbaObject());
        } else {
            currentTypeListener.processStartElement(name);
        }
    }

    public void processEndElement(QName name) {
        if (currentTypeListener != null) {
            currentTypeListener.processEndElement(name);
            depth--;
            if (depth == 0 && currentElement.equals(name)) {
                currentTypeListener = null;
            }
        }
    }

    public void processCharacters(String text) {
        if (currentTypeListener == null) {
            // primitive sequence
            if (isBase64Octets || isHexBinOctets) {
                QName valueQName = new QName("value");
                TypeCode valueTC = orb.get_primitive_tc(TCKind.from_int(TCKind._tk_octet));
                byte[] bytes;
                try {
                    if (isHexBinOctets) {
                        bytes = Hex.decodeHex(text.toCharArray());
                    } else {
                        bytes = Base64.decodeBase64(text.getBytes());
                    }
                } catch (Exception ex) {
                    throw new CorbaBindingException(ex);
                }
                for (int i = 0; i < bytes.length; i++) {
                    CorbaPrimitiveHandler handler = new CorbaPrimitiveHandler(valueQName,
                                                                              seqElementType,
                                                                              valueTC,
                                                                              null);
                    handler.setValue(new Byte(bytes[i]));
                    value.addElement(handler);
                }
            } else {
                CorbaTypeListener primitiveListener = 
                    CorbaHandlerUtils.getTypeListener(value.getName(),
                                                      seqElementType,
                                                      typeMap,
                                                      orb,
                                                      serviceInfo);
                value.addElement(primitiveListener.getCorbaObject());
                primitiveListener.processCharacters(text);
            }      
        } else {
            currentTypeListener.processCharacters(text);
        }
    }

    public void processWriteAttribute(String prefix, String namespaceURI, String localName, String val) {
        if (currentTypeListener != null) {
            currentTypeListener.processWriteAttribute(prefix, namespaceURI, localName, val);
        }
    }

    public void processWriteNamespace(String prefix, String namespaceURI) {
        if (currentTypeListener != null) {
            currentTypeListener.processWriteNamespace(prefix, namespaceURI);
        }
    }
}

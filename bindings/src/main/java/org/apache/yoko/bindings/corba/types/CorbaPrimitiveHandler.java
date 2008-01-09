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

import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;

public class CorbaPrimitiveHandler extends CorbaObjectHandler {

    private Object value;
    
    public CorbaPrimitiveHandler(QName primName, QName primIdlType, TypeCode primTC, Object primType) {
        super(primName, primIdlType, primTC, primType);
    }
    
    public Object getValue() {
        return value;
    }
    
    
    public void setValue(Object obj) {
        value = obj;
    }

    public String getDataFromValue() {
        String data = "";

        switch (this.typeCode.kind().value()) {

        case TCKind._tk_boolean:
            data = ((Boolean)value).toString();
            break;
        case TCKind._tk_char:
            char charValue = ((Character)value).charValue();
            // value + (-128)
            data = Byte.toString((byte)(charValue + Byte.MIN_VALUE));
            break;
        case TCKind._tk_wchar:
            data = ((Character)value).toString();
            break;
        case TCKind._tk_octet:
            data = ((Byte)value).toString();
            break;
        case TCKind._tk_short:
            data = ((Short)value).toString();
            break;
        case TCKind._tk_ushort:
            data = ((Integer)value).toString();
            break;
        case TCKind._tk_long:
            data = ((Integer)value).toString();
            break;
        case TCKind._tk_longlong:
            data = ((Long)value).toString();
            break;
        case TCKind._tk_ulong:
        case TCKind._tk_ulonglong:
            data = ((java.math.BigInteger)value).toString();
            break;
        case TCKind._tk_float:
            data = ((Float)value).toString();
            break;
        case TCKind._tk_double:
            data = ((Double)value).toString();
            break;
        case TCKind._tk_string:
        case TCKind._tk_wstring:
            data = (String)value;
            break;
        default:
            // Default: assume that whatever stored the data will also know how to convert it into what 
            // it needs.
            data = value.toString();
        }
        return data;
    }
    
    public void setValueFromData(String data) {
        switch (typeCode.kind().value()) {
        case TCKind._tk_boolean:
            value = new Boolean(data);
            break;
        case TCKind._tk_char:
            // A char is mapped to a byte, we need it as a character
            Byte byteValue = new Byte(data);
            // value - (-128)
            value = new Character((char)(byteValue.byteValue() - Byte.MIN_VALUE));
            break;
        case TCKind._tk_wchar:
            // A wide char is mapped to a string, we need it as a character
            value = new Character(data.charAt(0));
            break;
        case TCKind._tk_octet:
            value = new Byte(data);
            break;
        case TCKind._tk_short:
            value = new Short(data);
            break;
        case TCKind._tk_ushort:
            value = new Integer(data);
            break;
        case TCKind._tk_long:
            value = new Integer(data);
            break;
        case TCKind._tk_longlong:
            value = new Long(data);
            break;
        case TCKind._tk_ulong:
        case TCKind._tk_ulonglong:
            value = new java.math.BigInteger(data);
            break;
        case TCKind._tk_float:
            value = new Float(data);
            break;
        case TCKind._tk_double:
            value = new Double(data);
            break;
        case TCKind._tk_string:
        case TCKind._tk_wstring:
            value = data;
            break;
        default:
            // Default: just store the data we were given.  We'll expect that whatever stored the data
            // will also know how to convert it into what it needs.
            value = data;
        }
    }
    
    public void clear() {
        value = null;
    }
}

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.yoko.orb.OB;

//
// The data contained in an object key
//
final public class ObjectKeyData {
    public String serverId; // The Server Id

    public String[] poaId; // The POA to which this key refers

    public byte[] oid; // The object-id to which this key refers

    public boolean persistent; // Is the POA that created this key persistent?

    public int createTime; // If transient, what time was the POA created?

    public ObjectKeyData() {
    }

    public ObjectKeyData(String _serverId, String[] _poaId, byte[] _oid,
            boolean _persistent, int _createTime) {
        serverId = _serverId;
        poaId = _poaId;
        oid = _oid;
        persistent = _persistent;
        createTime = _createTime;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer(); 
        buf.append(serverId); 
        buf.append(':'); 
        if (poaId != null) {
            for (int i = 0; i < poaId.length; i++) {
                buf.append('/'); 
                buf.append(poaId[i]); 
            }
        }
        buf.append(':'); 
        if (oid != null) {
            buf.append(IORUtil.format_octets(oid)); 
        }
        return buf.toString(); 
    }
}

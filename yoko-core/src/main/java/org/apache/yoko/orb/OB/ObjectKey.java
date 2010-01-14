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

//
// OB 3.x object keys:
// Transient: 0 + data
// Persistent: non-zero + data
//
// OB 4.x object key:
//
// The object key is: 0xabacab
// ['1'=transient|'0'=persistent]|[time\0]`poa \0 poa \0 poa \0\0 id'.
// NOTE: we then know that we've reached the id by the presence of the
// two \0\0.  The time field we add for the POA create time (if
// transient) is current seconds. This is a tradeoff - it's most
// likely to be good enough.
//

package org.apache.yoko.orb.OB;
 
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OB.IORUtil;

final public class ObjectKey {
    static final Logger logger = Logger.getLogger(ObjectKey.class.getName());
    
    public static byte[] CreateObjectKey(ObjectKeyData id) {
        byte[] key;

        //
        // Count the number of bytes for the poas.
        //
        int len = id.serverId.length() + 1;
        for (int i = 0; i < id.poaId.length; i++) {
            len += id.poaId[i].length() + 1;
        }

        //
        // Add one for the '\0'
        //
        len += 1;

        //
        // Add 4 for the magic number
        //
        len += 3;

        //
        // Add one for persistent bit
        //
        len += 1;
        String time = null;

        //
        // If the POA is transient then add the time the poa was created
        //
        if (!id.persistent) {
            time = "" + id.createTime;
            len += time.length() + 1;
        }

        key = new byte[id.oid.length + len];
        int data = 0;

        //
        // Add the magic number
        //
        key[data++] = (byte) 0xab;
        key[data++] = (byte) 0xac;
        key[data++] = (byte) 0xab;

        //
        // Add the persistent/transient information
        //
        if (id.persistent) {
            key[data++] = (byte) '0';
        } else {
            key[data++] = (byte) '1';
            int n = time.length();
            for (int i = 0; i < n; i++) {
                key[data++] = (byte) time.charAt(i);
            }
            data++;
        }

        //
        // Copy in the data for the poas
        //
        int n = id.serverId.length();
        for (int j = 0; j < n; j++) {
            key[data++] = (byte) id.serverId.charAt(j);
        }
        data++;

        for (int i = 0; i < id.poaId.length; i++) {
            n = id.poaId[i].length();
            for (int j = 0; j < n; j++) {
                key[data++] = (byte) id.poaId[i].charAt(j);
            }
            data++;
        }

        //
        // Add additional '\0'
        //
        data++;

        System.arraycopy(id.oid, 0, key, data, id.oid.length);

        
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Created object key\n" + IORUtil.dump_octets(key)); 
        }
        return key;
    }

    //
    // Parse an object key, filling in the fields of the ObjectKeyData.
    // Return false if the object-key cannot be parsed, true otherwise.
    //
    public static boolean ParseObjectKey(byte[] key, ObjectKeyData keyData) {
        //
        // Parse out the POA's. This should be a sequence of strings
        // terminated by two null values.
        //
        int data = 0;
        int end = key.length;
        
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Parsing object key\n" + IORUtil.dump_octets(key)); 
        }

        //
        // First try to figure out whether the object-key is OB 4.x
        // format. Must be at least 4 bytes - magic + 1|0.
        //
        // OB 4.x magic number: abacab
        //
        if (key.length > 4 && key[0] == (byte) 0xab && key[1] == (byte) 0xac
                && key[2] == (byte) 0xab) {
            data += 3;
            if (key[data] == '0') // persistent
            {
                logger.fine("Parsing persistent object key"); 
                keyData.persistent = true;
                keyData.createTime = 0;
                ++data;
            } else {
                if (key[data] != '1') {
                    logger.fine("Characters '1' expected at position " + data); 
                    return false;
                }
                keyData.persistent = false;
                ++data;
                //
                // Remember the start of the time stamp
                //
                int start = data;
                while (data < end && key[data] != '\0') {
                    data++;
                }
                if (data >= end) {
                    logger.fine("Missing '\0' in key data"); 
                    return false;
                }

                String t = new String(key, start, data - start);
                try {
                    keyData.createTime = Integer.valueOf(t).intValue();
                } catch (NumberFormatException ex) {
                    logger.log(Level.FINE, "Invalid timestamp in key data", ex); 
                    return false;
                }
                //
                // skip the null byte
                //
                ++data;
            }

            boolean first = true;
            java.util.Vector poaId = new java.util.Vector();
            while (data < end) {
                //
                // Remember the start of the POA name
                //
                int start = data;
                while (data < end && key[data] != '\0') {
                    data++;
                }

                //
                // Ensure that we haven't gone too far...
                //
                if (data >= end) {
                    logger.fine("Missing '\0' in key data"); 
                    return false;
                }

                //
                // Append this to the sequence of POA's.
                //
                if (first) {
                    keyData.serverId = new String(key, start, data - start);
                    logger.fine("Parsed serverId=" + keyData.serverId); 
                    first = false;
                } else {
                    String element = new String(key, start, data - start); 
                    logger.fine("Parsed POA name=" + element); 
                    poaId.addElement(element);
                }

                //
                // Skip this byte, check to see if we have another '\0'
                //
                if (key[++data] == '\0') {
                    //
                    // Skip this byte
                    //
                    ++data;
                    break;
                }
            }
            keyData.poaId = new String[poaId.size()];
            poaId.copyInto(keyData.poaId);

            //
            // Verify that we haven't gone too far.
            //
            if (data >= end) {
                logger.fine("Missing object id in key data"); 
                return false;
            }

            //
            // Remaining portion is the ObjectId.
            //
            int len = end - data;
            keyData.oid = new byte[len];
            System.arraycopy(key, data, keyData.oid, 0, len);

            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Parsed object id is\n" + IORUtil.dump_octets(keyData.oid)); 
            }

            return true;
        } else {
            logger.fine("Invalid magic number in object key"); 
            return false;
        }
    }
}

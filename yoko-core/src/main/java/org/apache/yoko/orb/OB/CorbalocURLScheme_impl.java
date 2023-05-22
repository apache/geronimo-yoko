/*
 * Copyright 2021 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OB;

import org.apache.yoko.util.MinorCodes;

public class CorbalocURLScheme_impl extends org.omg.CORBA.LocalObject implements
        CorbalocURLScheme {
    private ORBInstance orbInstance_;

    private java.util.Hashtable protocols_ = new java.util.Hashtable();

    // ------------------------------------------------------------------
    // CorbalocURLScheme_impl private member implementations
    // ------------------------------------------------------------------

    private static byte[] stringToKey(String keyStr) {
        byte[] result = new byte[keyStr.length()];

        for (int i = 0; i < result.length; i++) {
            char ch = keyStr.charAt(i);
            if (ch > 255) {
                throw new org.omg.CORBA.BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorBadSchemeSpecificPart)
                                + ": invalid character in key, char value = 0x" + Integer.toHexString(ch),
                        MinorCodes.MinorBadSchemeSpecificPart,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
            result[i] = (byte) ch;
        }

        return result;
    }

    private org.omg.CORBA.Object parse_addresses(String str, int startIdx,
            int endIdx, String keyStr) {
        //
        // Check for rir:
        //
        if (str.substring(startIdx, startIdx + 4).equals("rir:")) {
            int comma = str.indexOf(',', startIdx);
            if (comma != -1 && comma <= endIdx) {
                throw new org.omg.CORBA.BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorBadSchemeSpecificPart)
                                + ": rir cannot be used with other protocols",
                        MinorCodes.MinorBadSchemeSpecificPart,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            if (startIdx + 3 != endIdx) {
                throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                        .describeBadParam(MinorCodes.MinorBadAddress)
                        + ": rir does not allow an address",
                        MinorCodes.MinorBadAddress,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            try {
                InitialServiceManager initialServiceManager = orbInstance_.getInitialServiceManager();
                return initialServiceManager.resolveInitialReferences(keyStr);
            } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
                throw new org.omg.CORBA.BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorBadSchemeSpecificPart)
                                + ": invalid initial reference token \"" + keyStr + "\"",
                        MinorCodes.MinorBadSchemeSpecificPart,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
        }

        //
        // Unescape stringified object key and convert to octets
        //
        byte[] key = stringToKey(URLUtil.unescapeURL(keyStr));

        // 
        // Convert addresses (separated by ',') into IOR profiles
        // 
        java.util.Vector profiles = new java.util.Vector();
        int pos = startIdx;
        while (pos <= endIdx) {
            //
            // Get the protocol identifier - we'll assume that protocols are
            // terminated by a ':'
            //
            String protocol;
            int colon = str.indexOf(':', pos);
            if (colon == -1) {
                throw new org.omg.CORBA.BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorBadSchemeSpecificPart)
                                + ": no protocol",
                        MinorCodes.MinorBadSchemeSpecificPart,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
            else if (colon > endIdx) {
                break;
            }
            if (colon == pos) {// ":" is shorthand for "iiop:"
                protocol = "iiop";
            }
            else {
                protocol = str.substring(pos, colon).toLowerCase();
            }
            pos = colon;

            //
            // Check for rir (again)
            //
            if (protocol.equals("rir")) {
                throw new org.omg.CORBA.BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorBadSchemeSpecificPart)
                                + ": rir cannot be used with other protocols",
                        MinorCodes.MinorBadSchemeSpecificPart,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            //
            // Get the protocol address
            //
            String addr;
            if (pos == endIdx) {
                addr = "";
                pos++;
            } else {
                int addrStart = pos + 1; // skip ':'
                int addrEnd;
                int comma = str.indexOf(',', addrStart);
                if (comma == -1 || comma > endIdx) {
                    addrEnd = endIdx;
                    pos = endIdx + 1;
                } else {
                    addrEnd = comma - 1;
                    pos = comma + 1;
                }
                addr = str.substring(addrStart, addrEnd + 1);
            }

            //
            // Find the protocol object
            //
            CorbalocProtocol p = find_protocol(protocol);
            if (p != null) {
                org.omg.IOP.TaggedProfile profile = p.parse_address(addr, key);
                profiles.addElement(profile);
            }
        }

        if (profiles.size() == 0) {
            throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorBadSchemeSpecificPart)
                    + ": no valid protocol addresses",
                    MinorCodes.MinorBadSchemeSpecificPart,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        org.omg.IOP.IOR ior = new org.omg.IOP.IOR();
        ior.type_id = "";
        ior.profiles = new org.omg.IOP.TaggedProfile[profiles.size()];
        profiles.copyInto(ior.profiles);

        ObjectFactory objectFactory = orbInstance_.getObjectFactory();
        return objectFactory.createObject(ior);
    }

    // ------------------------------------------------------------------
    // CorbalocURLScheme_impl constructor
    // ------------------------------------------------------------------

    public CorbalocURLScheme_impl(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String name() {
        return "corbaloc";
    }

    public org.omg.CORBA.Object parse_url(String url) {
        //
        // Get the object key
        //
        int slash = url.indexOf('/');

        //
        // Although an object key is optional according to the specification,
        // we consider this to be an invalid URL
        //
        if (slash == -1) {
            throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorOther)
                    + ": no key specified", MinorCodes.MinorOther,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        int addrStart = 9; // skip "corbaloc:"
        int addrEnd = slash - 1;
        if (addrStart == slash) {
            throw new org.omg.CORBA.BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorBadAddress)
                    + ": no protocol address", MinorCodes.MinorBadAddress,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        String key = url.substring(slash + 1);
        return parse_addresses(url, addrStart, addrEnd, key);
    }

    public void destroy() {
        java.util.Enumeration e = protocols_.elements();
        while (e.hasMoreElements()) {
            CorbalocProtocol protocol = (CorbalocProtocol) e.nextElement();
            protocol.destroy();
        }
        protocols_.clear();
        orbInstance_ = null;
    }

    public void add_protocol(CorbalocProtocol protocol)
            throws org.apache.yoko.orb.OB.CorbalocURLSchemePackage.ProtocolAlreadyExists {
        String name = protocol.name();
        if (protocols_.containsKey(name)) {
            throw new org.apache.yoko.orb.OB.CorbalocURLSchemePackage.ProtocolAlreadyExists();
        }
        protocols_.put(name, protocol);
    }

    public CorbalocProtocol find_protocol(String name) {
        return (CorbalocProtocol) protocols_.get(name);
    }
}

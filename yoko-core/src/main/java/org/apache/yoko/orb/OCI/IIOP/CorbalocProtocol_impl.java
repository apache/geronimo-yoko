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

package org.apache.yoko.orb.OCI.IIOP;

public class CorbalocProtocol_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OB.CorbalocProtocol {
    public String name() {
        return "iiop";
    }

    public org.omg.IOP.TaggedProfile parse_address(String addr, byte[] key) {
        byte major, minor;

        //
        // Do we have an iiop version 'X.Y@' portion? (default is 1.0)
        //
        int start = 0;
        int at = addr.indexOf('@');
        if (at == -1) {
            major = 1;
            minor = 0;
        } else {
            int pos = 0;
            int dot = 0;
            boolean seenDot = false;
            boolean ok = true;
            while (pos < at && ok) {
                char ch = addr.charAt(pos);
                if (ch == '.') {
                    if (seenDot || pos == 0 || pos == at - 1)
                        ok = false;
                    seenDot = true;
                    dot = pos;
                } else if (!Character.isDigit(ch))
                    ok = false;
                pos++;
            }
            if (!ok || !seenDot)
                throw new org.omg.CORBA.BAD_PARAM(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress)
                                + ": iiop version must be of the form `X.Y'",
                        org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            int nMajor = 0, nMinor = 0;
            try {
                nMajor = Integer.parseInt(addr.substring(0, dot));
                nMinor = Integer.parseInt(addr.substring(dot + 1, at));
            } catch (NumberFormatException ex) {
            }
            if (nMajor != 1 || nMinor > 255)
                throw new org.omg.CORBA.BAD_PARAM(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress)
                                + ": iiop version is invalid or unsupported",
                        org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            // 
            // Can only provide up to an iiop 1.2 profile. A server should
            // be able to communicate with a client with a lesser minor
            // version number.
            // 
            if (nMinor > 2)
                nMinor = 2;

            major = (byte) nMajor;
            minor = (byte) nMinor;

            //
            // Skip the version
            //
            start = at + 1;
        }

        //
        // Empty hostname is illegal (as is port ':YYYY' by itself)
        //
        if (start == addr.length() || addr.charAt(start) == ':')
            throw new org.omg.CORBA.BAD_PARAM(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress)
                            + ": iiop hostname must be specified",
                    org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        //
        // Hostname is terminated by ':port', or by end of string
        //
        String host;
        int colon = addr.indexOf(':', start);
        if (colon == -1)
            host = addr.substring(start);
        else
            host = addr.substring(start, colon);

        //
        // Valid range for port is 1 - 65535
        //
        int port = 2809; // default port
        if (colon != -1 && colon < addr.length()) {
            String str = addr.substring(colon + 1);
            try {
                port = Integer.parseInt(str);
            } catch (NumberFormatException ex) {
                throw new org.omg.CORBA.BAD_PARAM(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress)
                                + ": iiop port is invalid",
                        org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
            if (port < 1 || port > 65535)
                throw new org.omg.CORBA.BAD_PARAM(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress)
                                + ": iiop port must be between 1 and 65535",
                        org.apache.yoko.orb.OB.MinorCodes.MinorBadAddress,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        //
        // Create profile
        //
        org.omg.IOP.TaggedProfile profile = new org.omg.IOP.TaggedProfile();
        profile.tag = org.omg.IOP.TAG_INTERNET_IOP.value;

        if (major == (byte) 1 && minor == (byte) 0) {
            org.omg.IIOP.ProfileBody_1_0 body = new org.omg.IIOP.ProfileBody_1_0();
            body.iiop_version = new org.omg.IIOP.Version(major, minor);
            body.host = host;
            if (port >= 0x8000)
                body.port = (short) (port - 0xffff - 1);
            else
                body.port = (short) port;
            body.object_key = key;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.IIOP.ProfileBody_1_0Helper.write(out, body);
            profile.profile_data = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, profile.profile_data, 0, buf
                    .length());
        } else {
            // 
            // IIOP version is 1.1 or 1.2
            //
            org.omg.IIOP.ProfileBody_1_1 body = new org.omg.IIOP.ProfileBody_1_1();
            body.iiop_version = new org.omg.IIOP.Version(major, minor);
            body.host = host;
            if (port >= 0x8000)
                body.port = (short) (port - 0xffff - 1);
            else
                body.port = (short) port;
            body.object_key = key;
            body.components = new org.omg.IOP.TaggedComponent[0];

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.IIOP.ProfileBody_1_1Helper.write(out, body);
            profile.profile_data = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, profile.profile_data, 0, buf
                    .length());
        }

        return profile;
    }

    public void destroy() {
    }
}

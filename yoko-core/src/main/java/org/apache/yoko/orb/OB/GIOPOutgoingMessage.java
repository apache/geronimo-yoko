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

final public class GIOPOutgoingMessage {
    private static int maxMessageSize_ = 0; // TODO: pick a default

    private ORBInstance orbInstance_;

    private org.apache.yoko.orb.CORBA.OutputStream out_;

    private org.apache.yoko.orb.OCI.ProfileInfo profileInfo_;

    // ----------------------------------------------------------------------
    // GIOPOutgoingMessage private and protected member implementations
    // ----------------------------------------------------------------------

    private void writeServiceContextList(org.omg.IOP.ServiceContext[] scl) {
        int len = scl.length;
        out_.write_ulong(len);
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                org.omg.IOP.ServiceContext sc = scl[i];
                out_.write_ulong(sc.context_id);
                int n = sc.context_data.length;
                out_.write_ulong(n);
                out_.write_octet_array(sc.context_data, 0, n);
            }
        }
    }

    private void writeTargetAddress(org.omg.GIOP.TargetAddress target) {
        short disc = target.discriminator();
        out_.write_short(disc);
        switch (disc) {
        case 0: // GIOP::KeyAddr
        {
            byte[] seq = target.object_key();
            int len = seq.length;
            out_.write_ulong(len);
            if (len > 0)
                out_.write_octet_array(seq, 0, len);
            break;
        }

        case 1: // GIOP::ProfileAddr
        {
            org.omg.IOP.TaggedProfile profile = target.profile();
            org.omg.IOP.TaggedProfileHelper.write(out_, profile);
            break;
        }

        case 2: // GIOP::ReferenceAddr
        {
            org.omg.GIOP.IORAddressingInfo info = target.ior();
            org.omg.GIOP.IORAddressingInfoHelper.write(out_, info);
            break;
        }

        default:
            Assert._OB_assert(false);
        }
    }

    // ----------------------------------------------------------------------
    // GIOPOutgoingMessage package member implementations
    // ----------------------------------------------------------------------

    GIOPOutgoingMessage(ORBInstance orbInstance,
            org.apache.yoko.orb.CORBA.OutputStream out,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo) {
        orbInstance_ = orbInstance;
        out_ = out;
        profileInfo_ = profileInfo;
    }

    org.apache.yoko.orb.OCI.ProfileInfo profileInfo() {
        return profileInfo_;
    }

    void writeMessageHeader(org.omg.GIOP.MsgType_1_1 type, boolean fragment,
            int size) {
        Assert._OB_assert(type.value() >= 0
                && type.value() <= org.omg.GIOP.MsgType_1_1._Fragment);
        Assert
                ._OB_assert(!(profileInfo_.major == (byte) 1
                        && profileInfo_.minor == (byte) 0 && (type.value() > org.omg.GIOP.MsgType_1_1._MessageError || fragment)));

        if (maxMessageSize_ > 0 && size > maxMessageSize_) {
            String msg = "outgoing message size (" + size
                    + ") exceeds maximum (" + maxMessageSize_ + ")";
            orbInstance_.getLogger().warning(msg);

            throw new org.omg.CORBA.IMP_LIMIT(org.apache.yoko.orb.OB.MinorCodes
                    .describeImpLimit(org.apache.yoko.orb.OB.MinorCodes.MinorMessageSizeLimit),
                    org.apache.yoko.orb.OB.MinorCodes.MinorMessageSizeLimit,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        byte flags = 0;
        // JVM is always big endian. jlint complains here, so skip this code
        // boolean endian = false; // false means big endian
        // if(endian)
        // flags |= 0x01;
        if (fragment)
            flags |= 0x02;

        final byte[] giop = { (byte) 'G', (byte) 'I', (byte) 'O', (byte) 'P' };

        out_.write_octet_array(giop, 0, 4); // magic
        out_.write_octet(profileInfo_.major); // GIOP_version.major
        out_.write_octet(profileInfo_.minor); // GIOP_version.minor
        out_.write_octet(flags); // flags
        out_.write_octet((byte) type.value()); // message_type
        out_.write_ulong(size); // message_size
    }

    void writeRequestHeader(int id, String op, boolean response,
            org.omg.IOP.ServiceContext[] scl) {
        switch (profileInfo_.minor) {
        case 0:
        case 1: {
            writeServiceContextList(scl); // service_context
            out_.write_ulong(id); // request_id
            out_.write_boolean(response); // response_expected

            //
            // reserved
            //
            final byte[] reserved = { 0, 0, 0 };
            out_.write_octet_array(reserved, 0, 3);

            //
            // object_key
            //
            int keyLen = profileInfo_.key.length;
            out_.write_ulong(keyLen);
            out_.write_octet_array(profileInfo_.key, 0, keyLen);

            //
            // operation (we use octets to avoid codeset conversion)
            //
            int opLen = op.length();
            out_.write_ulong(opLen + 1);
            out_.write_octet_array(op.getBytes(), 0, opLen);
            out_.write_octet((byte) 0); // nul terminator

            out_.write_ulong(0); // requesting_principal

            break;
        }

        case 2: {
            out_.write_ulong(id); // request_id

            //
            // response_flags
            //
            byte responseFlags = 0;
            if (response)
                responseFlags |= 0x3;
            out_.write_octet(responseFlags);

            //
            // reserved
            //
            final byte[] reserved = { 0, 0, 0 };
            out_.write_octet_array(reserved, 0, 3);

            //
            // target
            //
            out_.write_short(org.omg.GIOP.KeyAddr.value);
            int keyLen = profileInfo_.key.length;
            out_.write_ulong(keyLen);
            out_.write_octet_array(profileInfo_.key, 0, keyLen);

            //
            // operation (we use octets to avoid codeset conversion)
            //
            int opLen = op.length();
            out_.write_ulong(opLen + 1);
            out_.write_octet_array(op.getBytes(), 0, opLen);
            out_.write_octet((byte) 0); // nul terminator

            writeServiceContextList(scl); // service_context

            //
            // For GIOP 1.2, the body (if any) must be aligned on an 8-octet
            // boundary, so we notify the OutputStream that it should align
            // the next write
            //
            out_._OB_alignNext(8);

            break;
        }

        default:
            Assert._OB_assert(false);
        }
    }

    void writeReplyHeader(int id, org.omg.GIOP.ReplyStatusType_1_2 status,
            org.omg.IOP.ServiceContext[] scl) {
        switch (profileInfo_.minor) {
        case 0:
        case 1: {
            Assert
                    ._OB_assert(status.value() <= org.omg.GIOP.ReplyStatusType_1_2._LOCATION_FORWARD);

            writeServiceContextList(scl); // service_context
            out_.write_ulong(id); // request_id
            out_.write_ulong(status.value()); // reply_status

            break;
        }

        case 2: {
            out_.write_ulong(id); // request_id
            out_.write_ulong(status.value()); // reply_status
            writeServiceContextList(scl); // service_context

            //
            // For GIOP 1.2, the body (if any) must be aligned on an 8-octet
            // boundary, so we notify the OutputStream that it should align
            // the next write
            //
            out_._OB_alignNext(8);

            break;
        }

        default:
            Assert._OB_assert(false);
        }
    }

    void writeCancelRequestHeader(int id) {
        out_.write_ulong(id); // request_id
    }

    // Not currently used
    void writeLocateRequestHeader(int id) {
        switch (profileInfo_.minor) {
        case 0:
        case 1: {
            out_.write_ulong(id); // request_id

            //
            // object_key
            //
            int keyLen = profileInfo_.key.length;
            out_.write_ulong(keyLen);
            out_.write_octet_array(profileInfo_.key, 0, keyLen);

            break;
        }

        case 2: {
            out_.write_ulong(id); // request_id

            //
            // target
            //
            out_.write_short(org.omg.GIOP.KeyAddr.value);
            int keyLen = profileInfo_.key.length;
            out_.write_ulong(keyLen);
            out_.write_octet_array(profileInfo_.key, 0, keyLen);

            break;
        }

        default:
            Assert._OB_assert(false);
        }
    }

    // Not currently used
    void writeLocateReplyHeader(int id, org.omg.GIOP.LocateStatusType_1_2 status) {
        switch (profileInfo_.minor) {
        case 0:
        case 1: {
            Assert
                    ._OB_assert(status.value() <= org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD);

            out_.write_ulong(id); // request_id
            out_.write_ulong(status.value()); // locate_status

            break;
        }

        case 2: {
            out_.write_ulong(id); // request_id
            out_.write_ulong(status.value()); // locate_status

            //
            // Do NOT align a locate reply body on an 8-octet boundary
            //

            break;
        }

        default:
            Assert._OB_assert(false);
        }
    }

    // Currently not used
    void writeFragmentHeader(int id) {
        Assert
                ._OB_assert(!(profileInfo_.major == 1 && profileInfo_.minor <= 1));

        out_.write_ulong(id); // request_id

        //
        // TODO: Possibly need to align on 8-octet boundary in GIOP 1.2
        // (see Interop issue #2521)
        //
    }

    // ----------------------------------------------------------------------
    // GIOPOutgoingMessage public member implementations
    // ----------------------------------------------------------------------

    public static void setMaxMessageSize(int max) {
        maxMessageSize_ = max;
    }
}

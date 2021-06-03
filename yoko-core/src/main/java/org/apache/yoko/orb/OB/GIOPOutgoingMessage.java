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

import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.GIOP.IORAddressingInfo;
import org.omg.GIOP.IORAddressingInfoHelper;
import org.omg.GIOP.KeyAddr;
import org.omg.GIOP.LocateStatusType_1_2;
import org.omg.GIOP.MsgType_1_1;
import org.omg.GIOP.ReplyStatusType_1_2;
import org.omg.GIOP.TargetAddress;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;

final public class GIOPOutgoingMessage {
    private static int maxMessageSize_ = 0; // TODO: pick a default

    private ORBInstance orbInstance_;

    private OutputStream out_;

    private ProfileInfo profileInfo_;

    // ----------------------------------------------------------------------
    // GIOPOutgoingMessage private and protected member implementations
    // ----------------------------------------------------------------------

    private void writeServiceContextList(ServiceContexts contexts) {
        int len = contexts.size();
        out_.write_ulong(len);
        for (ServiceContext sc: contexts) {
            out_.write_ulong(sc.context_id);
            int n = sc.context_data.length;
            out_.write_ulong(n);
            out_.write_octet_array(sc.context_data, 0, n);
        }
    }

    private void writeTargetAddress(TargetAddress target) {
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
            TaggedProfile profile = target.profile();
            TaggedProfileHelper.write(out_, profile);
            break;
        }

        case 2: // GIOP::ReferenceAddr
        {
            IORAddressingInfo info = target.ior();
            IORAddressingInfoHelper.write(out_, info);
            break;
        }

        default:
            throw Assert.fail();
        }
    }

    // ----------------------------------------------------------------------
    // GIOPOutgoingMessage package member implementations
    // ----------------------------------------------------------------------

    GIOPOutgoingMessage(ORBInstance orbInstance,
            OutputStream out,
            ProfileInfo profileInfo) {
        orbInstance_ = orbInstance;
        out_ = out;
        profileInfo_ = profileInfo;
    }

    ProfileInfo profileInfo() {
        return profileInfo_;
    }

    void writeMessageHeader(MsgType_1_1 type, boolean fragment,
                            int size) {
        Assert.ensure(type.value() >= 0
                && type.value() <= MsgType_1_1._Fragment);
        Assert
                .ensure(!(profileInfo_.major == (byte) 1
                        && profileInfo_.minor == (byte) 0 && (type.value() > MsgType_1_1._MessageError || fragment)));

        if (maxMessageSize_ > 0 && size > maxMessageSize_) {
            String msg = "outgoing message size (" + size
                    + ") exceeds maximum (" + maxMessageSize_ + ")";
            orbInstance_.getLogger().warning(msg);

            throw new IMP_LIMIT(MinorCodes
                    .describeImpLimit(MinorCodes.MinorMessageSizeLimit),
                    MinorCodes.MinorMessageSizeLimit,
                    CompletionStatus.COMPLETED_NO);
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

    void writeRequestHeader(int id, String op, boolean response, ServiceContexts contexts) {
        switch (profileInfo_.minor) {
        case 0:
        case 1: {
            writeServiceContextList(contexts); // service_context
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
            out_.write_short(KeyAddr.value);
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

            writeServiceContextList(contexts); // service_context

            //
            // For GIOP 1.2, the body (if any) must be aligned on an 8-octet
            // boundary, so we notify the OutputStream that it should align
            // the next write
            //
            out_.markGiop_1_2_HeaderComplete();

            break;
        }

        default:
            throw Assert.fail();
        }
    }

    void writeReplyHeader(int id, ReplyStatusType_1_2 status, ServiceContexts contexts) {
        switch (profileInfo_.minor) {
        case 0:
        case 1: {
            Assert.ensure(status.value() <= ReplyStatusType_1_2._LOCATION_FORWARD);

            writeServiceContextList(contexts); // service_context
            out_.write_ulong(id); // request_id
            out_.write_ulong(status.value()); // reply_status

            break;
        }

        case 2: {
            out_.write_ulong(id); // request_id
            out_.write_ulong(status.value()); // reply_status
            writeServiceContextList(contexts); // service_context

            //
            // For GIOP 1.2, the body (if any) must be aligned on an 8-octet
            // boundary, so we notify the OutputStream that it should align
            // the next write
            //
            out_.markGiop_1_2_HeaderComplete();

            break;
        }

        default:
            throw Assert.fail();
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
            out_.write_short(KeyAddr.value);
            int keyLen = profileInfo_.key.length;
            out_.write_ulong(keyLen);
            out_.write_octet_array(profileInfo_.key, 0, keyLen);

            break;
        }

        default:
            throw Assert.fail();
        }
    }

    // Not currently used
    void writeLocateReplyHeader(int id, LocateStatusType_1_2 status) {
        switch (profileInfo_.minor) {
        case 0:
        case 1: {
            Assert.ensure(status.value() <= LocateStatusType_1_2._OBJECT_FORWARD);

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
            throw Assert.fail();
        }
    }

    // Currently not used
    void writeFragmentHeader(int id) {
        Assert
                .ensure(!(profileInfo_.major == 1 && profileInfo_.minor <= 1));

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

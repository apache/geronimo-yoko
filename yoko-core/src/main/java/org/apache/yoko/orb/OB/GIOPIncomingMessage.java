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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.util.Assert;
import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.StringHolder;
import org.omg.GIOP.IORAddressingInfoHelper;
import org.omg.GIOP.LocateStatusType_1_2;
import org.omg.GIOP.LocateStatusType_1_2Holder;
import org.omg.GIOP.MsgType_1_1;
import org.omg.GIOP.ReplyStatusType_1_2;
import org.omg.GIOP.ReplyStatusType_1_2Holder;
import org.omg.GIOP.TargetAddress;
import org.omg.GIOP.TargetAddressHolder;
import org.omg.GIOP.Version;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedProfileHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.yoko.util.Assert.ensure;
import static org.apache.yoko.util.MinorCodes.MinorFragment;
import static org.apache.yoko.util.MinorCodes.MinorMessageSizeLimit;
import static org.apache.yoko.util.MinorCodes.MinorNoGIOP;
import static org.apache.yoko.util.MinorCodes.MinorUnknownMessage;
import static org.apache.yoko.util.MinorCodes.MinorVersion;
import static org.apache.yoko.util.MinorCodes.describeCommFailure;
import static org.apache.yoko.util.MinorCodes.describeImpLimit;
import static org.apache.yoko.io.AlignmentBoundary.EIGHT_BYTE_BOUNDARY;
import static org.apache.yoko.io.AlignmentBoundary.FOUR_BYTE_BOUNDARY;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final public class GIOPIncomingMessage {
    private ORBInstance orbInstance_;

    private InputStream in_;

    private static int maxMessageSize_;

    //
    // Message header
    //
    private org.omg.GIOP.Version version_ = new org.omg.GIOP.Version();

    private boolean littleEndian;

    private boolean fragmentToFollow;

    private MsgType_1_1 type_;

    private int size_;

    private class Fragment {
        private final Version version;

        private final boolean littleEndian;

        private Integer reqId;

        private final MsgType_1_1 type;

        private final WriteBuffer writeBuffer;

        public Fragment(byte major, byte minor, boolean littleEndian, Integer reqId, MsgType_1_1 type, WriteBuffer writeBuffer) {
            this.version = new Version(major, minor);
            this.littleEndian = littleEndian;
            this.reqId = reqId;
            this.type = type;
            this.writeBuffer = writeBuffer;
        }

        /**
         * Add the bytes from a following fragment.
         * The readBuffer should be positioned at the start of the fragment body.
         */
        void addFragment(ORBInstance orbInstance, ReadBuffer readBuffer) {
            if (maxMessageSize_ > 0 && writeBuffer.length() + readBuffer.available() > maxMessageSize_) {
                String msg = "incoming fragment exceeds maximum message size (" + maxMessageSize_ + ")";
                orbInstance_.getLogger().warning(msg);
                throw new IMP_LIMIT(describeImpLimit(MinorMessageSizeLimit), MinorMessageSizeLimit, COMPLETED_NO);
            }
            writeBuffer.ensureAvailable(readBuffer.available());
            readBuffer.readBytes(writeBuffer);
        }
    }

    private ConcurrentMap<Integer, Fragment> fragmentMap; // for GIOP 1.2

    private Fragment lastFragment_; // for GIOP 1.1

    private void skipServiceContextList(InputStream in) {
        int len = in.read_ulong();
        for (int i = 0; i < len; i++) {
            // context_id
            in.skipAlign(FOUR_BYTE_BOUNDARY);
            in._OB_skip(4);
            // context_data
            int datalen = in.read_ulong();
            in._OB_skip(datalen);
        }
    }

    private void readServiceContextList(ServiceContexts contexts) {
        for (int len = in_.read_ulong(); len > 0; len--) {
            contexts.mutable().add(readServiceContext());
        }
    }

    private ServiceContext readServiceContext() {
        ServiceContext sc = new ServiceContext();
        sc.context_id = in_.read_ulong();
        int datalen = in_.read_ulong();
        sc.context_data = new byte[datalen];
        in_.read_octet_array(sc.context_data, 0, datalen);
        return sc;
    }

    private void readTargetAddress(TargetAddressHolder target) {
        target.value = new TargetAddress();
        short discriminator = in_.read_short();
        switch (discriminator) {
        case 0: // GIOP::KeyAddr
            int len = in_.read_ulong();
            byte[] seq = new byte[len];
            in_.read_octet_array(seq, 0, len);
            target.value.object_key(seq);
            break;
        case 1: // GIOP::ProfileAddr
            target.value.profile(TaggedProfileHelper.read(in_));
            break;
        case 2: // GIOP::ReferenceAddr
            target.value.ior(IORAddressingInfoHelper.read(in_));
            break;
        default:
            throw new COMM_FAILURE(describeCommFailure(MinorNoGIOP) + ": invalid target address", MinorNoGIOP, COMPLETED_MAYBE);
        }
    }

    // ----------------------------------------------------------------------
    // GIOPIncomingMessage package member implementations
    // ----------------------------------------------------------------------

    GIOPIncomingMessage(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
        fragmentMap = new ConcurrentHashMap<>();
        lastFragment_ = null;
    }

    org.omg.GIOP.Version version() {
        return version_;
    }

    boolean swap() {
        return littleEndian;
    }

    MsgType_1_1 type() {
        return type_;
    }

    int size() {
        return size_;
    }

    InputStream input() {
        InputStream result = in_;
        in_ = null;
        return result;
    }

    void extractHeader(ReadBuffer br) {
        InputStream in = new InputStream(br, false);
        in_ = null;

        if ('G' != in.read_octet() || 'I' != in.read_octet() || 'O' != in.read_octet() || 'P' != in.read_octet())
            throw new COMM_FAILURE(describeCommFailure(MinorNoGIOP) + ": missing GIOP magic key", MinorNoGIOP, COMPLETED_MAYBE);

        //
        // Peek at version
        //
        version_.major = in.read_octet();
        version_.minor = in.read_octet();

        if (version_.major != 1 || version_.minor > 2)
            throw new COMM_FAILURE(describeCommFailure(MinorVersion), MinorVersion, COMPLETED_MAYBE);

        switch (version_.minor) {
        case 0: {
            littleEndian = in.read_boolean();
            in._OB_swap(littleEndian);
            fragmentToFollow = false;
            final int msgType = in.read_octet();
            type_ = MsgType_1_1.from_int(msgType);
            size_ = in.read_ulong();

            if (type_.value() > MsgType_1_1._MessageError)
                throw new COMM_FAILURE(describeCommFailure(MinorUnknownMessage) + ": invalid message type for GIOP 1.0", MinorUnknownMessage, COMPLETED_MAYBE);

            break;
        }

        case 1:
        case 2: {
            byte flags = in.read_octet();
            littleEndian = ((flags & 0x01) == 1);
            fragmentToFollow = ((flags & 0x02) == 2);
            in._OB_swap(littleEndian);
            type_ = MsgType_1_1.from_int(in.read_octet());
            size_ = in.read_ulong();

            if (type_.value() > MsgType_1_1._Fragment)
                throw new COMM_FAILURE(describeCommFailure(MinorUnknownMessage) + ": invalid message type for GIOP 1.1/1.2", MinorUnknownMessage, COMPLETED_MAYBE);

            break;
        }

        default:
            throw Assert.fail();
        }

        if (maxMessageSize_ > 0 && size_ > maxMessageSize_) {
            String msg = "incoming message size (" + size_ + ") exceeds maximum (" + maxMessageSize_ + ")";
            orbInstance_.getLogger().warning(msg);

            throw new IMP_LIMIT(describeImpLimit(MinorMessageSizeLimit), MinorMessageSizeLimit, COMPLETED_MAYBE);
        }

        //
        // The spec says:
        //
        // For GIOP version 1.2, if the second least significant bit of
        // Flags is 1, the sum of the message_size value and 12 must be
        // evenly divisible by 8.
        //
        if (version_.minor == 2 && fragmentToFollow && (size_ + 12) % 8 != 0)
            throw new COMM_FAILURE(String.format("%s: invalid GIOP 1.2 fragment size %d", describeCommFailure(MinorFragment), size_), MinorFragment, COMPLETED_MAYBE);
    }

    /**
     * Parse in a message
     * @param writer the buffer containing the message — to be owned by this object
     * @return true iff the message was complete
     */
    boolean consumeBuffer(WriteBuffer writer) {
        // Handle initial fragmented message
        if (fragmentToFollow && type_ != MsgType_1_1.Fragment) {
            startNewFragmentedMessage(writer);
            return false;
        }

        if (type_ == MsgType_1_1.Fragment) {
            return consumeFragment(writer.readFromStart());
        }

        if (type_ == MsgType_1_1.CancelRequest) {
            processCancelRequest(writer.readFromStart());
            return true;
        }

        // Message is not fragmented and is not a CancelRequest,
        // so we must have the complete message
        readEntireMessage(writer.readFromStart());
        return true;
    }

    private boolean consumeFragment(ReadBuffer reader) {
        // Fragment not supported in GIOP 1.0
        if (version_.minor < 1) throw new COMM_FAILURE(describeCommFailure(MinorFragment), MinorFragment, COMPLETED_MAYBE);

        final Fragment complete = version_.minor == 1
                ? handleFollowingFragmentGiop11(reader)
                : handleFollowingFragmentGiop12(reader);

        if (complete == null) return false;

        // We have received the last fragment, so reset our internal
        // state to appear as if we had just received the entire message
        version_ = complete.version;
        littleEndian = complete.littleEndian;
        type_ = complete.type;
        fragmentToFollow = false;
        // NOTE:  size_ is the size of the message, which doesn't
        // include the message header.  We need to adjust this for
        // fragmented messages otherwise we risk not detecting the
        // correct end of the buffer.
        size_ = complete.writeBuffer.length() - 12;
        readEntireMessage(complete.writeBuffer.readFromStart());
        return true;
    }

    private Fragment handleFollowingFragmentGiop11(ReadBuffer reader) {
        // If there was no previous fragment, we cannot process this fragment
        if (lastFragment_ == null) throw new COMM_FAILURE(describeCommFailure(MinorFragment), MinorFragment, COMPLETED_MAYBE);
        // Append buffer to existing data. We need to skip the header data
        // (the input stream is already positioned past the header).
        Assert.ensure(reader.getPosition() == 12);
        lastFragment_.addFragment(orbInstance_, reader);
        // TODO: not sure how this can work, since the boundary alignment can shift with each fragment

        // If we haven't read the request ID yet, then try to get it now
        if (lastFragment_.reqId == null) {
            InputStream reqIn = new InputStream(lastFragment_.writeBuffer.readFromStart(), 12, swap());
            try {
                skipServiceContextList(reqIn);
                lastFragment_.reqId = reqIn.read_ulong();
            } catch (MARSHAL ignored) {
                // we don't have the request ID yet
            }
        }

        // If fragment_ == false, then this is the last fragment
        if (fragmentToFollow) return null;

        try { return lastFragment_; } finally { lastFragment_ = null; }
    }

    private Fragment handleFollowingFragmentGiop12(ReadBuffer reader) {
        Fragment complete;
        // GIOP 1.2 defines the FragmentHeader message header, to allow
        // interleaving of Fragment messages for different requests
        InputStream in = new InputStream(reader, 12, swap());
        int reqId = in.read_ulong();

        // Find fragment data for request
        Fragment frag = fragmentMap.get(reqId);

        // If no fragment was found for the request, then either
        // the request was discarded, or the server is sending
        // invalid messages. Otherwise, we can append the buffer
        // to the existing data.
        if (frag == null) return null;
        // Append buffer to existing data. We need to skip the
        // header data (the input stream is already positioned
        // past the header).
        Assert.ensure(reader.getPosition() == 16);
        frag.addFragment(orbInstance_, reader);

        // If there is another fragment to follow, don't return anything to process
        if (fragmentToFollow) return null;

        // That was the final fragment so process this message
        fragmentMap.remove(reqId, frag);
        return frag;

        // TODO: clean up this fragment map somehow —
        //  maybe rig up each entry with a TTL set to request timeout
    }

    private void processCancelRequest(ReadBuffer reader) {
        readEntireMessage(reader);

        // Check if cancelled message corresponds to a fragment
        int reqId = readCancelRequestHeader();

        if (version_.minor == 1) {// GIOP 1.1
            // cancel the current fragmented message if the request ID matches
            if (lastFragment_ != null && lastFragment_.reqId == reqId) lastFragment_ = null;
        } else { // GIOP 1.2
            // discard any accumulated fragmented message corresponding to the cancelled id
            fragmentMap.remove(reqId);
            // TODO: is it valid to cancel a REPLY message? Maybe check the type before removing
        }

        in_._OB_reset();
    }

    private void readEntireMessage(ReadBuffer reader) {
        in_ = new InputStream(reader, 12, swap());
    }

    private void startNewFragmentedMessage(WriteBuffer writer) {
        if (version_.minor < 1) {
            throw new COMM_FAILURE(describeCommFailure(MinorFragment), MinorFragment, COMPLETED_MAYBE);
        } else if (version_.minor == 1) {
            startNewGiop11FragmentedMessage(writer);
        } else { // GIOP 1.2
            startNewGiop12FragmentedMessage(writer);
        }
    }

    private void startNewGiop11FragmentedMessage(WriteBuffer writer) {
        // In GIOP 1.1, fragments are only supported for request and reply messages
        if (type_ != MsgType_1_1.Request && type_ != MsgType_1_1.Reply)
            throw new COMM_FAILURE(describeCommFailure(MinorFragment), MinorFragment, COMPLETED_MAYBE);

        // Discard any previous fragmented message since
        // GIOP1.1 only allows one fragmented message in flight
        if (lastFragment_ != null) lastFragment_ = null;

        // Try to obtain the request ID by unmarshalling the
        // Request or Reply header data. If the header is fragmented,
        // a MARSHAL exception could be raised if we don't have enough
        // data.
        Integer reqId = null;
        try {
            InputStream in = new InputStream(writer.readFromStart(), 12, swap());
            skipServiceContextList(in);
            reqId = in.read_ulong();
        } catch (MARSHAL ignored) {
            // we just don't have the request id yet
        }

        lastFragment_ = new Fragment(version_.major, version_.minor, littleEndian, reqId, type_, writer);
    }

    private void startNewGiop12FragmentedMessage(WriteBuffer writer) {
        // In GIOP 1.2, fragments are only supported for request,
        // reply, locate request and locate reply messages
        if (type_ != MsgType_1_1.Request
                && type_ != MsgType_1_1.Reply
                && type_ != MsgType_1_1.LocateRequest
                && type_ != MsgType_1_1.LocateReply) {
            throw new COMM_FAILURE(describeCommFailure(MinorFragment), MinorFragment, COMPLETED_MAYBE);
        }

        // Try to obtain the request ID by unmarshalling the header data.
        final int reqId;
        boolean haveReqId = false;
        try {
            InputStream in = new InputStream(writer.readFromStart(), 12, swap());
            reqId = in.read_ulong();
        } catch (MARSHAL ex) {
            // In GIOP 1.2, the request ID is in the first 16 bytes,
            // and fragments must be at least 16 bytes long.
            // If we failed to read a request id, this is a serious error.
            throw Assert.fail("Should have had 16 bytes in fragment", ex);
        }

        // Add new fragment to fragment list
        Fragment frag = new Fragment(version_.major, version_.minor, littleEndian, reqId, type_, writer);
        fragmentMap.put(reqId, frag);
    }

    int readRequestHeader(BooleanHolder response,
                          TargetAddressHolder target,
                          StringHolder op,
                          ServiceContexts contexts) {
        Assert.ensure(type_ == MsgType_1_1.Request);

        int id = 0;

        switch (version_.minor) {
        case 0: {
            int len;

            readServiceContextList(contexts); // service_context
            id = in_.read_ulong(); // request_id
            response.value = in_.read_boolean(); // response_expected

            //
            // object_key
            //
            len = in_.read_ulong();
            byte[] key = new byte[len];
            in_.read_octet_array(key, 0, len);
            target.value = new TargetAddress();
            target.value.object_key(key);

            //
            // Use octets for operation to avoid codeset conversion
            //
            len = in_.read_ulong();
            byte[] s = new byte[len];
            in_.read_octet_array(s, 0, len);
            op.value = new String(s, 0, len - 1);
            // op.value = in_.read_string(); // operation

            len = in_.read_ulong(); // requesting_principal
            if (len > 0)
                in_._OB_skip(len);

            break;
        }

        case 1: {
            int len;

            readServiceContextList(contexts); // service_context
            id = in_.read_ulong(); // request_id
            response.value = in_.read_boolean(); // response_expected
            in_._OB_skip(3); // reserved

            //
            // object_key
            //
            len = in_.read_ulong();
            byte[] key = new byte[len];
            in_.read_octet_array(key, 0, len);
            target.value = new TargetAddress();
            target.value.object_key(key);

            //
            // Use octets for operation to avoid codeset conversion
            //
            len = in_.read_ulong();
            byte[] s = new byte[len];
            in_.read_octet_array(s, 0, len);
            op.value = new String(s, 0, len - 1);
            // op.value = in_.read_string(); // operation

            len = in_.read_ulong(); // requesting_principal
            if (len > 0)
                in_._OB_skip(len);

            break;
        }

        case 2: {
            id = in_.read_ulong(); // request_id
            byte flags = in_.read_octet(); // response_flags
            response.value = ((flags & 0x01) == 1);
            in_._OB_skip(3); // reserved
            readTargetAddress(target);

            //
            // Use octets for operation to avoid codeset conversion
            //
            int len = in_.read_ulong();
            byte[] s = new byte[len];
            in_.read_octet_array(s, 0, len);
            op.value = new String(s, 0, len - 1);
            // op.value = in_.read_string(); // operation

            readServiceContextList(contexts); // service_context

            //
            // For GIOP 1.2, the body (if present) must be aligned on
            // an 8-octet boundary
            //
            if (in_.getPosition() < size_ + 12) in_.skipAlign(EIGHT_BYTE_BOUNDARY);

            break;
        }

        default:
            throw Assert.fail();
        }

        return id;
    }

    int readReplyHeader(ReplyStatusType_1_2Holder status, ServiceContexts contexts) {
        Assert.ensure(type_ == MsgType_1_1.Reply);

        int id = 0;

        switch (version_.minor) {
        case 0:
        case 1: {
            readServiceContextList(contexts); // service_context
            id = in_.read_ulong(); // request_id
            // reply_status
            status.value = ReplyStatusType_1_2.from_int(in_.read_ulong());
            if (status.value.value() > ReplyStatusType_1_2._LOCATION_FORWARD)
                throw new COMM_FAILURE(describeCommFailure(MinorUnknownMessage) + ": invalid reply status", MinorUnknownMessage, COMPLETED_MAYBE);

            break;
        }

        case 2: {
            id = in_.read_ulong(); // request_id
            // reply_status
            status.value = ReplyStatusType_1_2.from_int(in_.read_ulong());
            if (status.value.value() > ReplyStatusType_1_2._NEEDS_ADDRESSING_MODE)
                throw new COMM_FAILURE(describeCommFailure(MinorUnknownMessage) + ": invalid reply status", MinorUnknownMessage, COMPLETED_MAYBE);
            readServiceContextList(contexts); // service_context

            //
            // For GIOP 1.2, the body (if present) must be aligned on
            // an 8-octet boundary
            //
            if (in_.getPosition() < size_ + 12)
                in_.skipAlign(EIGHT_BYTE_BOUNDARY);

            break;
        }

        default:
            throw Assert.fail();
        }

        return id;
    }

    int readCancelRequestHeader() {
        Assert.ensure(type_ == MsgType_1_1.CancelRequest);

        int id = in_.read_ulong(); // request_id

        return id;
    }

    int readLocateRequestHeader(TargetAddressHolder target) {
        Assert.ensure(type_ == MsgType_1_1.LocateRequest);

        int id = 0;

        switch (version_.minor) {
        case 0:
        case 1: {
            id = in_.read_ulong(); // request_id

            //
            // object_key
            //
            int keylen = in_.read_ulong();
            byte[] key = new byte[keylen];
            in_.read_octet_array(key, 0, keylen);
            target.value = new TargetAddress();
            target.value.object_key(key);

            break;
        }

        case 2: {
            id = in_.read_ulong(); // request_id
            readTargetAddress(target); // target

            break;
        }

        default:
            throw Assert.fail();
        }

        return id;
    }

    // Not currently used
    int readLocateReplyHeader(LocateStatusType_1_2Holder status) {
        Assert.ensure(type_ == MsgType_1_1.LocateReply);

        int id = 0;

        switch (version_.minor) {
        case 0:
        case 1: {
            id = in_.read_ulong(); // request_id

            // locate_status
            status.value = LocateStatusType_1_2.from_int(in_
                    .read_ulong());
            if (status.value.value() > LocateStatusType_1_2._OBJECT_FORWARD)
                throw new COMM_FAILURE(describeCommFailure(MinorUnknownMessage) + ": invalid locate reply status", MinorUnknownMessage, COMPLETED_MAYBE);

            break;
        }

        case 2: {
            id = in_.read_ulong(); // request_id

            // locate_status
            status.value = LocateStatusType_1_2.from_int(in_
                    .read_ulong());
            if (status.value.value() > LocateStatusType_1_2._LOC_NEEDS_ADDRESSING_MODE)
	                throw new COMM_FAILURE(describeCommFailure(MinorUnknownMessage) + ": invalid locate reply status", MinorUnknownMessage, COMPLETED_MAYBE);

            //
            // Do NOT align a locate reply body on an 8-octet boundary
            //

            break;
        }

        default:
            throw Assert.fail();
        }

        return id;
    }

    public static void setMaxMessageSize(int max) {
        maxMessageSize_ = max;
    }
}

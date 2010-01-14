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

final public class GIOPIncomingMessage {
    private ORBInstance orbInstance_;

    private org.apache.yoko.orb.CORBA.InputStream in_;

    private static int maxMessageSize_;

    //
    // Message header
    //
    private org.omg.GIOP.Version version_ = new org.omg.GIOP.Version();

    private boolean byteOrder_;

    private boolean fragment_;

    private org.omg.GIOP.MsgType_1_1 type_;

    private int size_;

    private class Fragment {
        private org.omg.GIOP.Version version;

        private boolean byteOrder;

        private int reqId;

        private boolean haveReqId;

        private org.omg.GIOP.MsgType_1_1 type;

        private org.apache.yoko.orb.OCI.Buffer buf;

        Fragment next;

        void add(ORBInstance orbInstance, org.apache.yoko.orb.OCI.Buffer b) {
            int len = buf.length();
            if (maxMessageSize_ > 0 && len + b.rest_length() > maxMessageSize_) {
                String msg = "incoming fragment exceeds "
                        + "maximum message size (" + maxMessageSize_ + ")";

                orbInstance_.getLogger().warning(msg);

                throw new org.omg.CORBA.IMP_LIMIT(org.apache.yoko.orb.OB.MinorCodes
                        .describeImpLimit(org.apache.yoko.orb.OB.MinorCodes.MinorMessageSizeLimit),
                        org.apache.yoko.orb.OB.MinorCodes.MinorMessageSizeLimit,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
            buf.realloc(len + b.rest_length());
            System.arraycopy(b.data(), b.pos(), buf.data(), len, b
                    .rest_length());
        }
    }

    private Fragment fragmentHead_; // for GIOP 1.2

    private Fragment lastFragment_; // for GIOP 1.1

    // ----------------------------------------------------------------------
    // GIOPIncomingMessage private and protected member implementations
    // ----------------------------------------------------------------------

    private int readFragmentHeader(org.apache.yoko.orb.CORBA.InputStream in) {
        int id = 0;

        switch (version_.minor) {
        case 0: {
            //
            // Not supported in GIOP 1.0
            //
            Assert._OB_assert(false);
        }

        case 1: {
            //
            // Fragment message is supported in 1.1, but not FragmentHeader
            //
            Assert._OB_assert(false);
        }

        case 2: {
            id = in.read_ulong();

            //
            // TODO: Possibly need to align on 8-octet boundary in GIOP 1.2
            // (see Interop issue #2521)
            //

            break;
        }

        default:
            Assert._OB_assert(false);
        }

        return id;
    }

    private void skipServiceContextList(org.apache.yoko.orb.CORBA.InputStream in) {
        int len = in.read_ulong();
        for (int i = 0; i < len; i++) {
            // context_id
            in._OB_skipAlign(4);
            in._OB_skip(4);
            // context_data
            int datalen = in.read_ulong();
            in._OB_skip(datalen);
        }
    }

    private void readServiceContextList(org.omg.IOP.ServiceContextListHolder scl) {
        int len = in_.read_ulong();
        scl.value = new org.omg.IOP.ServiceContext[len];
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                scl.value[i] = new org.omg.IOP.ServiceContext();
                org.omg.IOP.ServiceContext sc = scl.value[i];
                sc.context_id = in_.read_ulong();
                int datalen = in_.read_ulong();
                sc.context_data = new byte[datalen];
                in_.read_octet_array(sc.context_data, 0, datalen);
            }
        }
    }

    private void readTargetAddress(org.omg.GIOP.TargetAddressHolder target) {
        target.value = new org.omg.GIOP.TargetAddress();
        short disc = in_.read_short();
        switch (disc) {
        case 0: // GIOP::KeyAddr
        {
            int len = in_.read_ulong();
            byte[] seq = new byte[len];
            in_.read_octet_array(seq, 0, len);
            target.value.object_key(seq);
            break;
        }

        case 1: // GIOP::ProfileAddr
        {
            target.value.profile(org.omg.IOP.TaggedProfileHelper.read(in_));
            break;
        }

        case 2: // GIOP::ReferenceAddr
        {
            target.value.ior(org.omg.GIOP.IORAddressingInfoHelper.read(in_));
            break;
        }

        default:
            throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorNoGIOP)
                    + ": invalid target address", org.apache.yoko.orb.OB.MinorCodes.MinorNoGIOP,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

        }
    }

    // ----------------------------------------------------------------------
    // GIOPIncomingMessage package member implementations
    // ----------------------------------------------------------------------

    GIOPIncomingMessage(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
        fragmentHead_ = null;
        lastFragment_ = null;
    }

    org.omg.GIOP.Version version() {
        return version_;
    }

    boolean swap() {
        return byteOrder_ != false;
    }

    org.omg.GIOP.MsgType_1_1 type() {
        return type_;
    }

    int size() {
        return size_;
    }

    org.apache.yoko.orb.CORBA.InputStream input() {
        org.apache.yoko.orb.CORBA.InputStream result = in_;
        in_ = null;
        return result;
    }

    void extractHeader(org.apache.yoko.orb.OCI.Buffer buf) {
        in_ = null;

        byte[] pos = buf.data();
        if (pos[0] != (byte) 'G' || pos[1] != (byte) 'I'
                || pos[2] != (byte) 'O' || pos[3] != (byte) 'P') {
            throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorNoGIOP)
                    + ": missing GIOP magic key", org.apache.yoko.orb.OB.MinorCodes.MinorNoGIOP,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        }

        //
        // Peek at version
        //
        version_.major = pos[4];
        version_.minor = pos[5];

        if (version_.major != 1 || version_.minor > 2)
            throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorVersion),
                    org.apache.yoko.orb.OB.MinorCodes.MinorVersion,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false);

        switch (version_.minor) {
        case 0: {
            in._OB_skip(6); // magic + GIOP_version
            byteOrder_ = in.read_boolean();
            in._OB_swap(byteOrder_ != false);
            fragment_ = false;
            type_ = org.omg.GIOP.MsgType_1_1.from_int(in.read_octet());
            size_ = in.read_ulong();

            if (type_.value() > org.omg.GIOP.MsgType_1_1._MessageError)
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage)
                        + ": invalid message type for GIOP 1.0",
                        org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

            break;
        }

        case 1:
        case 2: {
            in._OB_skip(6); // magic + GIOP_version
            byte flags = in.read_octet();
            byteOrder_ = ((flags & 0x01) == 1);
            fragment_ = ((flags & 0x02) == 2);
            in._OB_swap(byteOrder_ != false);
            type_ = org.omg.GIOP.MsgType_1_1.from_int(in.read_octet());
            size_ = in.read_ulong();

            if (type_.value() > org.omg.GIOP.MsgType_1_1._Fragment)
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage)
                        + ": invalid message type for GIOP 1.1/1.2",
                        org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

            break;
        }

        default:
            Assert._OB_assert(false);
        }

        if (maxMessageSize_ > 0 && size_ > maxMessageSize_) {
            String msg = "incoming message size (" + size_
                    + ") exceeds maximum (" + maxMessageSize_ + ")";
            orbInstance_.getLogger().warning(msg);

            throw new org.omg.CORBA.IMP_LIMIT(org.apache.yoko.orb.OB.MinorCodes
                    .describeImpLimit(org.apache.yoko.orb.OB.MinorCodes.MinorMessageSizeLimit),
                    org.apache.yoko.orb.OB.MinorCodes.MinorMessageSizeLimit,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        }

        //
        // The spec says:
        //
        // For GIOP version 1.2, if the second least significant bit of
        // Flags is 1, the sum of the message_size value and 12 must be
        // evenly divisible by 8.
        //
        if (version_.minor == 2 && fragment_ && (size_ + 12) % 8 != 0)
            throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorFragment)
                    + ": invalid GIOP 1.2 fragment size",
                    org.apache.yoko.orb.OB.MinorCodes.MinorFragment,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    boolean consumeBuffer(org.apache.yoko.orb.OCI.Buffer buf) {
        //
        // Consume input buffer
        //
        boolean result = false;

        //
        // Handle initial fragmented message
        //
        if (fragment_ && type_ != org.omg.GIOP.MsgType_1_1.Fragment) {
            if (version_.minor < 1) {
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorFragment),
                        org.apache.yoko.orb.OB.MinorCodes.MinorFragment,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
            } else if (version_.minor == 1) {
                //
                // In GIOP 1.1, fragments are only supported for request and
                // reply messages
                //
                if (type_ != org.omg.GIOP.MsgType_1_1.Request
                        && type_ != org.omg.GIOP.MsgType_1_1.Reply)
                    throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorFragment),
                            org.apache.yoko.orb.OB.MinorCodes.MinorFragment,
                            org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

                //
                // If lastFragment_ is not 0, then the previous fragmented
                // message may have been cancelled
                //
                if (lastFragment_ != null)
                    lastFragment_ = null;

                //
                // Try to obtain the request ID by unmarshalling the
                // Request or Reply header data. If the header is fragmented,
                // a MARSHAL exception could be raised if we don't have enough
                // data.
                //
                int reqId = 0;
                boolean haveReqId = false;
                try {
                    org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                            buf, 12, swap());
                    skipServiceContextList(in);
                    reqId = in.read_ulong();
                    haveReqId = true;
                } catch (org.omg.CORBA.MARSHAL ex) {
                }

                lastFragment_ = new Fragment();
                lastFragment_.version = new org.omg.GIOP.Version(
                        version_.major, version_.minor);
                lastFragment_.byteOrder = byteOrder_;
                lastFragment_.reqId = reqId;
                lastFragment_.haveReqId = haveReqId;
                lastFragment_.type = type_;
                lastFragment_.buf = buf;
                lastFragment_.next = null;
            } else // GIOP 1.2
            {
                //
                // In GIOP 1.2, fragments are only supported for request,
                // reply, locate request and locate reply messages
                //
                if (type_ != org.omg.GIOP.MsgType_1_1.Request
                        && type_ != org.omg.GIOP.MsgType_1_1.Reply
                        && type_ != org.omg.GIOP.MsgType_1_1.LocateRequest
                        && type_ != org.omg.GIOP.MsgType_1_1.LocateReply) {
                    throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorFragment),
                            org.apache.yoko.orb.OB.MinorCodes.MinorFragment,
                            org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
                }

                //
                // Try to obtain the request ID by unmarshalling the
                // header data. If the header is fragmented, a MARSHAL
                // exception could be raised if we don't have enough
                // data.
                //
                int reqId = 0;
                boolean haveReqId = false;
                try {
                    org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                            buf, 12, swap());
                    reqId = in.read_ulong();
                    haveReqId = true;
                } catch (org.omg.CORBA.MARSHAL ex) {
                }

                //
                // What to do if initial message doesn't contain the
                // request ID?
                //
                Assert._OB_assert(haveReqId);

                //
                // Add new fragment to fragment list
                //
                Fragment f = new Fragment();
                f.version = new org.omg.GIOP.Version(version_.major,
                        version_.minor);
                f.byteOrder = byteOrder_;
                f.reqId = reqId;
                f.haveReqId = haveReqId;
                f.type = type_;
                f.buf = buf;
                f.next = fragmentHead_;
                fragmentHead_ = f;
            }
        } else if (type_ == org.omg.GIOP.MsgType_1_1.Fragment) {
            Fragment complete = null;

            if (version_.minor < 1) {
                //
                // Fragment not supported in GIOP 1.0
                //
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorFragment),
                        org.apache.yoko.orb.OB.MinorCodes.MinorFragment,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
            } else if (version_.minor == 1) {
                //
                // If lastFragment_ == 0, then we received a Fragment message
                // without an initial message
                //
                if (lastFragment_ == null)
                    throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorFragment),
                            org.apache.yoko.orb.OB.MinorCodes.MinorFragment,
                            org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

                //
                // Append buffer to existing data. We need to skip the
                // header data (the input stream is already positioned
                // past the header).
                //
                lastFragment_.add(orbInstance_, buf);

                //
                // If we haven't read the request ID yet, then try to
                // get it now
                //
                if (!lastFragment_.haveReqId) {
                    org.apache.yoko.orb.CORBA.InputStream reqIn = new org.apache.yoko.orb.CORBA.InputStream(
                            lastFragment_.buf, 12, swap());
                    try {
                        skipServiceContextList(reqIn);
                        lastFragment_.reqId = reqIn.read_ulong();
                        lastFragment_.haveReqId = true;
                    } catch (org.omg.CORBA.MARSHAL ex) {
                    }
                }

                //
                // If fragment_ == false, then this is the last fragment
                //
                if (!fragment_) {
                    complete = lastFragment_;
                    lastFragment_ = null;
                }
            } else // GIOP 1.2
            {
                //
                // GIOP 1.2 defines the FragmentHeader message header,
                // to allow interleaving of Fragment messages for
                // different requests
                //
                org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                        buf, 12, swap());
                int reqId = readFragmentHeader(in);

                //
                // Find fragment data for request
                //
                Fragment frag = null;
                Fragment p = fragmentHead_;
                Fragment prev = null;
                while (p != null) {
                    Fragment f = p;
                    if (f.haveReqId && f.reqId == reqId) {
                        frag = f;
                        break;
                    } else {
                        prev = p;
                        p = f.next;
                    }
                }

                //
                // If no fragment was found for the request, then either
                // the request was discarded, or the server is sending
                // invalid messages. Otherwise, we can append the buffer
                // to the existing data.
                //
                if (frag != null) {
                    //
                    // Append buffer to existing data. We need to skip the
                    // header data (the input stream is already positioned
                    // past the header).
                    //
                    frag.add(orbInstance_, in._OB_buffer());

                    //
                    // If fragment_ == false, then this is the last fragment
                    //
                    if (!fragment_) {
                        //
                        // Remove fragment from list
                        //
                        if (prev == null)
                            fragmentHead_ = frag.next;
                        else
                            prev.next = frag.next;
                        complete = frag;
                    }
                }
            }

            //
            // We have received the last fragment, so reset our internal
            // state to appear as if we had just received the entire message
            //
            if (complete != null) {
                version_ = complete.version;
                byteOrder_ = complete.byteOrder;
                type_ = complete.type;
                fragment_ = false;
                // NOTE:  size_ is the size of the message, which doesn't 
                // include the message header.  We need to adjust this for 
                // fragmented messages otherwise we risk not detecting the 
                // correct end of the buffer. 
                size_ = complete.buf.length() - 12;
                in_ = new org.apache.yoko.orb.CORBA.InputStream(complete.buf,
                        12, swap());
                complete = null;
                result = true;
            }
        } else if (type_ == org.omg.GIOP.MsgType_1_1.CancelRequest) {
            in_ = new org.apache.yoko.orb.CORBA.InputStream(buf, 12, swap());

            //
            // Check if cancelled message corresponds to a fragment
            //
            int reqId = readCancelRequestHeader();

            if (version_.minor == 1) // GIOP 1.1
            {
                if (lastFragment_ != null && lastFragment_.haveReqId
                        && lastFragment_.reqId == reqId) {
                    lastFragment_ = null;
                }
            } else // GIOP 1.2
            {
                Fragment p = fragmentHead_;
                while (p != null) {
                    Fragment f = p;
                    if (f.haveReqId && f.reqId == reqId) {
                        p = f.next;
                        f = null;
                        break;
                    } else
                        p = f.next;
                }
            }

            in_._OB_reset();
            result = true;
        } else {
            //
            // Message is not fragmented and is not a CancelRequest, so
            // we must have the complete message
            //
            in_ = new org.apache.yoko.orb.CORBA.InputStream(buf, 12, swap());
            result = true;
        }

        return result;
    }

    int readRequestHeader(org.omg.CORBA.BooleanHolder response,
            org.omg.GIOP.TargetAddressHolder target,
            org.omg.CORBA.StringHolder op,
            org.omg.IOP.ServiceContextListHolder scl) {
        Assert._OB_assert(type_ == org.omg.GIOP.MsgType_1_1.Request);

        int id = 0;

        switch (version_.minor) {
        case 0: {
            int len;

            readServiceContextList(scl); // service_context
            id = in_.read_ulong(); // request_id
            response.value = in_.read_boolean(); // response_expected

            //
            // object_key
            //
            len = in_.read_ulong();
            byte[] key = new byte[len];
            in_.read_octet_array(key, 0, len);
            target.value = new org.omg.GIOP.TargetAddress();
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

            readServiceContextList(scl); // service_context
            id = in_.read_ulong(); // request_id
            response.value = in_.read_boolean(); // response_expected
            in_._OB_skip(3); // reserved

            //
            // object_key
            //
            len = in_.read_ulong();
            byte[] key = new byte[len];
            in_.read_octet_array(key, 0, len);
            target.value = new org.omg.GIOP.TargetAddress();
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

            readServiceContextList(scl); // service_context

            //
            // For GIOP 1.2, the body (if present) must be aligned on
            // an 8-octet boundary
            //
            if (in_._OB_pos() < size_ + 12)
            {
                in_._OB_skipAlign(8);
            }

            break;
        }

        default:
            Assert._OB_assert(false);
        }

        return id;
    }

    int readReplyHeader(org.omg.GIOP.ReplyStatusType_1_2Holder status,
            org.omg.IOP.ServiceContextListHolder scl) {
        Assert._OB_assert(type_ == org.omg.GIOP.MsgType_1_1.Reply);

        int id = 0;

        switch (version_.minor) {
        case 0:
        case 1: {
            readServiceContextList(scl); // service_context
            id = in_.read_ulong(); // request_id
            // reply_status
            status.value = org.omg.GIOP.ReplyStatusType_1_2.from_int(in_
                    .read_ulong());
            if (status.value.value() > org.omg.GIOP.ReplyStatusType_1_2._LOCATION_FORWARD)
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage)
                        + ": invalid reply status",
                        org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

            break;
        }

        case 2: {
            id = in_.read_ulong(); // request_id
            // reply_status
            status.value = org.omg.GIOP.ReplyStatusType_1_2.from_int(in_
                    .read_ulong());
            if (status.value.value() > org.omg.GIOP.ReplyStatusType_1_2._NEEDS_ADDRESSING_MODE)
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage)
                        + ": invalid reply status",
                        org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
            readServiceContextList(scl); // service_context

            //
            // For GIOP 1.2, the body (if present) must be aligned on
            // an 8-octet boundary
            //
            if (in_._OB_pos() < size_ + 12)
                in_._OB_skipAlign(8);

            break;
        }

        default:
            Assert._OB_assert(false);
        }

        return id;
    }

    int readCancelRequestHeader() {
        Assert._OB_assert(type_ == org.omg.GIOP.MsgType_1_1.CancelRequest);

        int id = in_.read_ulong(); // request_id

        return id;
    }

    int readLocateRequestHeader(org.omg.GIOP.TargetAddressHolder target) {
        Assert._OB_assert(type_ == org.omg.GIOP.MsgType_1_1.LocateRequest);

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
            target.value = new org.omg.GIOP.TargetAddress();
            target.value.object_key(key);

            break;
        }

        case 2: {
            id = in_.read_ulong(); // request_id
            readTargetAddress(target); // target

            break;
        }

        default:
            Assert._OB_assert(false);
        }

        return id;
    }

    // Not currently used
    int readLocateReplyHeader(org.omg.GIOP.LocateStatusType_1_2Holder status) {
        Assert._OB_assert(type_ == org.omg.GIOP.MsgType_1_1.LocateReply);

        int id = 0;

        switch (version_.minor) {
        case 0:
        case 1: {
            id = in_.read_ulong(); // request_id

            // locate_status
            status.value = org.omg.GIOP.LocateStatusType_1_2.from_int(in_
                    .read_ulong());
            if (status.value.value() > org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD)
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage)
                        + ": invalid locate reply status",
                        org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

            break;
        }

        case 2: {
            id = in_.read_ulong(); // request_id

            // locate_status
            status.value = org.omg.GIOP.LocateStatusType_1_2.from_int(in_
                    .read_ulong());
            if (status.value.value() > org.omg.GIOP.LocateStatusType_1_2._LOC_NEEDS_ADDRESSING_MODE)
                throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                        .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage)
                        + ": invalid locate reply status",
                        org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

            //
            // Do NOT align a locate reply body on an 8-octet boundary
            //

            break;
        }

        default:
            Assert._OB_assert(false);
        }

        return id;
    }

    // ----------------------------------------------------------------------
    // GIOPIncomingMessage public member implementations
    // ----------------------------------------------------------------------

    public static void setMaxMessageSize(int max) {
        maxMessageSize_ = max;
    }
}

/*
 * Copyright 2022 IBM Corporation and others.
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
package org.apache.yoko.giop;

import org.apache.yoko.io.Buffer;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.omg.CORBA.MARSHAL;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static org.apache.yoko.giop.MessageType.StringField.OPERATION;
import static org.apache.yoko.io.AlignmentBoundary.EIGHT_BYTE_BOUNDARY;
import static org.apache.yoko.logging.VerboseLogging.DATA_IN_LOG;
import static org.apache.yoko.logging.VerboseLogging.DATA_OUT_LOG;
import static org.apache.yoko.orb.OCI.GiopVersion.GIOP1_0;
import static org.apache.yoko.orb.OCI.GiopVersion.GIOP1_2;
import static org.omg.GIOP.LocateStatusType_1_2._LOC_NEEDS_ADDRESSING_MODE;
import static org.omg.GIOP.LocateStatusType_1_2._LOC_SYSTEM_EXCEPTION;
import static org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD;
import static org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD_PERM;
import static org.omg.GIOP.LocateStatusType_1_2._OBJECT_HERE;
import static org.omg.GIOP.LocateStatusType_1_2._UNKNOWN_OBJECT;
import static org.omg.GIOP.MsgType_1_1._CancelRequest;
import static org.omg.GIOP.MsgType_1_1._CloseConnection;
import static org.omg.GIOP.MsgType_1_1._Fragment;
import static org.omg.GIOP.MsgType_1_1._LocateReply;
import static org.omg.GIOP.MsgType_1_1._LocateRequest;
import static org.omg.GIOP.MsgType_1_1._MessageError;
import static org.omg.GIOP.MsgType_1_1._Reply;
import static org.omg.GIOP.MsgType_1_1._Request;

public enum MessageType {
    REQUEST(_Request) {
        void describeHeader(StringBuilder sb, InputStream in, GiopVersion version) {
            switch (version) {
                case GIOP1_0:
                case GIOP1_1:
                    describeServiceContextList(sb, in);
                    describeReqId(sb, in);
                    describeResponseExpected(sb, in);
                    // alignment of object key automatically skips 3 octets here
                    describeObjectKey(sb, in);
                    OPERATION.describeString(sb,"\t", in);
                    describePrincipal(sb, in);
                    return;
                default:
                    describeReqId(sb, in);
                    describeResponseFlags(sb, in);
                    in._OB_skip(3); // explicitly skip 3 octets (reserved in GIOP 1.2)
                    describeTargetAddress(sb, in);
                    OPERATION.describeString(sb, "\t", in);
                    describeServiceContextList(sb, in);
                    if (0 == in.available()) break; // no request body
                    // in GIOP 1.2 the request body is aligned on an 8-octet boundary
                    in.skipAlign(EIGHT_BYTE_BOUNDARY);
            }
        }
        void describeResponseExpected(StringBuilder sb, InputStream in) { sb.append(String.format("%n\tRESPONSE EXPECTED = %b", in.read_boolean())); }
        void describeResponseFlags(StringBuilder sb, InputStream in) { sb.append(String.format("%n\tRESPONSE FLAGS = 0x%02x", in.read_octet())); }
        void describePrincipal(StringBuilder sb, InputStream in) {
            sb.append(String.format("%n\tREQUESTING PRINCIPAL:"));
            describeOctetSeq(sb, "\t\t", in);
        }
    },
    REPLY(_Reply) {
        void describeHeader(StringBuilder sb, InputStream in, GiopVersion version) {
            switch (version) {
                case GIOP1_0:
                case GIOP1_1:
                    describeServiceContextList(sb, in);
                    describeReqId(sb, in);
                    describeReplyStatus(sb, in);
                    return;
                default:
                    describeReqId(sb, in);
                    describeReplyStatus(sb, in);
                    describeServiceContextList(sb, in);
                    if (0 == in.available()) break; // no reply body
                    // in GIOP 1.2 the reply body is aligned on an 8-octet boundary
                    in.skipAlign(EIGHT_BYTE_BOUNDARY);
            }
        }

        void describeReplyStatus(StringBuilder sb, InputStream in) {
            ReplyStatus status = ReplyStatus.valueOf(in.read_long());
            sb.append(String.format("%n\tREPLY STATUS = %s", status));
        }
    },
    CANCEL_REQUEST(_CancelRequest){
        void describeHeader(StringBuilder sb, InputStream in, GiopVersion version) {
            describeReqId(sb, in);
        }
    },
    LOCATE_REQUEST(_LocateRequest){
        void describeHeader(StringBuilder sb, InputStream in, GiopVersion version) {
            switch (version) {
                case GIOP1_0:
                case GIOP1_1:
                    describeReqId(sb, in);
                    describeObjectKey(sb, in);
                    return;
                default:
                    describeReqId(sb, in);
                    describeTargetAddress(sb, in);
            }
        }
    },
    LOCATE_REPLY(_LocateReply){
        void describeHeader(StringBuilder sb, InputStream in, GiopVersion version) {
            describeReqId(sb, in);
            sb.append(String.format("%n\t%s", getLocateStatus(in)));
        }
        String getLocateStatus(InputStream in) {
            int locStat = in.read_long();
            switch (locStat) {
                case _UNKNOWN_OBJECT: return "LOCATE STATUS = UNKNOWN_OBJECT";
                case _OBJECT_FORWARD: return "LOCATE STATUS = OBJECT_FORWARD";
                case _OBJECT_HERE: return "LOCATE STATUS = OBJECT_HERE";
                case _OBJECT_FORWARD_PERM: return "LOCATE STATUS = OBJECT_FORWARD_PERM";
                case _LOC_SYSTEM_EXCEPTION: return "LOCATE STATUS = LOC_SYSTEM_EXCEPTION";
                case _LOC_NEEDS_ADDRESSING_MODE: return "LOCATE STATUS = LOC_NEEDS_ADDRESSING_MODE";
                default: return String.format("error parsing locate status flag: 0x%08x", locStat);
            }
        }
    },
    CLOSE_CONNECTION(_CloseConnection),
    MESSAGE_ERROR(_MessageError),
    FRAGMENT(_Fragment) {
        void describeHeader(StringBuilder sb, InputStream in, GiopVersion version) {
            if (version == GIOP1_2) describeReqId(sb, in);
        }
    },
    UNKNOWN;

    private static final int LITTLE_ENDIAN_FLAG = 1;
    private static final int FRAG_FLAG = 2;
    private static final String EOL = String.format("%n");

    public final int id;
    public final boolean fragmentable;

    MessageType(int id) {
        this.id = id;
        this.fragmentable = id == _Request || id == _Reply;
    }

    MessageType() { this(-1); }

    public static void logIncomingGiopMessage(WriteBuffer buffer) {
        logGiopMessage(buffer, DATA_IN_LOG, "\nIN COMING ");
    }

    public static void logOutgoingGiopMessage(ReadBuffer buffer) {
        logGiopMessage(buffer, DATA_OUT_LOG, "\nOUT GOING ");
    }

    private static void logGiopMessage(Buffer<?> buffer, Logger logger, String direction) {
        if (! logger.isLoggable(FINE)) return;
        final boolean includeMessageHeader;
        final boolean includeMessageOctets;
        final Level level;
        if (logger.isLoggable(FINEST)) {
            includeMessageHeader = true;
            includeMessageOctets = true;
            level = FINEST;
        } else if (logger.isLoggable(FINER)) {
            includeMessageHeader = false;
            includeMessageOctets = true;
            level = FINER;
        } else {
            includeMessageHeader = false;
            includeMessageOctets = false;
            level = FINE;
        }
        logger.log(level, describeGiopMessage(buffer, includeMessageHeader, includeMessageOctets));
    }

    private static String describeGiopMessage(Buffer<?> buffer, boolean includeMessageHeader, boolean includeMessageOctets) {
        final StringBuilder sb = new StringBuilder();
        try {
            final InputStream in = new InputStream(buffer.newReadBuffer());
            in._OB_skip(4); // skip GIOP magic header
            byte major = in.read_octet();
            byte minor = in.read_octet();
            GiopVersion version = GiopVersion.get(major, minor);
            byte flags = in.read_octet();
            in._OB_swap((flags & LITTLE_ENDIAN_FLAG) == LITTLE_ENDIAN_FLAG);
            MessageType type = valueOf(in.read_octet());
            final int size = in.read_long();
            type.describeGiopHeader(sb, major, minor, version, flags, size);
            if (includeMessageHeader) type.describeHeader(sb, in, version);
            else in._OB_skip(in.available()); // wind to the end to prevent mis-labelling the body
            if (includeMessageOctets) dumpHex(sb, in);
        } catch (Throwable t) {
            sb.append(EOL).append("describeMessage() failed with ").append(t);
            Arrays.stream(t.getStackTrace()).map(e -> EOL + "\t" + e).forEach(sb::append);
        }
        return sb.toString();
    }

    private void describeGiopHeader(StringBuilder sb, byte major, byte minor, GiopVersion version, byte flags, int size) {
        sb.append(String.format("GIOP %d.%d %s MESSAGE%n\tSIZE = %d", major, minor, this, size));
        if (fragmentable && version != GIOP1_0)
            sb.append(String.format("%n\tFRAGMENT_TO_FOLLOW = %s", ((flags & FRAG_FLAG) == FRAG_FLAG)));
    }

    void describeHeader(StringBuilder sb, InputStream in, GiopVersion version) {}

    private static void dumpHex(StringBuilder sb, InputStream in) {
        sb.append(EOL);
        if (in.available() == 0) in.dumpAllData(sb); // there is no body, just print the hex
        else in.dumpAllDataWithPosition(sb, "body"); // print the hex showing where the body starts
    }

    private static void describeObjectKey(StringBuilder sb, InputStream in) {
        sb.append(String.format("%n\tOBJECT KEY:"));
        describeOctetSeq(sb, "\t\t", in);
    }

    private static void describeTargetAddress(StringBuilder sb, InputStream in) {
        short disposition = in.read_short();
        switch (disposition) {
            case 0:
                sb.append(String.format("%n\tKEY ADDRESS:"));
                describeOctetSeq(sb,"\t\t", in);
                return;
            case 1:
                sb.append(String.format("%n\tPROFILE ADDRESS:"));
                describeTaggedProfile(sb,"\t\t", in);
                return;
            case 2:
                sb.append(String.format("%n\tREFERENCE ADDRESS: INDEX = %d", in.read_long()));
                describeIor(sb,"\t\t", in);
                return;
            default:
                sb.append(String.format("%n\tERROR: TargetAddress has unknown disposition 0x%04x", disposition));
        }
    }

    private static void describeServiceContextList(StringBuilder sb, InputStream in) {
        int len = in.read_long();
        for (int i = 1; i <= len; i++) {
            sb.append(EOL).append("\t").append("SERVICE CONTEXT ").append(i).append(" OF ").append(len);
            describeServiceContext(sb, in);
        }
    }

    private static void describeServiceContext(StringBuilder sb, InputStream in) {
        sb.append(" TAG = ");
        describeServiceContextId(sb, in.read_long());
        describeOctetSeq(sb, "\t\t", in);
    }

    private static void describeServiceContextId(StringBuilder sb, int tag) {
        sb.append(String.format("0x%08x (%s)", tag, ServiceContextTag.valueOf(tag)));
    }

    private static void describeOctetSeq(StringBuilder sb, String indent, InputStream in) {
        int len = in.read_long();
        sb.append(String.format(" [0x%08x octets]", len));
        if (len == 0) return;
        sb.append(EOL);
        if (len > in.available()) {
            sb.append(indent).append("ERROR reading octet sequence of length ").append(len);
            sb.append(" when only ").append(in.available()).append(" bytes are available in the buffer");
            return;
        }
        try {
            // dump length and data as hex
            in.getBuffer().dumpSomeData(sb, indent, len);
        } finally {
            // wind past the data
            in._OB_skip(len);
        }
    }

    private static void describeTaggedProfile(StringBuilder sb, String indent, InputStream in) {
        sb.append(String.format("%n%sID = 0x%08x", indent, in.read_long()));
        describeOctetSeq(sb, indent, in);
    }

    private static void describeTaggedProfileSeq(StringBuilder sb, String indent, InputStream in) {
        int len = in.read_long();
        for (int i = 1; i <= len; i++) {
            sb.append(indent);
            sb.append("TAGGED PROFILE ");
            sb.append(i);
            sb.append(" OF ");
            sb.append(len);
            sb.append(EOL);
            describeTaggedProfile(sb, indent + "\t", in);
        }
    }

    private static void describeIor(StringBuilder sb, String indent, InputStream in) {
        // struct IOR { string type_id; TaggedProfileSeq profiles; }
        sb.append(String.format("%n%sTYPE ID:", indent));
        describeOctetSeq(sb,indent + "\t", in);
        describeTaggedProfileSeq(sb, indent, in);
    }

    private static void describeReqId(StringBuilder sb, InputStream in) {
        sb.append(String.format("%n\tREQUEST ID = %d", in.read_long()));
    }

    enum StringField {
        OPERATION;
        private void describeString(StringBuilder sb, String indent, InputStream in) {
            int start = in.getBuffer().getPosition();
            try {
                String value = in.read_string();
                sb.append(String.format("%n%s%s = %s", indent, this, value));
            } catch (MARSHAL m) {
                int pos = in.getBuffer().getPosition();
                sb.append(String.format("%n%s%s: error reading string at 0x%h, failed at 0x%h%n", indent, this, start, pos));
            }
        }
    }

    public static MessageType valueOf(int i) {
        switch (i) {
            case _Request: return REQUEST;
            case _Reply: return REPLY;
            case _CancelRequest: return CANCEL_REQUEST;
            case _LocateRequest: return LOCATE_REQUEST;
            case _LocateReply: return LOCATE_REPLY;
            case _CloseConnection: return CLOSE_CONNECTION;
            case _MessageError: return MESSAGE_ERROR;
            case _Fragment: return FRAGMENT;
            default: return UNKNOWN;
        }
    }
}

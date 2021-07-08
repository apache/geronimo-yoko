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
package org.apache.yoko.giop;

import org.apache.yoko.io.Buffer;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.orb.OCI.GiopVersion;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static org.apache.yoko.logging.VerboseLogging.DATA_IN_LOG;
import static org.apache.yoko.logging.VerboseLogging.DATA_OUT_LOG;
import static org.apache.yoko.orb.OCI.GiopVersion.GIOP1_0;
import static org.apache.yoko.orb.OCI.GiopVersion.GIOP1_2;
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
        String describeHeader(ReadBuffer rb, GiopVersion version, boolean littleEndian) {
            return describeReqId(rb, littleEndian);
            // TODO: parse request header
        }
    },
    REPLY(_Reply){
        String describeHeader(ReadBuffer rb, GiopVersion version, boolean littleEndian) {
            return describeReqId(rb, littleEndian);
            // TODO: parse reply header
        }
    },
    CANCEL_REQUEST(_CancelRequest){
        String describeHeader(ReadBuffer rb, GiopVersion version, boolean littleEndian) {
            return describeReqId(rb, littleEndian);
        }
    },
    LOCATE_REQUEST(_LocateRequest){
        String describeHeader(ReadBuffer rb, GiopVersion version, boolean littleEndian) {
            return describeReqId(rb, littleEndian) + EOL + getTarget(rb, version, littleEndian);
        }
        String getTarget(ReadBuffer rb, GiopVersion version, boolean littleEndian) {
            switch (version) {
                case GIOP1_0:
                case GIOP1_1:
                    // print out IOP::ObjectKey
                    return String.format("OBJECT KEY: %n%s", describeOctetSeq("\t\t", rb, littleEndian));
                case GIOP1_2:
                    // print out TargetAddress
                    short discriminator = rb.readShort(littleEndian);
                    switch (discriminator) {
                        case 0: return "KEY ADDR: \n" + describeOctetSeq("\t\t", rb, littleEndian);
                        case 1: return String.format("PROFILE ADDR: %n%s", describeTaggedProfile("\t\t", rb, littleEndian));
                        case 2: return String.format("REFERENCE ADDR: INDEX = %d%n%s", rb.readInt(littleEndian), describeIor("\t\t", rb, littleEndian));
                        default: return "error: TargetAddress has unknown discriminator " + discriminator;
                    }
            }
            return "unknown GIOP version " + version;
        }
    },
    LOCATE_REPLY(_LocateReply){
        String describeHeader(ReadBuffer rb, GiopVersion version, boolean littleEndian) {
            return String.format("%s%n\t%s", describeReqId(rb, littleEndian), getLocateStatus(rb, littleEndian));
        }
        String getLocateStatus(ReadBuffer rb, boolean littleEndian) {
            int locStat = rb.readInt(littleEndian);
            switch (locStat) {
                case 0: return "LOCATE STATUS = UNKNOWN_OBJECT";
                case 2: return "LOCATE STATUS = OBJECT_FORWARD";
                case 3: return "LOCATE STATUS = OBJECT_FORWARD_PERM";
                case 4: return "LOCATE STATUS = LOC_SYSTEM_EXCEPTION";
                case 5: return "LOCATE STATUS = LOC_NEEDS_ADDRESSING_MODE";
                default: return String.format("error parsing locate status flag: 0x%08x", locStat);
            }
        }
    },
    CLOSE_CONNECTION(_CloseConnection),
    MESSAGE_ERROR(_MessageError),
    FRAGMENT(_Fragment) {
        String describeHeader(ReadBuffer rb, GiopVersion version, boolean littleEndian) {
            return version == GIOP1_2 ? describeReqId(rb, littleEndian) : "";
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
        if (logger.isLoggable(FINEST)) logger.finest(direction + describeMessage(buffer) + "\n" + buffer.dumpAllData());
        else if (logger.isLoggable(FINE)) logger.fine(direction + describeMessage(buffer));
    }

    private static String describeMessage(Buffer<?> buffer) {
        try {
            ReadBuffer rb = buffer.newReadBuffer();
            rb.skipBytes(4);
            byte major = rb.readByte();
            byte minor = rb.readByte();
            GiopVersion version = GiopVersion.get(major, minor);
            byte flags = rb.readByte();
            boolean littleEndian = (flags & LITTLE_ENDIAN_FLAG) == LITTLE_ENDIAN_FLAG;
            MessageType type = valueOf(rb.readByte());
            final int size = rb.readInt(littleEndian);
            return type.describeGiopHeader(major, minor, version, flags, size) + type.describeHeader(rb, version, littleEndian);
        } catch (Throwable t) {
            return "describeMessage() failed with " + t + EOL + Arrays.stream(t.getStackTrace()).map(e -> "\t" + e).collect(Collectors.joining(EOL));
        }
    }

    private String describeGiopHeader(byte major, byte minor, GiopVersion version, byte flags, int size) {
        String result = String.format("GIOP %d.%d %s MESSAGE%n\tSIZE=%d", major, minor, this, size);
        if (fragmentable && version != GIOP1_0)
            result += String.format("%n\tFRAGMENT_TO_FOLLOW = %s", ((flags & FRAG_FLAG) == FRAG_FLAG));
        return result;
    }

    String describeHeader(ReadBuffer rb, GiopVersion version, boolean littleEndian) { return ""; }

    private static String describeOctetSeq(String indent, ReadBuffer rb, boolean littleEndian) {
        int len = rb.readInt(littleEndian);
        if (len > rb.available()) return indent + "error reading octet sequence of length " + len + " when only " + rb.available() + " bytes are available in the buffer";
        return rb.dumpSomeData(indent, len);
    }

    private static String describeTaggedProfile(String indent, ReadBuffer rb, boolean littleEndian) {
        return String.format("%n%sID = 0x%08x%n%s", indent, rb.readInt(littleEndian), describeOctetSeq(indent, rb, littleEndian));
    }

    private static String describeTaggedProfileSeq(String indent, ReadBuffer rb, boolean littleEndian) {
        int len = rb.readInt(littleEndian);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb
                .append(indent)
                .append("TAGGED PROFILE ")
                .append(i+1)
                .append(" OF ")
                .append(len)
                .append(EOL)
                .append(describeTaggedProfile(indent + "\t", rb, littleEndian));
        return sb.toString();
    }

    private static String describeIor(String indent, ReadBuffer rb, boolean littleEndian) {
        // struct IOR { string type_id; TaggedProfileSeq profiles; }
        return String.format("%sTYPE ID:%n%s%n%s", indent, describeOctetSeq(indent + "\t", rb, littleEndian), describeTaggedProfileSeq(indent, rb, littleEndian));
    }

    private static String describeReqId(ReadBuffer rb, boolean littleEndian) {
        return String.format("%n\tREQUEST ID = %d", rb.readInt(littleEndian));
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

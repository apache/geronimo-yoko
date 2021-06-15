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

import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static org.apache.yoko.logging.VerboseLogging.DATA_IN_LOG;
import static org.apache.yoko.logging.VerboseLogging.DATA_OUT_LOG;
import static org.omg.GIOP.MsgType_1_1._CancelRequest;
import static org.omg.GIOP.MsgType_1_1._CloseConnection;
import static org.omg.GIOP.MsgType_1_1._Fragment;
import static org.omg.GIOP.MsgType_1_1._LocateReply;
import static org.omg.GIOP.MsgType_1_1._LocateRequest;
import static org.omg.GIOP.MsgType_1_1._MessageError;
import static org.omg.GIOP.MsgType_1_1._Reply;
import static org.omg.GIOP.MsgType_1_1._Request;

public enum MessageType {
    REQUEST(_Request),
    REPLY(_Reply),
    CANCEL_REQUEST(_CancelRequest),
    LOCATE_REQUEST(_LocateRequest),
    LOCATE_REPLY(_LocateReply),
    CLOSE_CONNECTION(_CloseConnection),
    MESSAGE_ERROR(_MessageError),
    FRAGMENT(_Fragment),
    UNKNOWN;
    public final int id;
    public final boolean fragmentable;

    MessageType(int id) {
        this.id = id;
        this.fragmentable = id == _Request || id == _Reply;
    }

    MessageType() { this(-1); }

    private static String describeMessage(Buffer<?> buffer) {
        ReadBuffer rb = buffer.newReadBuffer();
        rb.skipBytes(4);
        byte major = rb.readByte();
        byte minor = rb.readByte();
        byte flags = rb.readByte();
        boolean fragmentToFollow = (major > 1 || minor > 0) && ((flags & 2) == 2);
        boolean littleEndian = flags % 2 == 1;
        MessageType type = valueOf(rb.readByte());
        final int size = littleEndian ? rb.readInt_LE() : rb.readInt();
        final String reqId = type.expectRequestIdAtIndex12(major, minor) ?
                ", REQUEST_ID=" + (littleEndian ? rb.readInt_LE() : rb.readInt()) :
                "";
        final String frag = type.fragmentable ? ", FRAGMENT_TO_FOLLOW=" + fragmentToFollow : "";
        return "GIOP " + major + "." + minor + " " + type + " MESSAGE, SIZE=" + size + reqId + frag;
    }

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

    private boolean expectRequestIdAtIndex12(byte major, byte minor) {
        switch (id) {
            case _Request:
            case _Reply:
            case _Fragment:
                return minor > 1 || major > 1;
            case _CancelRequest:
            case _LocateRequest:
            case _LocateReply:
                return true;
            case _CloseConnection:
            case _MessageError:
            default:
                return false;
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

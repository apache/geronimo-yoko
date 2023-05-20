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
package org.apache.yoko.orb.OCI.IIOP;

import org.omg.CORBA.INTERNAL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.apache.yoko.util.Hex.formatHexPara;
import static org.apache.yoko.orb.OCI.IIOP.FragmentingPump.Flag.FRAGMENT_TO_FOLLOW;
import static org.apache.yoko.orb.OCI.IIOP.FragmentingPump.Flag.LITTLE_ENDIAN;
import static org.apache.yoko.orb.OCI.IIOP.FragmentingPump.MsgType.CLOSE_CONNECTION;
import static org.apache.yoko.orb.OCI.IIOP.FragmentingPump.MsgType.FRAGMENT;
import static org.apache.yoko.orb.OCI.IIOP.FragmentingPump.MsgType.msgType;

/**
 *
 */
class FragmentingPump {

    enum Flag {
        LITTLE_ENDIAN(1),
        FRAGMENT_TO_FOLLOW(2);
        private final int mask;
        Flag(int mask) {this.mask = mask;}
        boolean isSet(byte[] header) { return (header[6] & mask) != 0; }
        void set(byte[] header) { header[6] |= mask; }
        void unset(byte[] header) { header[6] &= ~mask; }
        void set(byte[] header, boolean on) { if (on) this.set(header); else this.unset(header);}
    }

    enum MsgType {
        REQUEST(0, true),
        REPLY(1, true),
        CANCEL_REQUEST(2),
        LOCATE_REQUEST(3),
        LOCATE_REPLY(4),
        CLOSE_CONNECTION(5),
        MESSAGE_ERROR(6),
        FRAGMENT(7);
        final int id;
        final boolean fragmentable;
        MsgType(int id) { this(id, false); }
        MsgType(int id, boolean fragmentable) { this.id = id; this.fragmentable = fragmentable; }
        static MsgType msgType(byte[] header) {
            final byte octet = header[7];
            MsgType result = values()[octet];
            assert octet == result.id;
            return result;
        }
        public void setType(byte[] hdr) { hdr[7] = (byte)id; }
    }

    /** The Length of a GIOP header is defined by CORBA 2.3.1, section 15.4.1 */
    private static final int GIOP_HDR_LEN = 12;
    private static final int MSG_ID_LEN = Integer.BYTES;
    private final int maxMessageSize;
    private final int maxGiopLen;
    private final int maxFragBody;

    final Socket src;
    final Socket dst;
    volatile boolean connectionClosing;

    FragmentingPump(Socket src, Socket dst, int maxMessageSize) {
        if (maxMessageSize % 8 != 0) throw new IllegalArgumentException("Maximum message size must fall on an 8-byte boundary.");
        if (maxMessageSize <= GIOP_HDR_LEN + MSG_ID_LEN) throw new IllegalArgumentException("Maximum message size must be larger than " + (GIOP_HDR_LEN + MSG_ID_LEN));
        this.maxMessageSize = maxMessageSize;
        this.maxGiopLen = maxMessageSize - GIOP_HDR_LEN;
        this.maxFragBody = maxGiopLen - MSG_ID_LEN;
        this.src = src;
        this.dst = dst;
    }

    void pumpForward() { pump(" ---> ", getIn(src), getOut(dst)); }

    void pumpReturn() { pump(" <--- ", getIn(dst), getOut(src)); }

    private static InputStream getIn(Socket sock) {
        try { return sock.getInputStream(); }
        catch (Exception e) { throw (INTERNAL)new INTERNAL().initCause(e); }
    }

    private static OutputStream getOut(Socket sock) {
        try { return sock.getOutputStream(); }
        catch (Exception e) { throw (INTERNAL)new INTERNAL().initCause(e); }
    }

    private void pump(String prefix, InputStream in, OutputStream out) {
        try {
            while (!!!connectionClosing) {
                byte[] hdr = readBytes(in, GIOP_HDR_LEN);
                if (hdr == null) {
                    System.out.println(prefix + " stream closed");
                    break;
                }
                System.out.println(prefix + " read GIOP HEADER: " + format(hdr));
                validate(hdr);
                final MsgType type = msgType(hdr);
                if (msgType(hdr) == CLOSE_CONNECTION) connectionClosing = true;
                System.out.printf("Read in GIOP header for GIOP %d.%d %s message with %d octet body.%n", major(hdr), minor(hdr), type, readLength(hdr));
                if (readLength(hdr) == 0) {
                    out.write(hdr);
                    System.out.println("Wrote out GIOP message of 12 octets.");
                    out.flush();
                    continue;
                }
                byte[] body = readBytes(in, readLength(hdr));
                if (needsFragmenting(hdr))
                    sendAsFragments(out, hdr, body);
                else
                    sendAsWholeMessage(out, hdr, body);
            }
        } catch (Exception e) {
            throw (INTERNAL)new INTERNAL().initCause(e);
        }
    }

    private static void validate(byte[] hdr) {
        assert hdr[0] == 'G'; // It's a
        assert hdr[1] == 'I'; // kind
        assert hdr[2] == 'O'; // of
        assert hdr[3] == 'P'; // magic

        assert major(hdr) == 1;
        assert minor(hdr) == 0 || minor(hdr) == 2;
        assert !!! LITTLE_ENDIAN.isSet(hdr); // not expecting anything little-endian
        assert !!! FRAGMENT_TO_FOLLOW.isSet(hdr); // not expecting anything already fragmented
        assert msgType(hdr) != null; // this will actually throw an exception if the message type isn't valid
    }

    private boolean needsFragmenting(byte[] header) {
        return (minor(header) >= 2) // Only fragment GIOP 1.2 and above messages
                    && msgType(header).fragmentable // Only fragment requests and replies
                    && (readLength(header) > maxGiopLen); // Only fragment BIG messages
    }

    private static void sendAsWholeMessage(OutputStream out, byte[] hdr, byte[] body) throws IOException {
        sendMessage(out, hdr, body, 0, body.length);
    }

    private void sendAsFragments(OutputStream out, byte[] hdr, byte[] body) throws IOException {
        for (int off = MSG_ID_LEN; off < body.length; off += maxFragBody) {
            int len = Math.min(maxFragBody, body.length - off);
            sendMessage(out, hdr, body, off, len);
        }
    }

    /**
     * Sends a message using the supplied header and body data.
     * Automatically determines the correct length for the message,
     * as well as the message type and the fragment to follow flag.
     * <p/>
     * Assumes that the body provided is the whole of a GIOP message body,
     * and that an invocation to send part of a message is a valid
     * fragmentation of the original message.
     * <p/>
     * Only supports GIOP 1.2 style fragmentation. GIOP 1.1 fragmentation is bad, mmm'kay?
     * <p/>
     * Automatically inserts the request ID in any fragmented message.
     * <p/>
     * It is the caller's responsibility to ensure this method is called
     * enough times, and with the right sequence of offsets and lengths
     * to correctly fragment a message.
     *
     * @param out the OutputStream to which the message is to be written
     * @param hdr the header bytes from the original message (these may be modified but are safe to reuse for all calls for sending a single logical message as fragments)
     * @param body the original message body in its entirety
     * @param off the offset into the body to be transmitted:
     *            <ul>
     *            <li>if off is zero, len must be equal to body.length, and this indicates the message is not being fragmented</li>
     *            <li>if off is greater than 0, it must fall on an 8-byte boundary within the original entire GIOP message,
     *                i.e. off + GIOP_HDR_LEN must be divisible by 8</li>
     *            </ul>
     * @param len the number of bytes from the original message body to transmit
     *            if this is not the final fragment or an entire message, len must be divisible by 8 to preserve 8-byte boundaries across fragments
     *
     */
    private static void sendMessage(OutputStream out, byte[] hdr, byte[] body, int off, int len) throws IOException {
        if (hdr.length != GIOP_HDR_LEN) throw new IllegalArgumentException("The header must be exactly " + GIOP_HDR_LEN + " bytes long.");
        final boolean isFragmentMsg = off > MSG_ID_LEN;
        final boolean sendRequestId = off > 0;
        final boolean fragmentToFollow = off + len < body.length;
        final int alignment = (off + GIOP_HDR_LEN) % 8;
        if (off == 0) {
            if (len < body.length) throw new IllegalArgumentException("If offset is zero, len  must equal body.length and the entire message must be transmitted.");
        } else {
            if (alignment != 0) throw new IllegalArgumentException("Fragments must start on an 8-byte boundary");
            if (fragmentToFollow) {
                if (len % 8 != 0) throw new IllegalArgumentException("Non-final fragments must end on an 8-byte boundary");
            }
        }
        int giopLen = sendRequestId ? len + MSG_ID_LEN : len;
        StringBuilder sb = new StringBuilder();
        // always write the computed length into the header (even though this may not change anything)
        writeLength(hdr, giopLen);
        // if we aren't sending the whole buffer, we will be sending more later
        FRAGMENT_TO_FOLLOW.set(hdr, fragmentToFollow);
        // if we aren't starting at the beginning of the body, this is a fragment
        if (isFragmentMsg) FRAGMENT.setType(hdr);
        System.out.println("About to write GIOP HEADER: " + format(hdr));
        out.write(hdr);
        formatHexPara(hdr, sb);
        // we might need to write out the request id separately
        if (sendRequestId) {
            out.write(body, 0, MSG_ID_LEN);
            formatHexPara(body, 0, MSG_ID_LEN, sb);
        }
        // now write the body
        out.write(body, off, len);
        formatHexPara(body, off, len, sb);
        System.out.printf("  %08x%n", len);
        System.out.printf("Wrote out %s message of %d octets.%n", msgType(hdr), hdr.length + giopLen);
        System.out.println("### LEN = " + len);
    }

    private static byte[] readBytes(InputStream in, int len) throws IOException {
        byte[] buf = new byte[len];
        int read = in.read(buf);
        if (read == -1) return null;
        for (int off = read; off < len; off += in.read(buf, off, len - off));
        System.out.printf("Read in %d octets.%n", len);
        return buf;
    }

    private static String format(byte[] hdr) {
        int i = 0;
        return String.format("%02x%02x%02x%02x %02x%02x%02x%02x %02x%02x%02x%02x%n",
                hdr[i++], hdr[i++], hdr[i++], hdr[i++],
                hdr[i++], hdr[i++], hdr[i++], hdr[i++],
                hdr[i++], hdr[i++], hdr[i++], hdr[i++]);
    }

    private static byte major(byte[] hdr) { return hdr[4]; }

    private static byte minor(byte[] hdr) { return hdr[5]; }

    private static int readLength(byte[] header) {
        int len = 0;
        // sound the octal alert!!!
        len += ((header[010] & 0xFF) << 030); //System.out.printf("  %08x%n", len);
        len += ((header[011] & 0xFF) << 020); //System.out.printf("  %08x%n", len);
        len += ((header[012] & 0xFF) << 010); //System.out.printf("  %08x%n", len);
        len += ((header[013] & 0xFF) << 000); //System.out.printf("  %08x%n", len);
        return len;
    }

    private static void writeLength(byte[] header, int length) {
        // sound the octal alert!!!
        header[010] = (byte) (length >>> 030);
        header[011] = (byte) (length >>> 020);
        header[012] = (byte) (length >>> 010);
        header[013] = (byte) (length >>> 000);
        assert readLength(header) == length;
    }
}

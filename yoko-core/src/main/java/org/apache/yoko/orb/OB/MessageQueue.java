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
import org.apache.yoko.orb.OCI.Buffer;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.omg.CORBA.SystemException;

import java.util.Vector;

import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;
import static org.omg.GIOP.MsgType_1_1.LocateRequest;
import static org.omg.GIOP.MsgType_1_1.Request;

public class MessageQueue {
    // Unsent requests
    private final Vector<UnsentMessage> unsent_ = new Vector<>();

    // Requests that have been sent but are waiting for replies.
    private final Vector<Downcall> pending_ = new Vector<>();

    // Add new unsent buffer
    public void add(ORBInstance orbInstance, Buffer buf) {
        // Add new message to the message buffers
        unsent_.addElement(new UnsentMessage(buf));
    }

    // Add new unsent downcall
    public void add(ORBInstance orbInstance, Downcall down) {
        // Add the message header
        try {
            // Save stream position and rewind
            OutputStream out = down.output();

            int pos = out._OB_pos();
            out._OB_pos(0);

            // Write message header
            ProfileInfo profileInfo = down.profileInfo();
            GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance, out, profileInfo);

            // Write protocol header
            if ("_locate".equals(down.operation()))
                outgoing.writeMessageHeader(LocateRequest, false, pos - 12);
            else
                outgoing.writeMessageHeader(Request, false, pos - 12);

            // Restore stream position
            out._OB_pos(pos);
        } catch (SystemException ex) {
            _OB_assert(ex.completed == COMPLETED_NO);
            down.setFailureException(ex);
            return;
        }
        unsent_.addElement(new UnsentMessage(down));
    }

    // retrieve the first buffer in the queue
    public Buffer getFirstUnsentBuffer() {
        return unsent_.isEmpty() ? null : unsent_.firstElement().buf;
    }

    // Move the first unsent downcall to the list of pending downcalls
    public Downcall moveFirstUnsentToPending() {
        if (unsent_.isEmpty()) return null;

        // Remove first downcall
        UnsentMessage m = unsent_.firstElement();
        Downcall down = m.down;
        unsent_.removeElementAt(0);

        // Only add to pending if a response is expected
        if (down != null) {
            if (down.responseExpected()) {
                down.setPending();
                pending_.addElement(down);
            } else {
                down.setNoException(null);
            }
        }
        return down;
    }

    // Find and remove a pending downcall
    public Downcall findAndRemovePending(int reqId) {
        for (int i = 0; i < pending_.size(); i++) {
            Downcall d = pending_.elementAt(i);
            if (d.requestId() == reqId) {
                pending_.removeElementAt(i);
                return d;
            }
        }
        return null;
    }

    // Change the state of the queue due to an exception. Sets the
    // state of all unsent and pending downcalls.
    public void setException(int state, SystemException ex, boolean notCompleted) {
        // Always use a completion status of NO for unsent requests.
        while (!unsent_.isEmpty()) {
            UnsentMessage m = unsent_.firstElement();
            if (m.down != null) m.down.setFailureException(ex);
            unsent_.removeElementAt(0);
        }

        SystemException except = Util.copySystemException(ex);
        except.completed = notCompleted ? COMPLETED_NO : COMPLETED_MAYBE;
        while (!pending_.isEmpty()) {
            Downcall down = pending_.firstElement();
            down.setFailureException(except);
            pending_.removeElementAt(0);
        }
    }

    // check if we have any unsent messages in the queue
    public boolean hasUnsent() {
        return !unsent_.isEmpty();
    }
}

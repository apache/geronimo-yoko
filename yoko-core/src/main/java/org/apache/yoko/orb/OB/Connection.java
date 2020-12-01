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

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.logging.Logger.getLogger;
import static org.apache.yoko.orb.OB.Connection.Access.CLOSE;
import static org.apache.yoko.orb.OB.Connection.Access.READ;
import static org.apache.yoko.orb.OB.Connection.Access.WRITE;
import static org.apache.yoko.orb.OB.Connection.Flag.CLOSING_LOGGED;
import static org.apache.yoko.util.CollectionExtras.readOnlyEnumSet;

abstract class Connection {
    private final static Logger logger = getLogger(Connection.class.getName());

    enum Access { READ, WRITE, CLOSE }

    enum State {
        ACTIVE(READ, WRITE) {
            void applyTo(Connection conn) {
                conn.start();
                conn.refresh();
            }
        },
        HOLDING(WRITE) {
            void applyTo(Connection conn) {
                conn.pause();
            }
        },
        CLOSING(READ, WRITE, CLOSE) {
            void applyTo(Connection conn) {
                // gracefully shutdown by sending off pending messages,
                // reading any messages left on the wire and then closing
                conn.gracefulShutdown();
                conn.refresh();
            }
        },
        ERROR(CLOSE) {
            void applyTo(Connection conn) {
                conn.abortiveShutdown();
                conn.markDestroyed();
                conn.refresh();
            }
        },
        CLOSED() {
            void applyTo(Connection conn) {
                conn.close();
                conn.markDestroyed();
                conn.refresh();
            }
        },
        STALE() {
            void applyTo(Connection conn) { }
        };

        private final Set<Access> permissions;

        private static final Map<State, Set<?>> ALLOWED_TRANSITIONS;
        static {
            // REMEMBER: in Enums, class initialisation runs AFTER all the instance constructors
            Map<State, Set<?>> map = new EnumMap<>(State.class);
            ALLOWED_TRANSITIONS = unmodifiableMap(map);
            map.put(ACTIVE, readOnlyEnumSet(HOLDING, CLOSING, ERROR, CLOSED));
            map.put(HOLDING, readOnlyEnumSet(ACTIVE, CLOSING, ERROR, CLOSED));
            map.put(CLOSING, readOnlyEnumSet(ERROR, CLOSED));
            map.put(ERROR, readOnlyEnumSet(CLOSED));
            map.put(CLOSED, readOnlyEnumSet(STALE));
            map.put(STALE, Collections.EMPTY_SET);
        }

        State() { this.permissions = Collections.EMPTY_SET; }

        State(Access...permissions) {
            // EnumSet.of() requires an initial element, but it's ok to add the element twice
            this.permissions = unmodifiableSet(EnumSet.of(permissions[0], permissions));
        }

        final boolean cannotTransitionTo(State next) { return !!!canGoTo(next); }
        private boolean canGoTo(State next) { return ALLOWED_TRANSITIONS.get(this).contains(next); }
        final boolean forbids(Access op) { return !!!permissions.contains(op); }
        final boolean isClosed() {return this == CLOSED || this == STALE; }

        abstract void applyTo(Connection conn);
    }

    interface Flag {
        int REQUEST_SENT = 1;
        int DESTROYED = 4;
        int REPLY_SENT = 2;
        int OUTBOUND = 8;
        int CLIENT_ENABLED = 16;
        int SERVER_ENABLED = 32;
        int CLOSING_LOGGED = 64;
    }

    private State state;
    private int flags = 0;

    Connection(State initialState) { this.state = initialState; }

    synchronized State getState() { return state; }

    boolean setState(State newState) {
        synchronized (this) {
            if (state.cannotTransitionTo(newState)) {
                logger.fine("No state change from " + state + " to "  + newState);
                return false;
            }
            state = newState;
        }
        state.applyTo(this);
        return true;
    }

    final synchronized boolean isRequestSent() { return (flags & Flag.REQUEST_SENT) != 0; }
    final synchronized boolean isDestroyed() { return (flags & Flag.DESTROYED) != 0; }
    final synchronized boolean isReplySent() { return (flags & Flag.REPLY_SENT) != 0; }
    final synchronized boolean isOutbound() { return (flags & Flag.OUTBOUND) != 0; }
    final synchronized boolean isClientEnabled() { return (flags & Flag.CLIENT_ENABLED) != 0; }
    final synchronized boolean isServerEnabled() { return (flags & Flag.SERVER_ENABLED) != 0; }
    final synchronized void markRequestSent() { flags |= Flag.REQUEST_SENT; }
    final synchronized void markDestroyed() { flags |= Flag.DESTROYED; }
    final synchronized void markOutbound() { flags |= Flag.OUTBOUND; }
    final synchronized void markClientEnabled() { flags |= Flag.CLIENT_ENABLED; }
    final synchronized void markServerEnabled() { flags |= Flag.SERVER_ENABLED; }
    final synchronized boolean markClosingLogged() { try { return (flags & CLOSING_LOGGED) == 0; } finally { flags |= CLOSING_LOGGED; } }

    /** callback method when the ACM signals a timeout */
    abstract void ACM_callback();
    /** activate the connection */
    abstract void start();
    /** refresh the connection status after a change in internal state */
    abstract void refresh();
    /** tell the connection to stop processing; resumable with a refresh() */
    abstract void pause();
    abstract void gracefulShutdown();
    abstract void abortiveShutdown();
    abstract void close();
}

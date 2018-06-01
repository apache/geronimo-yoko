package org.apache.yoko.rmi.impl;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class CustomMarshaledObjectReader extends DelegatingObjectReader {
    private enum State {
        UNINITIALISED, BEFORE_CUSTOM_DATA, IN_CUSTOM_DATA, CLOSED;
        private static final Map<State, Set<State>> TRANSITIONS;
        
        static {
            EnumMap<State, Set<State>> transition = new EnumMap<>(State.class);
            allow(transition, from(UNINITIALISED), to(BEFORE_CUSTOM_DATA));
            allow(transition, from(BEFORE_CUSTOM_DATA), to(IN_CUSTOM_DATA, CLOSED));
            allow(transition, from(IN_CUSTOM_DATA), to(CLOSED));
            allow(transition, from(CLOSED),         to(CLOSED));
            TRANSITIONS = Collections.unmodifiableMap(transition);
        }
        
        private static void allow(Map<State, Set<State>> map, State from, Set<State> to) {
            map.put(from, to);
        }
        
        private static State from(State state) { return state; }
        
        private static Set<State> to(State state, State...states) {
            return Collections.unmodifiableSet(EnumSet.of(state, states));
        }
        
        void checkStateTransition(State newState) {
            if (TRANSITIONS.get(this).contains(newState))
                return;
            throw new INTERNAL("Unexpected state transition from " + this + " to " + newState);
        }
    }

    private final ObjectReader objectReader;
    private State state = State.UNINITIALISED;

    public static ObjectReader wrap(final ObjectReader delegate) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<ObjectReader>() {
                public ObjectReader run() throws IOException {
                    return new CustomMarshaledObjectReader(delegate);
                }
            });
        } catch (PrivilegedActionException pae) {
            throw (IOException) pae.getCause();
        }
    }

    private CustomMarshaledObjectReader(ObjectReader delegate) throws IOException {
        this.objectReader = delegate;
        setState(State.BEFORE_CUSTOM_DATA);
    }

    private State setState(final State newState) throws IOException {
        state.checkStateTransition(newState);
        try {
            return state;
        } finally {
            state = newState;
            switch(newState) {
                case UNINITIALISED:
                    throw new IllegalStateException();
                case BEFORE_CUSTOM_DATA:
                    delegateTo(getDefaultWriteObjectReader(objectReader));
                    break;
                case IN_CUSTOM_DATA:
                    delegateTo(objectReader);
                    break;
                case CLOSED:
                    delegateTo(ClosedObjectReader.INSTANCE);
                    break;
            }
        }
    }

    private ObjectReader getDefaultWriteObjectReader(final ObjectReader delegate) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<ObjectReader>() {
                public ObjectReader run() throws IOException {
                    return new DefaultWriteObjectReader(delegate);
                }
            });
        } catch (PrivilegedActionException pae) {
            throw (IOException) pae.getCause();
        }
    }

    private void readCustomRMIValue() throws IOException {
        setState(State.IN_CUSTOM_DATA);
        objectReader._startValue();
    }

    public void close() throws IOException {
        switch (setState(State.CLOSED)) {
            case UNINITIALISED:
                throw new IllegalStateException();
            case BEFORE_CUSTOM_DATA :
                objectReader.readValueObject();
                break;
            case IN_CUSTOM_DATA :
                objectReader._endValue();
                break;
            case CLOSED :
                // nothing to do here
                break;
        }
    }

    @Override
    protected final Object readObjectOverride() throws ClassNotFoundException, IOException {
        return super.readObjectOverride0();
    }

    /**
     * This class handles reading the defaultWriteObject() data,
     * and prepares its outer instance when the custom data is
     * first read.
     */
    private final class DefaultWriteObjectReader extends DelegatingObjectReaderWithBeforeReadHook {

        private boolean allowDefaultRead = true;

        private DefaultWriteObjectReader(ObjectReader delegate) throws IOException {
            super(delegate);
        }

        @Override
        public void defaultReadObject() throws IOException, ClassNotFoundException {
            if (allowDefaultRead) {
                allowDefaultRead = false;
                CustomMarshaledObjectReader.this.objectReader.defaultReadObject();
            } else {
                throw new IllegalStateException("defaultReadObject() or readFields() must not be called more than once");
            }
        }

        @Override
        public GetField readFields() throws IOException ,ClassNotFoundException {
            if (allowDefaultRead) {
                allowDefaultRead = false;
                return CustomMarshaledObjectReader.this.objectReader.readFields();
            } else {
                throw new IllegalStateException("readFields() or defaultReadObject() must not be called more than once");
            }
        };

        @Override
        void beforeRead() {
            try {
                CustomMarshaledObjectReader.this.readCustomRMIValue();
            } catch (IOException e) {
                throw (MARSHAL)new MARSHAL(e.toString()).initCause(e);
            }
        }
    }
}
